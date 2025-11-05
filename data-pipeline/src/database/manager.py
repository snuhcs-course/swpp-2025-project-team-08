from typing import Any, Dict, List
from psycopg.rows import dict_row
from psycopg_pool import ConnectionPool


class PostgresManager:
    def __init__(
        self, conn_string: str, min_pool_size: int = 1, max_pool_size: int = 10
    ) -> None:
        self.pool = ConnectionPool(
            conninfo=conn_string,
            min_size=min_pool_size,
            max_size=max_pool_size,
            kwargs={"row_factory": dict_row},
        )
        self.insert_sql = """
            INSERT INTO program (
                uuid, title, preview, summary, details, application_method, 
                apply_url, reference_url,
                eligibility_gender, eligibility_min_age, eligibility_max_age,
                eligibility_region, eligibility_marital_status, eligibility_education,
                eligibility_min_household, eligibility_max_household,
                eligibility_min_income, eligibility_max_income, eligibility_employment,
                operating_entity, operating_entity_type, apply_start_at, apply_end_at, embedding, category
            ) VALUES (
                %(uuid)s, %(title)s, %(preview)s, %(summary)s, %(details)s, %(application_method)s, 
                %(apply_url)s, %(reference_url)s,
                %(eligibility_gender)s, %(eligibility_min_age)s, %(eligibility_max_age)s,
                %(eligibility_region)s, %(eligibility_marital_status)s, %(eligibility_education)s,
                %(eligibility_min_household)s, %(eligibility_max_household)s,
                %(eligibility_min_income)s, %(eligibility_max_income)s, %(eligibility_employment)s,
                %(operating_entity)s, %(operating_entity_type)s, %(apply_start_at)s, %(apply_end_at)s,
                %(embedding)s, %(category)s
            )
            ON CONFLICT (uuid) DO NOTHING
        """

    def save_programs(self, programs: List[Dict[str, Any]]) -> int:
        if not programs:
            return 0

        inserted_count = 0
        with self.pool.connection() as conn:
            with conn.cursor() as cur:
                for program in programs:
                    cur.execute(self.insert_sql, program)
                    inserted_count += cur.rowcount
            conn.commit()

        return inserted_count

    def close(self) -> None:
        self.pool.close()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        self.close()
