from langchain_core.prompts import PromptTemplate

TRIM_PROGRAM_TEMPLATE_TEXT = """
You are a data transformation assistant.
Convert the raw_program JSON into the structured JSON format below.

**Target JSON Structure (all fields required; use null if data unavailable):**
- uuid: string
- title: string
- preview: string (One line summary of the program)
- summary: string (A concise 3-5 line summary of the key points)
- details: string (A comprehensive, highly specific description of all program contents)
- application_method: string | null
- apply_url: string | null
- reference_url: string | null
- apply_start_at: string | null (ISO 8601 timestamp)
- apply_end_at: string | null (ISO 8601 timestamp)
- eligibility_gender: '남성' | '여성' | null
- eligibility_min_age: integer | null
- eligibility_max_age: integer | null
- eligibility_region: string | null (full region name, e.g., "서울 용산구")
- eligibility_marital_status: '미혼' | '기혼' | '이혼/사별' | null
- eligibility_education: string | null
- eligibility_min_household: integer | null
- eligibility_max_household: integer | null
- eligibility_min_income: integer | null (Annual net income in units of 10,000 KRW; e.g., 5000 for 50M KRW)
- eligibility_max_income: integer | null (Annual net income in units of 10,000 KRW; e.g., 5000 for 50M KRW)
- eligibility_employment: '재직자' | '미취업자' | '자영업자' | null
- operating_entity: string | null (Use 'central' for central gov, or the full local entity name, e.g., "경기도 용인시")


**Instructions:**
1. Map raw fields to the structure above.
2. Use null for missing/unavailable data.
3. Return valid JSON with ALL fields (no extras).

{format_instructions}

**raw_program JSON:**
```json
{raw_program}
```
"""


def build_trim_program_prompt(format_instructions: str):
    return PromptTemplate(
        template=TRIM_PROGRAM_TEMPLATE_TEXT,
        input_variables=["raw_program"],
        partial_variables={"format_instructions": format_instructions},
    )
