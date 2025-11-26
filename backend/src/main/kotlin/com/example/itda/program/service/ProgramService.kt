package com.example.itda.program.service

import com.example.itda.feedCache.persistence.FeedCacheItem
import com.example.itda.feedCache.service.FeedCacheService
import com.example.itda.program.ProgramNotFoundException
import com.example.itda.program.config.AppConstants
import com.example.itda.program.controller.ProgramCategoryResponse
import com.example.itda.program.controller.ProgramResponse
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.BookmarkEntity
import com.example.itda.program.persistence.BookmarkRepository
import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.ProgramExampleRepository
import com.example.itda.program.persistence.ProgramLikeEntity
import com.example.itda.program.persistence.ProgramLikeRepository
import com.example.itda.program.persistence.ProgramRepository
import com.example.itda.program.persistence.enums.ProgramCategory
import com.example.itda.user.UserNotFoundException
import com.example.itda.user.controller.User
import com.example.itda.user.persistence.UserRepository
import com.example.itda.utils.PageResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

private const val REASON_RATIO = AppConstants.PROGRAM_REASON_RATIO
private const val W_L = AppConstants.CACHE_W_L
private const val W_B = AppConstants.CACHE_W_B

@Service
class ProgramService(
    val feedCacheService: FeedCacheService,
    val programRepository: ProgramRepository,
    val programExampleRepository: ProgramExampleRepository,
    val bookmarkRepository: BookmarkRepository,
    val userRepository: UserRepository,
    val programLikeRepository: ProgramLikeRepository,
) {
    @Transactional
    fun getPrograms(
        user: User,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val allFeedCacheItems = feedCacheService.getUserFeedCache(user.id, category)

        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(allFeedCacheItems.size)

        if (start >= allFeedCacheItems.size) {
            return PageResponse.empty(pageable)
        }

        val feedCacheItems = allFeedCacheItems.subList(start, end)
        val programIds = feedCacheItems.map { it.id }

        val programMap = programRepository.findAllById(programIds).associateBy { it.id }
        val likeStatusMap =
            programLikeRepository.findByUserIdAndProgramIdIn(user.id, programIds)
                .associate { it.program.id to it.isLike }
        val bookmarkedSet = bookmarkRepository.findBookmarkedProgramIds(user.id, programIds).toSet()

        val reasonMap = generateReasonMap(feedCacheItems, likeStatusMap, bookmarkedSet)

        val programSummaryResponses =
            programIds.mapNotNull { id ->
                val entity = programMap[id] ?: return@mapNotNull null

                val isBookmarked = bookmarkedSet.contains(id)
                val likeStatus = likeStatusMap[id]
                val reason = reasonMap[id]

                ProgramSummaryResponse.fromEntity(entity, isBookmarked, likeStatus, reason)
            }
        val programSummaryResponsesPage = PageImpl(programSummaryResponses, pageable, allFeedCacheItems.size.toLong())

        return PageResponse.from(programSummaryResponsesPage)
    }

    private fun generateReasonMap(
        items: List<FeedCacheItem>,
        likeStatusMap: Map<Long, Boolean?>,
        bookmarkedSet: Set<Long>,
    ): Map<Long, String> {
        if (items.isEmpty()) return emptyMap()

        val targetCount = (items.size * REASON_RATIO).toInt()
        if (targetCount == 0) return emptyMap()

        val candidates =
            items.filter { item ->
                item.id !in bookmarkedSet && likeStatusMap[item.id] == null
            }

        val reasonMap = mutableMapOf<Long, String>()

        candidates
            .filter { it.likeContribution >= W_L * 100 }
            .sortedByDescending { it.likeContribution }
            .take(targetCount)
            .forEach { item ->
                reasonMap[item.id] = "Recommended based on your likes (${item.likeContribution}%)"
            }

        candidates
            .filter { it.bookmarkContribution >= W_B * 100 }
            .sortedByDescending { it.bookmarkContribution }
            .filter { it.id !in reasonMap }
            .take(targetCount)
            .forEach { item ->
                reasonMap[item.id] = "Recommended based on your bookmarks (${item.bookmarkContribution}%)"
            }

        return reasonMap
    }

    @Transactional(readOnly = true)
    fun getProgram(
        user: User,
        id: Long,
    ): ProgramResponse {
        val program = programRepository.findByIdOrNull(id) ?: throw ProgramNotFoundException()

        val likeStatus = programLikeRepository.findByUserIdAndProgramId(user.id, program.id)?.isLike
        val isBookmarked = bookmarkRepository.existsByUserIdAndProgramId(user.id, program.id)

        return ProgramResponse.fromEntity(program, likeStatus, isBookmarked)
    }

    fun getProgramCategories(): List<ProgramCategoryResponse> {
        return ProgramCategory.entries.map(ProgramCategoryResponse::fromEntity)
    }

    fun getProgramExamples(): List<ProgramSummaryResponse> {
        return programExampleRepository.findAll().map(ProgramSummaryResponse::fromEntity)
    }

    fun getProgramExample(id: Long): ProgramResponse {
        val program =
            programExampleRepository.findByIdOrNull(id) ?: throw ProgramNotFoundException()
        return ProgramResponse.fromEntity(program)
    }

    @Transactional(readOnly = true)
    fun searchLatestPrograms(
        user: User,
        searchTerm: String,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val programsPage: Page<ProgramEntity> =
            programRepository.searchLatest(
                query = searchTerm,
                category = category,
                pageable = pageable,
            )

        if (programsPage.isEmpty) {
            return PageResponse.empty(pageable)
        }

        val programIds = programsPage.content.map { it.id }
        val likeStatusMap =
            programLikeRepository.findByUserIdAndProgramIdIn(user.id, programIds)
                .associate { it.program.id to it.isLike }
        val bookmarkedSet = bookmarkRepository.findBookmarkedProgramIds(user.id, programIds).toSet()

        val programSummaryResponsesPage =
            programsPage.map { entity ->
                val isBookmarked = bookmarkedSet.contains(entity.id)
                val likeStatus = likeStatusMap[entity.id]

                ProgramSummaryResponse.fromEntity(
                    entity = entity,
                    isBookmarked = isBookmarked,
                    likeStatus = likeStatus,
                    reason = null,
                )
            }

        return PageResponse.from(programSummaryResponsesPage)
    }

//    private fun mapSearchTermToCategory(searchTerm: String): ProgramCategory? {
//        val matchedByName =
//            ProgramCategory.entries
//                .find { it.name.equals(searchTerm, ignoreCase = true) }
//        if (matchedByName != null) return matchedByName
//
//        return ProgramCategory.entries
//            .find { it.value.contains(searchTerm) }
//    }

    @Transactional
    fun searchProgramsByRank(
        user: User,
        searchTerm: String,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val programsPage: Page<ProgramEntity> =
            programRepository.searchByRank(
                query = searchTerm,
                category = category,
                pageable = pageable,
            )

        if (programsPage.isEmpty) {
            return PageResponse.empty(pageable)
        }

        val programIds = programsPage.content.map { it.id }
        val likeStatusMap =
            programLikeRepository.findByUserIdAndProgramIdIn(user.id, programIds)
                .associate { it.program.id to it.isLike }
        val bookmarkedSet = bookmarkRepository.findBookmarkedProgramIds(user.id, programIds).toSet()

        val programSummaryResponsesPage =
            programsPage.map { entity ->
                val isBookmarked = bookmarkedSet.contains(entity.id)
                val likeStatus = likeStatusMap[entity.id]

                ProgramSummaryResponse.fromEntity(
                    entity = entity,
                    isBookmarked = isBookmarked,
                    likeStatus = likeStatus,
                    reason = null,
                )
            }

        return PageResponse.from(programSummaryResponsesPage)
    }

    @Transactional
    fun bookmarkProgram(
        userId: String,
        programId: Long,
    ) {
        val programEntity = programRepository.findByIdWithWriteLock(programId) ?: throw ProgramNotFoundException()
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (programEntity.bookmarks.any { it.user.id == userEntity.id }) {
            return
        }
        val bookmarkEntity =
            bookmarkRepository.save(
                BookmarkEntity(program = programEntity, user = userEntity, createdAt = OffsetDateTime.now()),
            )
        programEntity.bookmarks.add(bookmarkEntity)
        userEntity.bookmarks.add(bookmarkEntity)
    }

    @Transactional
    fun unbookmarkProgram(
        userId: String,
        programId: Long,
    ) {
        val programEntity = programRepository.findByIdWithWriteLock(programId) ?: throw ProgramNotFoundException()
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val bookmarkToDelete = programEntity.bookmarks.find { it.user.id == userEntity.id } ?: return
        programEntity.bookmarks.remove(bookmarkToDelete)
        userEntity.bookmarks.remove(bookmarkToDelete)
        bookmarkRepository.delete(bookmarkToDelete)
    }

    @Transactional
    fun likeProgram(
        userId: String,
        programId: Long,
        isLike: Boolean,
    ) {
        val programEntity = programRepository.findByIdWithWriteLock(programId) ?: throw ProgramNotFoundException()
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (programEntity.programLikes.any { it.user.id == userEntity.id }) {
            return
        }
        val programLikeEntity =
            programLikeRepository.save(
                ProgramLikeEntity(
                    program = programEntity,
                    user = userEntity,
                    isLike = isLike,
                    createdAt = OffsetDateTime.now(),
                ),
            )
        programEntity.programLikes.add(programLikeEntity)
    }

    @Transactional
    fun unLikeProgram(
        userId: String,
        programId: Long,
    ) {
        val programEntity = programRepository.findByIdWithWriteLock(programId) ?: throw ProgramNotFoundException()
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val programLikeToDelete = programEntity.programLikes.find { it.user.id == userEntity.id } ?: return
        programEntity.programLikes.remove(programLikeToDelete)
        programLikeRepository.delete(programLikeToDelete)
    }
}
