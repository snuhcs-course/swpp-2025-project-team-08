from langchain_core.prompts import PromptTemplate

TRIM_PROGRAM_TEMPLATE_TEXT = """
You are a data transformation assistant.
Convert the raw_program JSON into the structured JSON format below.

**Target JSON Structure (all fields required; use null if data unavailable):**
- uuid: string
- title: string
- details: string
- application_method: string | null
- apply_url: string | null
- reference_url: string | null
- apply_start_at: ISO 8601 timestamp string | null
- apply_end_at: ISO 8601 timestamp string | null

- eligibility_gender: '남성' | '여성' | null
- eligibility_min_age: integer | null
- eligibility_max_age: integer | null
- eligibility_region: string | null
- eligibility_marital_status: '무관' | '미혼' | '기혼' | '이혼/사별' | null
- eligibility_education: string | null
- eligibility_min_household: integer | null
- eligibility_max_household: integer | null
- eligibility_min_income: integer | null
- eligibility_max_income: integer | null
- eligibility_employment: '무관' | '재직자' | '미취업자' | '자영업자' | null


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
