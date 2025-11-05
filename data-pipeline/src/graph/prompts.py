from langchain_core.prompts import PromptTemplate

TRIM_PROGRAM_TEMPLATE_TEXT = """
You are a data transformation assistant.
Convert the raw_program JSON into the structured JSON format below.

**Target JSON Structure (all fields required; use null if data unavailable):**
- uuid: string
- title: string
- preview: string (One line summary of the program, must be less than 10 words. e.g. 60세 이상 저소득 무릎관절 수술비 지원)
- summary: string (A concise 3-5 line summary of the key points)
- details: string (A comprehensive, highly specific description of all program contents)
- application_method: string | null
- apply_url: string | null (If applications are submitted online, provide a valid website URL)
- reference_url: string | null
- apply_start_at: string | null (ISO 8601 timestamp)
- apply_end_at: string | null (ISO 8601 timestamp)
- eligibility_min_age: integer | null (Minimum applicant age)
- eligibility_max_age: integer | null (Maximum applicant age)
- eligibility_region: string | null (full region name, e.g., "서울시 용산구")
- eligibility_min_household: integer | null (Minimum applicant household size)
- eligibility_max_household: integer | null (Maximun applicant household size)
- eligibility_min_income: integer | null (Annual net income in units of 10,000 KRW; e.g., 5000 for 50M KRW)
- eligibility_max_income: integer | null (Annual net income in units of 10,000 KRW; e.g., 5000 for 50M KRW)
- eligibility_gender: 'MALE' | 'FEMALE' | null (Applicant gender)
- eligibility_marital_status: 'SINGLE' | 'MARRIED' | 'DIVORCED_OR_BEREAVED' | null (Applicant marital status)
- eligibility_education: 'ELEMENTARY_SCHOOL_STUDENT' | 'MIDDLE_SCHOOL_STUDENT' | 'HIGH_SCHOOL_STUDENT' | 'COLLEGE_STUDENT' | 'ELEMENTARY_SCHOOL' | 'MIDDLE_SCHOOL' | 'HIGH_SCHOOL' | 'ASSOCIATE' | 'BACHELOR' | null (Applicant education status)
- eligibility_employment: 'EMPLOYED' | 'UNEMPLOYED' | 'SELF_EMPLOYED' | null (Applicant employment status)
- category: 'CASH' | 'HEALTH' | 'CARE' | 'DEMENTIA' | 'EMPLOYMENT' | 'LEISURE' | 'HOUSING' | 'OTHER' (Program's category)
- operating_entity: string (Full name of the operating organization, e.g., "보건복지부" for the 'central' type or "경기도 용인시" for the 'local' type)
- operating_entity_type: 'local' | 'center' (Organization type: 'local' for local government, 'center' for central government)


**Instructions:**
1. Map raw fields to the structure above.
2. Use null for missing/unavailable data.
3. Return valid JSON with ALL fields (no extras).
4. Use Korean for the `title`, `preview`, `summary`, `details`, and `application_method` fields.

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
