package com.example.itda.data.model

import com.google.gson.annotations.SerializedName


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
    val likeStatus : String? = null, // LIKED, DISLIKED, null
    val isBookmarked : Boolean =  false,
    val reason : String? = null,
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

val dummyProgramDetailResponse = ProgramDetailResponse(
    id = 0,
    uuid = "",
    category = "",
    categoryValue = "",
    title = "",
    details = "",
    summary = "",
    preview = "",
    applicationMethod = "",
    applyUrl = "",
    referenceUrl = "",
    eligibilityMinAge = 0,
    eligibilityMaxAge = 0,
    eligibilityMinHousehold = 0,
    eligibilityMaxHousehold = 0,
    eligibilityMinIncome = 0,
    eligibilityMaxIncome = 0,
    eligibilityRegion = "",
    eligibilityGender = "",
    eligibilityMaritalStatus = "",
    eligibilityEducation = "",
    eligibilityEmployment = "",
    applyStartAt = "",
    applyEndAt = "",
    createdAt = "",
    operatingEntity = "",
    operatingEntityType = "",
    likeStatus = null, // LIKED, DISLIKED, null
    isBookmarked = false,
)