package com.example.itda.data.source.remote

import android.util.Log
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

object ApiErrorParser {
    private const val TAG = "ApiErrorParser"

    fun parseError(exception: Throwable): ApiError {

        return when (exception) {
            is HttpException -> {
                try {
                    val errorBody = exception.response()?.errorBody()?.string()

                    if (errorBody.isNullOrBlank()) {
                        return ApiError.Unknown()
                    }

                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

                    val result = parseErrorResponse(errorResponse)
                    result
                } catch (e: Exception) {
                    ApiError.Unknown()
                }
            }
            is IOException -> {
                ApiError.NetworkError()
            }
            else -> {
                ApiError.Unknown()
            }
        }
    }

    private fun parseErrorResponse(errorResponse: ErrorResponse): ApiError {
        val message = errorResponse.message
        val code = errorResponse.code

        return when {
            message.contains("Wrong password", ignoreCase = true) -> {
                ApiError.WrongPassword()
            }
            message.contains("User not found", ignoreCase = true) -> {
                ApiError.UserNotFound()
            }
            message.contains("Invalid email format", ignoreCase = true) -> {
                ApiError.InvalidEmail()
            }
            message.contains("Password's length should be", ignoreCase = true) -> {
                ApiError.BadPassword()
            }
            message.contains("Email conflict", ignoreCase = true) -> {
                ApiError.EmailConflict()
            }
            message.contains("Authenticate failed", ignoreCase = true) -> {
                ApiError.Unauthorized()
            }
            code == "UNAUTHORIZED" -> {
                ApiError.Unauthorized()
            }
            else -> {
                ApiError.Unknown()
            }
        }
    }
}