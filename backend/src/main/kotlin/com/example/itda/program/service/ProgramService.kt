package com.example.itda.program.service

import com.example.itda.program.ProgramNotFoundException
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
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class ProgramService(
    val programRepository: ProgramRepository,
    val programExampleRepository: ProgramExampleRepository,
    val bookmarkRepository: BookmarkRepository,
    val userRepository: UserRepository,
    val programLikeRepository: ProgramLikeRepository,
) {
    @Transactional(readOnly = true)
    fun getPrograms(
        user: User,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val programs = programRepository.findAllByPreference(user.id, category?.toString(), pageable)

        return PageResponse.from(programs, ProgramSummaryResponse::fromEntity)
    }

    @Transactional(readOnly = true)
    fun getProgram(id: Long): ProgramResponse {
        val program = programRepository.findByIdOrNull(id) ?: throw ProgramNotFoundException()

        return ProgramResponse.fromEntity(program)
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
        searchTerm: String,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val programs: Page<ProgramEntity> =
            programRepository.searchLatest(
                query = searchTerm,
                category = category,
                pageable = pageable,
            )

        return PageResponse.from(programs, ProgramSummaryResponse::fromEntity)
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
        searchTerm: String,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val programs: Page<ProgramEntity> =
            programRepository.searchByRank(
                query = searchTerm,
                category = category,
                pageable = pageable,
            )

        return PageResponse.from(programs, ProgramSummaryResponse::fromEntity)
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
                ProgramLikeEntity(program = programEntity, user = userEntity, isLike = isLike, createdAt = OffsetDateTime.now()),
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
