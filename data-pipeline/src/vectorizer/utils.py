from typing import Dict, Any

GENDER_MAP = {"MALE": "남성", "FEMALE": "여성"}

MARITAL_STATUS_MAP = {
    "SINGLE": "미혼",
    "MARRIED": "기혼",
    "DIVORCED_OR_BEREAVED": "이혼/사별",
}

EMPLOYMENT_MAP = {
    "EMPLOYED": "재직자",
    "UNEMPLOYED": "미취업자",
    "SELF_EMPLOYED": "자영업자",
}

EDUCATION_MAP = {
    "ELEMENTARY_SCHOOL_STUDENT": "초등학생",
    "MIDDLE_SCHOOL_STUDENT": "중학생",
    "HIGH_SCHOOL_STUDENT": "고등학생",
    "COLLEGE_STUDENT": "대학생",
    "ELEMENTARY_SCHOOL": "초졸",
    "MIDDLE_SCHOOL": "중졸",
    "HIGH_SCHOOL": "고졸",
    "ASSOCIATE": "전문대졸",
    "BACHELOR": "대졸",
}

CATEGORY_MAP = {
    "CASH": "현금 지급",
    "HEALTH": "보건 의료",
    "CARE": "돌봄 요양",
    "DEMENTIA": "치매 지원",
    "EMPLOYMENT": "고용 일자리",
    "LEISURE": "여가 문화",
    "HOUSING": "주거 지원",
    "OTHER": "기타",
}


def generate_program_text(program: Dict[str, Any]) -> str:
    parts = []

    category_kr = CATEGORY_MAP.get(program.get("category"), "")
    title = program.get("title", "")
    preview = program.get("preview", "")

    raw_summary = program.get("summary", "")
    summary = raw_summary.replace("\n-", ". ").replace("-", "").strip()

    parts.append(f"[{category_kr}] {title}. {preview}. {summary}")

    parts.append("지원 대상 요건은 다음과 같습니다.")

    if region := program.get("eligibility_region"):
        parts.append(f"지원 대상 거주지는 {region}입니다.")

    min_age = program.get("eligibility_min_age")
    max_age = program.get("eligibility_max_age")
    if min_age is not None and max_age is not None:
        parts.append(f"지원 대상 나이는 {min_age}세 이상 {max_age}세 이하입니다.")
    elif min_age is not None:
        parts.append(f"지원 대상 나이는 {min_age}세 이상입니다.")
    elif max_age is not None:
        parts.append(f"지원 대상 나이는 {max_age}세 이하입니다.")

    if gender_key := program.get("eligibility_gender"):
        gender_kr = GENDER_MAP.get(gender_key, gender_key)
        parts.append(f"지원 대상 성별은 {gender_kr}입니다.")

    if emp_key := program.get("eligibility_employment"):
        emp_kr = EMPLOYMENT_MAP.get(emp_key, emp_key)
        parts.append(f"지원 대상 고용 상태는 {emp_kr}입니다.")

    if marital_key := program.get("eligibility_marital_status"):
        marital_kr = MARITAL_STATUS_MAP.get(marital_key, marital_key)
        parts.append(f"지원 대상 결혼 상태는 {marital_kr}입니다.")

    if edu_key := program.get("eligibility_education"):
        edu_kr = EDUCATION_MAP.get(edu_key, edu_key)
        parts.append(f"지원 대상 학력은 {edu_kr}입니다.")

    min_hh = program.get("eligibility_min_household")
    max_hh = program.get("eligibility_max_household")
    if min_hh is not None and max_hh is not None:
        parts.append(f"지원 대상 가구원 수는 {min_hh}인 이상 {max_hh}인 이하입니다.")
    elif min_hh is not None:
        parts.append(f"지원 대상 가구원 수는 {min_hh}인 이상입니다.")

    min_inc = program.get("eligibility_min_income")
    max_inc = program.get("eligibility_max_income")

    if min_inc is not None and max_inc is not None:
        parts.append(
            f"지원 대상 가구 소득은 {min_inc}만원 이상 {max_inc}만원 이하입니다."
        )
    elif max_inc is not None:
        parts.append(f"지원 대상 가구 소득은 {max_inc}만원 이하입니다.")
    elif min_inc is not None:
        parts.append(f"지원 대상 가구 소득은 {min_inc}만원 이상입니다.")

    return " ".join(parts).strip()
