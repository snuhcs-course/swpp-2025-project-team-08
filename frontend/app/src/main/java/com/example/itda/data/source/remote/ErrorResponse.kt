package com.example.itda.data.source.remote

import com.google.gson.annotations.SerializedName

/**
 * 백엔드 에러 응답 모델
 */
data class ErrorResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String
)

/**
 * 에러 타입 정의
 */
sealed class ApiError(val message: String) {
    // 회원가입 관련 에러
    data class InvalidEmail(val msg: String = "이메일 형식이 올바르지 않습니다") : ApiError(msg)
    data class BadPassword(val msg: String = "비밀번호는 8~16자여야 합니다") : ApiError(msg)
    data class EmailConflict(val msg: String = "이미 사용 중인 이메일입니다") : ApiError(msg)

    // 로그인 관련 에러
    data class UserNotFound(val msg: String = "존재하지 않는 계정입니다") : ApiError(msg)
    data class WrongPassword(val msg: String = "비밀번호가 틀렸습니다") : ApiError(msg)

    // 인증 에러
    data class Unauthorized(val msg: String = "로그인이 필요합니다") : ApiError(msg)

    // 기타
    data class Unknown(val msg: String = "알 수 없는 오류가 발생했습니다") : ApiError(msg)
    data class NetworkError(val msg: String = "네트워크 연결을 확인해주세요") : ApiError(msg)
}