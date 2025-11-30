from langchain_core.prompts import PromptTemplate

TRIM_PROGRAM_TEMPLATE_TEXT = """
You are a data transformation assistant.
Convert the raw_program JSON into the structured JSON format below.

**Target JSON Structure (all fields required; use null if data unavailable):**
- uuid: string
- title: string
- preview: string (One line summary of the program, must be less than 10 words. e.g. 60세 이상 저소득 무릎관절 수술비 지원)
- summary: string (Concise 3-5 line summary as a markdown list; each line must start with '-')
- details: string (Follow "Details Format" below)
- application_method: string | null
- apply_url: string | null (If applications are submitted online, provide a valid website URL)
- reference_url: string | null
- apply_start_at: string | null (ISO 8601 timestamp)
- apply_end_at: string | null (ISO 8601 timestamp)
- eligibility_min_age: integer | null (Minimum applicant age)
- eligibility_max_age: integer | null (Maximum applicant age)
- eligibility_region: string | null (Follow "Eligibility Region Format" below)
- eligibility_min_household: integer | null (Minimum applicant household size)
- eligibility_max_household: integer | null (Maximun applicant household size)
- eligibility_min_income: integer | null (Annual net income in units of 10,000 KRW; e.g., 5000 for 50M KRW)
- eligibility_max_income: integer | null (Annual net income in units of 10,000 KRW; e.g., 5000 for 50M KRW)
- eligibility_gender: 'MALE' | 'FEMALE' | null (Applicant gender)
- eligibility_marital_status: 'SINGLE' | 'MARRIED' | 'DIVORCED_OR_BEREAVED' | null (Applicant marital status)
- eligibility_education: 'ELEMENTARY_SCHOOL_STUDENT' | 'MIDDLE_SCHOOL_STUDENT' | 'HIGH_SCHOOL_STUDENT' | 'COLLEGE_STUDENT' | 'ELEMENTARY_SCHOOL' | 'MIDDLE_SCHOOL' | 'HIGH_SCHOOL' | 'ASSOCIATE' | 'BACHELOR' | null (Applicant education status)
- eligibility_employment: 'EMPLOYED' | 'UNEMPLOYED' | 'SELF_EMPLOYED' | null (Applicant employment status)
- category: 'CASH' | 'HEALTH' | 'CARE' | 'DEMENTIA' | 'EMPLOYMENT' | 'LEISURE' | 'HOUSING' | 'OTHER' (Program's category)
- operating_entity: string | null (Name of the operating organization. e.g., 기후에너지환경부 기후적응과, 보건복지부 기초연금과)
- operating_entity_type: 'LOCAL' | 'CENTRAL' (Organization type: 'LOCAL' for local government, 'CENTRAL' for central government)

**Instructions:**
1. Map raw fields to the structure above.
2. Use null for missing/unavailable data.
3. Return valid JSON with ALL fields (no extras).
4. Use Korean for the `title`, `preview`, `summary`, `details`, and `application_method` fields.
5. For details and eligibility_region, Follow the below formats

**Details Format**
1. 원칙
- 모든 내용은 최대한 간결하게 작성한다.
- 짧은 내용은 가능하다면 Phrase 정도 단위로 요약한다.
- Phrase 수준까지 요약하여 너무 많은 정보가 소실된다면 한 문장까지로 요약한다.
- 나열하는 것이 필요할 때는 markdown 목록 문법을 사용하여 나열한다.
- 모든 원칙을 지켜 다음 형식대로 저장한다.

2. 형식
```
### 지원 대상
* (한 줄 이내로 요약한 지원 대상. 예 : "60대 이상 남성", "국내 거주 내·외국인 누구나")
  + **소득 기준** : (명시된 소득에 관한 기준이 없다면 이 항목 자체를 제거한다. (예: 기준 중위소득 70% 이하 가구))
  + **재산 기준** : (명시된 재산에 관한 기준이 없다면 이 항목 자체를 제거한다. (예: 월간 소득 3.5억 원 이하))
  + **제외 대상** : (명시된 제외 대상에 대한 기준이 없다면 이 항목 자체를 제거한다. (예: 타 유사 서비스 수혜자, 장기요양 등급 1~2등급 판정자 등))
---
### 내용
(간결히 요약한 지원 내용들을 나열하고 정리한다. 이 부분은 각 정책의 성격에 알맞게 자유롭게 항목을 추가하고 제거하고 수정해도 무방하다.)
(예: * **내용 : ** 지역 노년 일자리 제공\n* **지원 금액 : ** 월 10만원 상당의 현금 지원)
---
### 절차
* **신청 방법** : 온라인 (예: [복지로 웹사이트 URL](https://www.bokjiro.go.kr)) 또는 방문 (예: 거주지 관할 동/면/읍 주민센터) 에 맞춰 알맞은 내용을 표시한다.
* **필요 서류** : (예: 신분증 사본, 소득/재산 신고서, 건강보험자격득실확인서 등)
* **결과 발표** : (예: 신청일로부터 30일 이내 개별 통보, 익월 1일 적용)
---
### 관련 정보
* **대표 전화:** (전화번호와 그 번호가 어디로 연결되는 번호인지, 저장된 내용에 포함되어 있다면 적고 없다면 "없음" 으로 표시. (예: 129 보건복지콜센터 또는 02-XXX-XXXX 또는 없음))
* **관련 웹사이트:** (저장된 내용에 포함되어 있다면 적고 없다면 "없음" 으로 표시. (예: [보건복지부 홈페이지](https://google.com, "google link")))
* **참고 사항:** (중요한 내용이 아니면 이 부분자체가 없어도 무관하다. (예: 정책은 예산 범위 내에서 변경될 수 있습니다.))
```

**Eligibility Region Format**
- Extract based on 'operating_entity_type':
- If operating_entity_type is 'CENTRAL': null
- If operating_entity_type is 'LOCAL': Output strictly in "Region" or "Region District" format.
  - Region: 강원, 경기, 경남, 경북, 광주, 대구, 대전, 부산, 서울, 세종, 울산, 인천, 전남, 전북, 제주, 충남, 충북 (Must not be full name like "서울특별시", "경상북도" or "부산광역시")
  - District: Must end in '시', '군', or '구' (e.g., "경기 성남시").

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
