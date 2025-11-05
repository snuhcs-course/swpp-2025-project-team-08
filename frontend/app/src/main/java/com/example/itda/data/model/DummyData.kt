package com.example.itda.data.model

import com.example.itda.R

object DummyData {
    val dummyPrograms = listOf<Program>(
        /*
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
        */
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

    val dummyFeedItems = listOf<Program>(
        Program(
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
        Program(
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
        Program(
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
        Program(
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
        Program(
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
        Program(
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
            id = "1",  // String으로 변경
            email = "test@example.com",
            name = "홍길동",
            birthDate = "1990-01-01",
            gender = "남성",
            address = "서울특별시 강남구",
            postcode = "12345",
            maritalStatus = "미혼",
            educationLevel = "대졸",
            householdSize = 3,
            householdIncome = 5000,
            employmentStatus = "재직자"
        ),
        User(
            id = "2",
            email = "test2@example.com",
            name = "김철수",
            birthDate = "1985-05-15",
            gender = "남성",
            address = "서울특별시 서초구",
            postcode = "12345",
            maritalStatus = "기혼",
            educationLevel = "석사",
            householdSize = 4,
            householdIncome = 8000,
            employmentStatus = "재직자"
        )
    )

}