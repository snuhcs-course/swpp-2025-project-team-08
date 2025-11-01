import json
import os
import argparse

from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Optional, Dict, List
from urllib.parse import quote_plus

from dotenv import load_dotenv
from tqdm import tqdm

from graph import graph
from database.manager import PostgresManager
from loader import BokjiroLoader

load_dotenv()

DB_HOST = os.getenv("DB_HOST")
DB_PORT = os.getenv("DB_PORT")
DB_NAME = os.getenv("DB_NAME")
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DATABASE_URL = f"postgresql://{quote_plus(DB_USER)}:{quote_plus(DB_PASSWORD)}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

YOUTH_CENTER_API_KEY = os.getenv("YOUTH_CENTER_API_KEY")


def create_parser():
    parser = argparse.ArgumentParser(description="data pipeline for ITDA")

    parser.add_argument(
        "mode",
        choices=["load", "trim", "save", "all"],
        help="which stage to run: load => fetch from APIs, trim => transform, save => persist to DB, all => run everything",
    )

    parser.add_argument("--db-commit-batch-size", type=int, default=10)
    parser.add_argument("--db-min-pool-size", type=int, default=1)
    parser.add_argument("--db-max-pool-size", type=int, default=5)
    parser.add_argument("--api-page-size", type=int, default=50)
    parser.add_argument("--api-max-page-num", type=int, default=10)

    return parser


def save_raw_programs(programs: List[Dict[str, Any]]):
    project_root = Path(__file__).resolve().parents[1]
    data_dir = project_root / "data"
    data_dir.mkdir(parents=True, exist_ok=True)

    ts = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ")
    save_path_ts = data_dir / f"raw_programs_{ts}.json"
    save_path = data_dir / "raw_programs.json"

    with save_path_ts.open("w", encoding="utf-8") as f:
        json.dump(programs, f, ensure_ascii=False, indent=2)

    with save_path.open("w", encoding="utf-8") as f:
        json.dump(programs, f, ensure_ascii=False, indent=2)

    print(f"Saved raw programs to {save_path_ts} and {save_path}.")


def save_trimmed_programs():
    pass


def do_load(args, db_manager: PostgresManager) -> List[Dict[str, Any]]:
    print("[*] Start loading...")
    loaders = [
        BokjiroLoader(
            page_size=args.api_page_size,
            max_page_num=args.api_max_page_num,
        ),
    ]

    programs = []
    for loader in loaders:
        print(f"Running {loader}...")
        loader_programs = loader.load()
        programs.extend(loader_programs)
    print(f"Total {len(programs)} programs are loaded.")

    save_raw_programs(programs)
    print()

    return programs


def do_trim(
    args, raw_programs: Optional[List[Dict[str, Any]]] = None
) -> List[Dict[str, Any]]:
    print("[*] Start trimming with graph...")
    programs = []

    for raw_program in tqdm(raw_programs, desc="Trimming Programs"):
        result = graph.invoke({"raw_program": raw_program})
        programs.append(result["trimmed_program"])
    print(f"Total {len(programs)} programs are trimmed.\n")

    return programs


def do_save(
    args, db_manager: PostgresManager, programs: Optional[List[Dict[str, Any]]] = None
):
    print("[*] Start saving to DB...")
    try:
        total_inserted = 0
        batch_size = args.db_commit_batch_size

        for i in tqdm(range(0, len(programs), batch_size), desc="Saving Programs"):
            batch = programs[i : i + batch_size]
            inserted_count = db_manager.save_programs(batch)
            total_inserted += inserted_count

        print(f"Total {total_inserted} programs are saved.\n")
    finally:
        db_manager.close()


def main():
    parser = create_parser()
    args = parser.parse_args()

    mode = args.mode

    db_manager = PostgresManager(
        conn_string=DATABASE_URL,
        min_pool_size=args.db_min_pool_size,
        max_pool_size=args.db_max_pool_size,
    )

    if mode == "load":
        do_load(args, db_manager)
    elif mode == "trim":
        do_trim(args)
    elif mode == "save":
        do_save(args, db_manager)
    elif mode == "all":
        programs = do_load(args, db_manager)
        programs = do_trim(args, programs)
        do_save(args, db_manager, programs)


if __name__ == "__main__":
    main()
