package com.example.itda.data.model

data class Program(
    val id: Int,             // 프로그램 고유 ID
    val title: String,       // 프로그램 제목
    val category: String,    // 프로그램 카테고리
    val department: String,  // 주관 부서
    val link: String,        // 관련 링크 (URL)
    val content: String,     // 프로그램 설명 (text)
    val start_date: String,  // 시작 날짜 (ISO 포맷 문자열로 전달)
    val end_date: String     // 종료 날짜
)