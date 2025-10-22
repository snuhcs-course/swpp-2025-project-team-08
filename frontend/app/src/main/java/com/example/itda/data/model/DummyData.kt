package com.example.itda.data.model

import com.example.itda.R

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

    val dummyCategories = listOf<Category>(
        Category(0,  "전체"),
        Category(1,  "대회"),
        Category(2,  "소비지원"),
        Category(3,  "교육"),
        Category(4,  "자산형성"),
        Category(5,  "민간사업"),
        Category(6,  "정부사업"),
        Category(7,  "AI"),
        Category(8,  "IT"),
    )

    val dummyFeedItems = listOf<FeedItem>(
        FeedItem(
            id = 1,
            title = "AI 해커톤 2025",
            categories = listOf<Category>(
                dummyCategories[1],
                dummyCategories[5],
                dummyCategories[7],
                dummyCategories[8]
            ),
            department = "소프트웨어학과",
            link = "https://example.com/ai-hackathon",
            content = "인공지능을 활용한 창의적 문제 해결을 주제로 한 해커톤입니다.",
            start_date = "2025-11-01",
            end_date = "2025-11-03",
            isStarred = true,
            logo = R.drawable.gov_logo,
            isEligible = true
        ),
        FeedItem(
            id = 2,
            title = "IT 취업 역량 캠프",
            categories = listOf<Category>(
                dummyCategories[3],
                dummyCategories[5],
                dummyCategories[8]
            ),
            department = "진로취업센터",
            link = "https://example.com/jobcamp",
            content = "IT 직무 취업을 위한 실전 포트폴리오 작성 및 모의 면접 프로그램.",
            start_date = "2025-10-20",
            end_date = "2025-10-25",
            isStarred = false,
            logo = R.drawable.hissf_logo,
            isEligible = true
        ),
        FeedItem(
            id = 3,
            title = "청년 도약 계좌",
            categories = listOf<Category>(
                dummyCategories[4],
                dummyCategories[6],
            ),
            department = "금융위원회",
            content = "최대 5천만원 목돈 마련 기회",
            link = "https://example.com/ai-hackathon",
            start_date = "2025-11-01",
            end_date = "2025-11-03",
            isStarred = false,
            logo = R.drawable.ic_launcher_foreground,
            isEligible = false
        ),
        FeedItem(
            id = 4,
            title = "민생회복 소비쿠폰",
            categories = listOf<Category>(
                dummyCategories[2],
                dummyCategories[6],
            ),
            department = "행정안전부",
            content = "25만원 받을 수 있음",
            link = "https://example.com/jobcamp",
            start_date = "2025-10-20",
            end_date = "2025-10-25",
            isStarred = true,
            logo = R.drawable.gov_logo,
            isEligible = false
        ),
        FeedItem(
            id = 5,
            title = "2차 민생회복 소비쿠폰",
            categories = listOf<Category>(
                dummyCategories[2],
                dummyCategories[6],
            ),
            department = "행정안전부",
            content = "25만원 받을 수 있음",
            link = "https://example.com/jobcamp",
            start_date = "2025-10-20",
            end_date = "2025-10-25",
            isStarred = true,
            logo = R.drawable.ic_launcher_background,
            isEligible = false
        ),
        FeedItem(
            id = 6,
            title = "1000회차 민생회복 소비로또",
            categories = listOf<Category>(
                dummyCategories[2],
                dummyCategories[6],
            ),
            department = "행정안전부",
            content = "25만원 받을 수 있음",
            link = "https://example.com/jobcamp",
            start_date = "2025-10-20",
            end_date = "2025-10-25",
            isStarred = false,
            logo = R.drawable.gov_logo,
            isEligible = true
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

}