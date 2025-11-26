package com.example.itda.data.model

import com.google.gson.annotations.SerializedName

/*data class Program(
    val id: Int,             // 프로그램 고유 ID
    val title: String,       // 프로그램 제목
    val categories: List<Category>,    // 프로그램 카테고리
    val department: String,  // 주관 부서
    val link: String,        // 관련 링크 (URL)
    val content: String,     // 프로그램 설명 (text)
    val start_date: String,  // 시작 날짜 (ISO 포맷 문자열로 전달)
    val end_date: String,     // 종료 날짜
//    val id: Int,                // 프로그램 ID TODO - FeedItem id = ProgramId 로 해서 feeditem id로 program 객체를 또 불러와서 가져오는 방식?
    val isStarred: Boolean,     // 즐겨찾기 여부. User와의 relation 이 필요할 듯 한데.. user에게 programStarred / program에 userStarring 으로 id들 보관
    val logo: Int,             // 로고 ID TODO - logoUrl로 변경 필요 // 이건 그냥 program에 포함되어있어도 무방할 url
    val isEligible: Boolean,    // 신청 대상자 여부. 이것도 relation 필요할듯

)*/

data class ProgramPageResponse(
    @SerializedName("content")
    val content: List<ProgramResponse>, // 실제 프로그램 목록

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("isFirst")
    val isFirst: Boolean,

    @SerializedName("isLast")
    val isLast: Boolean,

)

data class ProgramResponse(
    val id: Int,
    val title: String,
    val preview : String,
    val operatingEntity : String,
    val operatingEntityType : String,
    val category : String,
    val categoryValue: String,
    val likeStatus : String?, // LIKED, DISLIKED, null
    val isBookmarked : Boolean,
    val reason : String?,
)

data class ProgramDetailResponse(
    val id: Int,
    val uuid: String,
    val category: String,
    val categoryValue: String,
    val title: String,
    val details: String,
    val summary: String,
    val preview: String,
    val applicationMethod: String,
    val applyUrl: String?,
    val referenceUrl: String?,
    val eligibilityMinAge: Int,
    val eligibilityMaxAge: Int,
    val eligibilityMinHousehold: Int?,
    val eligibilityMaxHousehold: Int?,
    val eligibilityMinIncome: Int?,
    val eligibilityMaxIncome: Int?,
    val eligibilityRegion: String?,
    val eligibilityGender: String?,
    val eligibilityMaritalStatus: String?,
    val eligibilityEducation: String?,
    val eligibilityEmployment: String?,
    val applyStartAt: String?,
    val applyEndAt: String?,
    val createdAt: String?,
    val operatingEntity: String,
    val operatingEntityType : String,
    val likeStatus : String?, // LIKED, DISLIKED, null
    val isBookmarked : Boolean,
)

//data class ProgramSummaryResponse(
//    val id: Int,
//    val title: String,
//    val preview: String,
//    val operatingEntity: String,
//    val category: String,
//    val categoryValue: String
//)

data class PageResponse<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("number")
    val number: Int,

    @SerializedName("first")
    val first: Boolean,

    @SerializedName("last")
    val last: Boolean,

    @SerializedName("empty")
    val empty: Boolean,

    @SerializedName("numberOfElements")
    val numberOfElements: Int = 0,

    @SerializedName("pageable")
    val pageable: Pageable? = null,

    @SerializedName("sort")
    val sort: Sort? = null
)

data class Pageable(
    @SerializedName("pageNumber")
    val pageNumber: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("paged")
    val paged: Boolean,

    @SerializedName("unpaged")
    val unpaged: Boolean,

    @SerializedName("sort")
    val sort: Sort
)

data class Sort(
    @SerializedName("sorted")
    val sorted: Boolean,

    @SerializedName("empty")
    val empty: Boolean,

    @SerializedName("unsorted")
    val unsorted: Boolean
)