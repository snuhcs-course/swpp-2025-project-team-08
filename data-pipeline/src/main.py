import json
import os
import argparse

from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List
from urllib.parse import quote_plus

from dotenv import load_dotenv
from tqdm import tqdm

from graph import graph
from loader import BokjiroLoader, Subsidy24Loader
from utils.errors import MismatchError
from vectorizer import Vectorizer
from database.manager import PostgresManager

load_dotenv()

DB_HOST = os.getenv("DB_HOST")
DB_PORT = os.getenv("DB_PORT")
DB_NAME = os.getenv("DB_NAME")
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DATABASE_URL = f"postgresql://{quote_plus(DB_USER)}:{quote_plus(DB_PASSWORD)}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

ts = datetime.now(timezone.utc).strftime("%y%m%dt%h%m%sz")

project_root = Path(__file__).resolve().parents[1]
data_dir = project_root / "data"
data_dir.mkdir(parents=True, exist_ok=True)

raw_path = data_dir / "raw_programs.jsonl"
raw_path_ts = data_dir / f"raw_programs_{ts}.jsonl"
trimmed_path = data_dir / "trimmed_programs.jsonl"
trimmed_path_ts = data_dir / f"trimmed_programs_{ts}.jsonl"
embedding_path = data_dir / "embeddings.jsonl"
embedding_path_ts = data_dir / f"embeddings_{ts}.jsonl"
save_failure_path = data_dir / f"save_failures_{ts}.jsonl"


def create_parser():
    parser = argparse.ArgumentParser(description="data pipeline for ITDA")

    parser.add_argument(
        "mode",
        choices=["load", "trim", "vectorize", "save", "all"],
    )

    parser.add_argument("--load-max-page-bokjiro", type=int, default=1)
    parser.add_argument("--load-max-page-subsidy24", type=int, default=1)
    parser.add_argument("--vectorize-batch-size", type=int, default=32)
    parser.add_argument("--db-commit-batch-size", type=int, default=32)
    parser.add_argument("--db-min-pool-size", type=int, default=1)
    parser.add_argument("--db-max-pool-size", type=int, default=3)

    return parser


def save_raw_programs(programs: List[Dict[str, Any]]):
    with raw_path.open("w", encoding="utf-8") as f, raw_path_ts.open(
        "w", encoding="utf-8"
    ) as f_ts:
        for program in programs:
            json_string = json.dumps(program, ensure_ascii=False) + "\n"
            f.write(json_string)
            f_ts.write(json_string)

    print(f"Saved {len(programs)} raw programs.")


def do_load(args):
    print("[*] Start loading...")
    loaders = [
        BokjiroLoader(
            max_page=args.load_max_page_bokjiro,
        ),
        Subsidy24Loader(
            max_page=args.load_max_page_subsidy24,
        ),
    ]

    count = 0
    with raw_path.open("w", encoding="utf-8") as f, raw_path_ts.open(
        "w", encoding="utf-8"
    ) as f_ts:
        for loader in loaders:
            print(f"Running {loader}...")

            for page in tqdm(
                range(1, loader.max_page + 1),
                desc=f"{loader}",
                unit="page",
            ):
                # TODO: try catch load for each page
                programs = loader.load(page)

                for program in programs:
                    json_string = json.dumps(program, ensure_ascii=False) + "\n"
                    f.write(json_string)
                    f_ts.write(json_string)

                count += len(programs)

    print(f"Total {count} programs are loaded.\n")


def do_trim(args) -> List[Dict[str, Any]]:
    print("[*] Start trimming...")

    count = 0
    total_bytes = raw_path.stat().st_size

    with raw_path.open("r", encoding="utf-8") as f_in, trimmed_path.open(
        "w", encoding="utf-8"
    ) as f_out, trimmed_path_ts.open("w", encoding="utf-8") as f_out_ts, tqdm(
        total=total_bytes, desc="Trimming Programs", unit="B", unit_scale=True
    ) as pbar:
        for line in f_in:
            raw_program = json.loads(line)
            result = graph.invoke({"raw_program": raw_program})

            is_valid = result["is_valid"]
            program = result["trimmed_program"]

            if is_valid:
                json_string = json.dumps(program, ensure_ascii=False) + "\n"
                f_out.write(json_string)
                f_out_ts.write(json_string)
                count += 1
            pbar.update(len(line.encode("utf-8")))

        print(f"Total {count} programs are trimmed.\n")


