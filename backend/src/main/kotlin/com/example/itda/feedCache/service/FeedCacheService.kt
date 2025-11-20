package com.example.itda.feedCache.service

import com.example.itda.feedCache.persistence.FeedCacheEntity
import com.example.itda.feedCache.persistence.FeedCacheRepository
import com.example.itda.program.config.AppConstants
import com.example.itda.program.persistence.ProgramRepository
import com.example.itda.user.UserNotFoundException
import com.example.itda.user.persistence.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

private const val CACHE_EXPIRY_HOURS: Long = 1
private const val W_U = 0.3f
private const val W_L = 0.2f
private const val W_B = 0.2f
private const val W_S = 0.3f
private const val EMBEDDING_DIMENSION = AppConstants.EMBEDDING_DIMENSION
private const val TOP_N_PROGRAMS = 500

@Service
class FeedCacheService(
    private val feedCacheRepository: FeedCacheRepository,
    private val userRepository: UserRepository,
    private val programRepository: ProgramRepository,
) {
    fun getUserRecommendedProgramIds(userId: String): List<Long> {
        val cache = feedCacheRepository.findByUserId(userId)

        val isCacheExpired = cache == null || cache.updatedAt.plus(CACHE_EXPIRY_HOURS, ChronoUnit.HOURS).isBefore(OffsetDateTime.now())

        if (isCacheExpired) {
            return generateAndCacheUserFeed(userId)
        }

        return cache!!.programIds.toList()
    }

    // 캐시 테이블 갱신(생성)
    fun generateAndCacheUserFeed(userId: String): List<Long> {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val vVector = calculateVActor(userId)

        val allPrograms = programRepository.findAll()

        val likesEmbedding = userEntity.likesEmbedding ?: FloatArray(EMBEDDING_DIMENSION) { 0f }
        val bookmarksEmbedding = userEntity.bookmarksEmbedding ?: FloatArray(EMBEDDING_DIMENSION) { 0f }

        val rankedResult =
            allPrograms
                .mapNotNull { program ->
                    val programEmbedding = program.embedding
                    val programId = program.id ?: return@mapNotNull null

                    // 1. V x Program Score (랭킹 점수) 계산
                    var vScore = 0f
                    for (i in 0 until EMBEDDING_DIMENSION) {
                        vScore += vVector[i] * programEmbedding[i]
                    }

                    // 2. Liked Score (SL) 및 Bookmarked Score (SB) 계산
                    var sL = 0f
                    var sB = 0f
                    for (i in 0 until EMBEDDING_DIMENSION) {
                        sL += programEmbedding[i] * (likesEmbedding[i] * W_L)
                        sB += programEmbedding[i] * (bookmarksEmbedding[i] * W_B)
                    }

                    // 3. 비율 (Ratio) 계산: L 비율 or 0
                    val slSbSum = sL + sB
                    val ratio =
                        if (slSbSum > 0) {
                            sL / slSbSum
                        } else {
                            0.0f
                        }

                    Triple(vScore, programId, ratio)
                }
                .sortedByDescending { it.first }

        val topNResult = rankedResult.take(TOP_N_PROGRAMS)

        val topNProgramIds = LongArray(topNResult.size) { i -> topNResult[i].second }
        val topNProgramRatios = FloatArray(topNResult.size) { i -> topNResult[i].third }

        var existingCache = feedCacheRepository.findByUserId(userId)

        if (existingCache != null) {
            existingCache.programIds = topNProgramIds
            existingCache.likeRatios = topNProgramRatios
            existingCache.updatedAt = OffsetDateTime.now()
        } else {
            existingCache =
                FeedCacheEntity(
                    user = userEntity,
                    programIds = topNProgramIds,
                    likeRatios = topNProgramRatios,
                    updatedAt = OffsetDateTime.now(),
                )
        }

        feedCacheRepository.save(existingCache)

        return topNProgramIds.toList()
    }

    fun calculateVActor(userId: String): FloatArray {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val userEmbedding = userEntity.userEmbedding ?: FloatArray(EMBEDDING_DIMENSION) { 0f }
        val likesEmbedding = userEntity.likesEmbedding ?: FloatArray(EMBEDDING_DIMENSION) { 0f }
        val bookmarksEmbedding = userEntity.bookmarksEmbedding ?: FloatArray(EMBEDDING_DIMENSION) { 0f }
        val seeLessEmbedding = userEntity.seeLessEmbedding ?: FloatArray(EMBEDDING_DIMENSION) { 0f }

        val vVector = FloatArray(EMBEDDING_DIMENSION) { 0f }
        for (i in 0 until EMBEDDING_DIMENSION) {
            vVector[i] += (userEmbedding[i] * W_U)
            vVector[i] += (likesEmbedding[i] * W_L)
            vVector[i] += (bookmarksEmbedding[i] * W_B)
            vVector[i] -= (seeLessEmbedding[i] * W_S)
        }
        return vVector
    }
}
