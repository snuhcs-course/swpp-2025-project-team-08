package com.example.itda.data.model

import com.example.itda.ui.auth.components.formatBirthDate
import com.example.itda.ui.auth.components.isValidBirthDate

/**
 * 프로필 업데이트 요청 객체
 */
@ConsistentCopyVisibility
data class ProfileUpdateRequest private constructor(
    val name: String,
    val birthDate: String?,
    val gender: String?,
    val address: String?,
    val postcode: String?,
    val maritalStatus: String?,
    val educationLevel: String?,
    val householdSize: Int?,
    val householdIncome: Int?,
    val employmentStatus: String?,
    val tags: List<String>?
) {
    class Builder {
        private var name: String = ""
        private var birthDate: String? = null
        private var gender: String? = null
        private var address: String? = null
        private var postcode: String? = null
        private var maritalStatus: String? = null
        private var educationLevel: String? = null
        private var householdSize: Int? = null
        private var householdIncome: Int? = null
        private var employmentStatus: String? = null
        private var tags: List<String>? = null

        fun name(name: String) = apply { this.name = name }
        fun birthDate(birthDate: String?) = apply { this.birthDate = birthDate }
        fun gender(gender: String?) = apply { this.gender = gender }
        fun address(address: String?) = apply { this.address = address }
        fun postcode(postcode: String?) = apply { this.postcode = postcode }
        fun maritalStatus(status: String?) = apply { this.maritalStatus = status }
        fun educationLevel(level: String?) = apply { this.educationLevel = level }
        fun householdSize(size: Int?) = apply { this.householdSize = size }
        fun householdIncome(income: Int?) = apply { this.householdIncome = income }
        fun employmentStatus(status: String?) = apply { this.employmentStatus = status }
        fun tags(tags: List<String>?) = apply { this.tags = tags }

        /**
         * 유효성 검사를 수행 -> ProfileUpdateRequest 객체를 생성
         */
        fun build(): Result<ProfileUpdateRequest> {
            return runCatching {
                // 필수 항목 검사
                require(name.isNotBlank()) { "성함을 입력해주세요" }
                require(!birthDate.isNullOrBlank()) { "생년월일을 입력해주세요" }
                require(!gender.isNullOrBlank()) { "성별을 선택해주세요" }
                require(!address.isNullOrBlank()) { "주소를 입력해주세요" }
                require(!postcode.isNullOrBlank()) { "우편번호를 입력해주세요" }
                require(birthDate!!.length == 8) { "생년월일은 8자리를 입력해주세요" }
                require(isValidBirthDate(birthDate!!)) { "올바른 생년월일을 입력해주세요" }

                val formattedBirthDate = formatBirthDate(birthDate!!)

                ProfileUpdateRequest(
                    name = name,
                    birthDate = formattedBirthDate,
                    gender = gender,
                    address = address,
                    postcode = postcode,
                    maritalStatus = maritalStatus,
                    educationLevel = educationLevel,
                    householdSize = householdSize,
                    householdIncome = householdIncome,
                    employmentStatus = employmentStatus,
                    tags = tags
                )
            }
        }
    }

    companion object {
        fun builder() = Builder()
    }
}