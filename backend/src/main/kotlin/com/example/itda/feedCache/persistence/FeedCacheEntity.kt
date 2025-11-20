package com.example.itda.feedCache.persistence

import com.example.itda.user.persistence.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "feed_cache")
class FeedCacheEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: UserEntity,
    @Column(name = "program_ids", nullable = false)
    @JdbcTypeCode(SqlTypes.ARRAY)
    var programIds: LongArray,
    @Column(name = "like_ratios", nullable = false)
    @JdbcTypeCode(SqlTypes.ARRAY)
    var likeRatios: FloatArray,
    @Column(
        name = "updated_at",
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP",
    )
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
