package com.example.itda.data.model

import com.google.gson.annotations.SerializedName

/**
 * 카카오 주소 검색 응답
 */
data class KakaoAddressResponse(
    @SerializedName("documents")
    val documents: List<AddressDocument>,

    @SerializedName("meta")
    val meta: Meta
)

/**
 * 주소 문서
 */
data class AddressDocument(
    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("address_type")
    val addressType: String,  // "REGION" or "ROAD"

    @SerializedName("x")
    val longitude: String,

    @SerializedName("y")
    val latitude: String,

    @SerializedName("address")
    val address: Address?,

    @SerializedName("road_address")
    val roadAddress: RoadAddress?
)

/**
 * 지번 주소
 */
data class Address(
    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("region_1depth_name")
    val region1depthName: String,  // 시도

    @SerializedName("region_2depth_name")
    val region2depthName: String,  // 구

    @SerializedName("region_3depth_name")
    val region3depthName: String,  // 동

    @SerializedName("mountain_yn")
    val mountainYn: String,

    @SerializedName("main_address_no")
    val mainAddressNo: String,

    @SerializedName("sub_address_no")
    val subAddressNo: String,

    @SerializedName("zip_code")
    val zipCode: String?
)

/**
 * 도로명 주소
 */
data class RoadAddress(
    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("region_1depth_name")
    val region1depthName: String,

    @SerializedName("region_2depth_name")
    val region2depthName: String,

    @SerializedName("region_3depth_name")
    val region3depthName: String,

    @SerializedName("road_name")
    val roadName: String,

    @SerializedName("underground_yn")
    val undergroundYn: String,

    @SerializedName("main_building_no")
    val mainBuildingNo: String,

    @SerializedName("sub_building_no")
    val subBuildingNo: String,

    @SerializedName("building_name")
    val buildingName: String,

    @SerializedName("zone_no")
    val zoneNo: String  // 우편번호 (5자리)
)

/**
 * 메타 정보
 */
data class Meta(
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("pageable_count")
    val pageableCount: Int,

    @SerializedName("is_end")
    val isEnd: Boolean
)