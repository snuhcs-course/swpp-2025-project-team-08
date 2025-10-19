import argparse
import os
import sys
from urllib.parse import quote_plus
from tqdm import tqdm
from dotenv import load_dotenv
from graph import graph
from loader.api_loader import YouthCenterLoader
from database.manager import PostgresManager

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

    parser.add_argument("--db-commit-batch-size", type=int, default=10)
    parser.add_argument("--db-min-pool-size", type=int, default=1)
    parser.add_argument("--db-max-pool-size", type=int, default=5)
    parser.add_argument("--api-page-size", type=int, default=50)
    parser.add_argument("--api-max-page-num", type=int, default=10)

    return parser


def main():
    parser = create_parser()
    args = parser.parse_args()

    loaders = [
        YouthCenterLoader(
            api_key=YOUTH_CENTER_API_KEY,
            page_size=args.api_page_size,
            max_page_num=args.api_max_page_num,
        )
    ]

    print("[*] Start loaders...")
    raw_programs = []

    for loader in tqdm(loaders, desc="Loading programs"):
        loader_programs = loader.load()
        raw_programs.extend(loader_programs)
    print(f"Total {len(raw_programs)} programs are loaded.\n")

    print("[*] Start trimming with graph...")
    programs = []

    for raw_program in tqdm(raw_programs, desc="Trimming Programs"):
        result = graph.invoke({"raw_program": raw_program})
        programs.append(result["trimmed_program"])
    print(f"Total {len(programs)} programs are trimmed.\n")

    print("[*] Start saving to DB...")
    db_manager = PostgresManager(
        conn_string=DATABASE_URL,
        min_pool_size=args.db_min_pool_size,
        max_pool_size=args.db_max_pool_size,
    )
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


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n[!] Interrupted by user", file=sys.stderr)
        sys.exit(130)
    except Exception as e:
        print(f"[!] Error: {e}", file=sys.stderr)
        sys.exit(1)
