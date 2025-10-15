package com.example.itda.data.model

object DummyData {
    val dummyPrograms = listOf<Program>(
        Program(
            id = 1,
            title = "AI 해커톤 2025",
            category = "대회",
            department = "소프트웨어학과",
            link = "https://example.com/ai-hackathon",
            content = "인공지능을 활용한 창의적 문제 해결을 주제로 한 해커톤입니다.",
            start_date = "2025-11-01",
            end_date = "2025-11-03"
        ),
        Program(
            id = 2,
            title = "IT 취업 역량 캠프",
            category = "교육",
            department = "진로취업센터",
            link = "https://example.com/jobcamp",
            content = "IT 직무 취업을 위한 실전 포트폴리오 작성 및 모의 면접 프로그램.",
            start_date = "2025-10-20",
            end_date = "2025-10-25"
        ),
        Program(
            id = 3,
            title = "청년 도약 계좌",
            category = "자산형성",
            department = "금융위원회",
            content = "최대 5천만원 목돈 마련 기회",
            link = "https://example.com/ai-hackathon",
            start_date = "2025-11-01",
            end_date = "2025-11-03"
        ),
        Program(
            id = 4,
            title = "민생회복 소비쿠폰",
            category = "소비지원",
            department = "행정안전부",
            content = "25만원 받을 수 있음",
            link = "https://example.com/jobcamp",
            start_date = "2025-10-20",
            end_date = "2025-10-25"
        ),
        Program(
            id = 5,
            title = "2차 민생회복 소비쿠폰",
            category = "소비지원",
            department = "행정안전부",
            content = "25만원 받을 수 있음",
            link = "https://example.com/jobcamp",
            start_date = "2025-10-20",
            end_date = "2025-10-25"
        ),
        Program(
            id = 6,
            title = "1000회차 민생회복 소비로또",
            category = "소비지원",
            department = "행정안전부",
            content = "25만원 받을 수 있음",
            link = "https://example.com/jobcamp",
            start_date = "2025-10-20",
            end_date = "2025-10-25"
        ),
    )

    val dummyUser = listOf<User>(
        User(
            id = 1,
            email = "hong.gildong@example.com",
            password = "hashed_password_1",
            name = "홍길동",
            age = 32,
            gender = "남성",
            address = "서울특별시 강남구",
            marital_status = "기혼",
            education_level = "대학교 졸업",
            household_size = 4,
            household_income = 7500 // 단위: 만 원
        ),
        User(
            id = 2,
            email = "kim.younghee@example.com",
            password = "hashed_password_2",
            name = "김영희",
            age = 25,
            gender = "여성",
            address = "부산광역시 해운대구",
            marital_status = "미혼",
            education_level = "대학 재학 중",
            household_size = 1,
            household_income = 3000
        ),
        User(
            id = 3,
            email = "park.chulsoo@example.com",
            password = "hashed_password_3",
            name = "박철수",
            age = 48,
            gender = "남성",
            address = "경기도 성남시 분당구",
            marital_status = "기혼",
            education_level = "대학원 졸업",
            household_size = 3,
            household_income = 12000
        )
    )

    val dummyCategories = listOf<Category>(
        Category(0, "all", "전체"),
        Category(1, "competition", "대회"),
        Category(2, "spending", "소비지원"),
        Category(3, "education", "교육"),
        Category(4, "wealth", "자산형성"),
    )
}