package com.example.itda.data.model

data class User(
    val id: Int,                 // 사용자 ID
    val email: String,           // 이메일
    val password: String,        // 비밀번호
    val name: String,            // 이름
    val age: Int,                // 나이
    val gender: String,          // 성별
    val address: String,         // 주소
    val marital_status: String,  // 결혼 여부
    val education_level: String, // 교육 수준
    val household_size: Int,     // 가구 인원수
    val household_income: Int    // 가구 소득
)
