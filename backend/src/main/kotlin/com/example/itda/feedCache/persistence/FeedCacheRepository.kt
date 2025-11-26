package com.example.itda.feedCache.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface FeedCacheRepository : JpaRepository<FeedCacheEntity, Long> {
    fun findByUserId(userId: String): FeedCacheEntity?
}
