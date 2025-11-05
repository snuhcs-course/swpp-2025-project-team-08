package com.example.itda.data.model

data class Category(
    val category: String,       // category id
    val value: String,           // korean category name
)

val dummyCategories = listOf<Category>(
    Category("", "전체"),
    Category("cash", "빈곤 완화"),
    Category("health","보건, 의료"),
    Category("care", "돌봄, 요양"),
    Category("dementia", "치매 관련"),
    Category("employment", "고용, 일자리"),
    Category("leisure", "사회, 여가문화 활동"),
    Category("housing", "주거 지원"),
    Category("other", "기타")
)