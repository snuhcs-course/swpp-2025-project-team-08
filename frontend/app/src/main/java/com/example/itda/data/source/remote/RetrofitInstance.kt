package com.example.itda.data.source.remote

import com.example.itda.data.source.local.PrefDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://35.216.102.140/api/v1/"
    private var prefDataSource: PrefDataSource? = null

    fun init(dataSource: PrefDataSource) {
        prefDataSource = dataSource
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Auth Interceptor - Authorization 헤더 추가
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // /auth/ 경로는 토큰 불필요 (로그인, 회원가입)
        val needsAuth = !originalRequest.url.encodedPath.contains("/auth/")

        if (!needsAuth) {
            return@Interceptor chain.proceed(originalRequest)
        }

        // 토큰 가져오기
        val token = runBlocking {
            prefDataSource?.getAccessToken()
        }

        // Authorization 헤더 추가
        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    // Retrofit 객체
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API 인터페이스
    val authAPI: AuthAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val programAPI: ProgramAPI by lazy {
        retrofit.create(ProgramAPI::class.java)
    }

    val userAPI: UserAPI by lazy {
        retrofit.create(UserAPI::class.java)
    }
}
