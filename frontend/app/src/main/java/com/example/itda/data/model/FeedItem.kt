package com.example.itda.data.model

data class FeedItem (
    val id: Int,                // 프로그램 ID
    val isStared : Boolean,     // 즐겨찾기 여부
    val logo : Int,             // 로고 ID TODO - logoUrl로 변경 필요
    val isEligible: Boolean,    // 신청 대상자 여부
)

