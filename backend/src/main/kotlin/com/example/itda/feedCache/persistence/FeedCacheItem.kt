package com.example.itda.feedCache.persistence

data class FeedCacheItem(
    val id: Long,
    val score: Float,
    val likeContribution: Int,
    val bookmarkContribution: Int,
)