def do_vectorize(args):
    print("[*] Start vectorizing...")
    count = 0
    total_bytes = trimmed_path.stat().st_size

    batch_size = args.vectorize_batch_size
    vectorizer = Vectorizer()

    with trimmed_path.open("r", encoding="utf-8") as f_in, embedding_path.open(
        "w", encoding="utf-8"
    ) as f_out, embedding_path_ts.open("w", encoding="utf-8") as f_out_ts, tqdm(
        total=total_bytes, desc="Vectorize", unit="B", unit_scale=True
    ) as pbar:
        text_batch = []
        uuid_batch = []

        for line in f_in:
            program = json.loads(line)
            text_batch.append(program["details"])
            uuid_batch.append(program["uuid"])

            if len(text_batch) >= batch_size:
                vector_batch = vectorizer.run(text_batch)

                if len(vector_batch) == len(uuid_batch):
                    for uuid, vector in zip(uuid_batch, vector_batch):
                        data = {"uuid": uuid, "embedding": vector.tolist()}
                        json_string = json.dumps(data, ensure_ascii=False) + "\n"
                        f_out.write(json_string)
                        f_out_ts.write(json_string)

                    count += len(vector_batch)
                    text_batch = []
                    uuid_batch = []

            pbar.update(len(line.encode("utf-8")))

        if text_batch and uuid_batch:
            vector_batch = vectorizer.run(text_batch)

            if len(vector_batch) == len(uuid_batch):
                for uuid, vector in zip(uuid_batch, vector_batch):
                    data = {"uuid": uuid, "embedding": vector.tolist()}
                    json_string = json.dumps(data, ensure_ascii=False) + "\n"
                    f_out.write(json_string)
                    f_out_ts.write(json_string)

                count += len(vector_batch)
                text_batch = []
                uuid_batch = []

    print(f"Total {count} programs are vectorized.\n")


def do_save(args):
    print("[*] Start saving to DB...")

    with PostgresManager(
        conn_string=DATABASE_URL,
        failure_path=save_failure_path,
        min_pool_size=args.db_min_pool_size,
        max_pool_size=args.db_max_pool_size,
    ) as db_manager:

        count = 0
        total_bytes = trimmed_path.stat().st_size + embedding_path.stat().st_size

        batch = []
        batch_size = args.db_commit_batch_size

        with trimmed_path.open("r", encoding="utf-8") as f_p, embedding_path.open(
            "r", encoding="utf-8"
        ) as f_e, tqdm(
            total=total_bytes, desc="Save", unit="B", unit_scale=True
        ) as pbar:
            for line_p, line_e in zip(f_p, f_e):
                program = json.loads(line_p)
                embedding = json.loads(line_e)

                if program["uuid"] != embedding["uuid"]:
                    raise MismatchError("[!] The Embedding does not match the Program.")

                program["embedding"] = str(embedding["embedding"])
                batch.append(program)

                if len(batch) >= batch_size:
                    count += db_manager.save_programs(batch)
                    batch = []

                pbar.update(len(line_p.encode("utf-8")) + len(line_e.encode("utf-8")))

            if batch:
                count += db_manager.save_programs(batch)
                batch = []

    print(f"Total {count} programs are saved to DB.\n")


def main():
    parser = create_parser()
    args = parser.parse_args()

    mode = args.mode

    if mode == "load":
        do_load(args)
    elif mode == "trim":
        do_trim(args)
    elif mode == "vectorize":
        do_vectorize(args)
    elif mode == "save":
        do_save(args)
    elif mode == "all":
        do_load(args)
        do_trim(args)
        do_vectorize(args)
        do_save(args)


if __name__ == "__main__":
    main()
