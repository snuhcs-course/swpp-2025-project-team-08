package com.example.itda.program.config

object AppConstants {
    const val EMBEDDING_DIMENSION = 1024

    const val CACHE_EXPIRY_HOURS: Long = 1
    const val CACHE_RECENT_PROGRAM_LIMIT = 10
    const val CACHE_W_U = 0.3f
    const val CACHE_W_L = 0.2f
    const val CACHE_W_B = 0.2f
    const val CACHE_W_S = 0.3f

    const val CACHE_CORRECTION = 0.2f

    const val PROGRAM_REASON_RATIO = 0.1
}
