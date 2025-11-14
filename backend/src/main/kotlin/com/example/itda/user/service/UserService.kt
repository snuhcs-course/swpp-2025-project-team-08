package com.example.itda.user.service

import com.example.itda.program.ProgramNotFoundException
import com.example.itda.program.config.AppConstants
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.BookmarkRepository
import com.example.itda.program.persistence.ProgramExampleRepository
import com.example.itda.program.persistence.enums.BookmarkSortType
import com.example.itda.user.AuthenticateException
import com.example.itda.user.InvalidBirthDateFormatException
import com.example.itda.user.LogInInvalidPasswordException
import com.example.itda.user.SignUpBadPasswordException
import com.example.itda.user.SignUpEmailConflictException
import com.example.itda.user.SignUpInvalidEmailException
import com.example.itda.user.UserAccessTokenUtil
import com.example.itda.user.UserNotFoundException
import com.example.itda.user.controller.AuthResponse
import com.example.itda.user.controller.PreferenceRequest
import com.example.itda.user.controller.ProfileRequest
import com.example.itda.user.controller.User
import com.example.itda.user.persistence.TagEntity
import com.example.itda.user.persistence.TagRepository
import com.example.itda.user.persistence.UserEntity
import com.example.itda.user.persistence.UserRepository
import com.example.itda.utils.PageResponse
import org.mindrot.jbcrypt.BCrypt
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.collections.addAll
import kotlin.text.clear

@Service
class UserService(
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val programExampleRepository: ProgramExampleRepository,
    private val bookmarkRepository: BookmarkRepository,
) {
    @Transactional
    fun authenticate(accessToken: String): User {
        val id = UserAccessTokenUtil.validateAccessTokenGetUserId(accessToken) ?: throw AuthenticateException()
        val user = userRepository.findByIdOrNull(id) ?: throw AuthenticateException()
        return User.fromEntity(user)
    }

    @Transactional
    fun signUp(
        email: String,
        password: String,
    ): AuthResponse {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!emailRegex.matches(email)) {
            throw SignUpInvalidEmailException()
        }
        if (password.length < 8 || password.length > 16) {
            throw SignUpBadPasswordException()
        }
        if (userRepository.existsByEmail(email)) {
            throw SignUpEmailConflictException()
        }

        val encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val userEntity =
            userRepository.save(
                UserEntity(
                    email = email,
                    password = encryptedPassword,
                ),
            )

        val accessToken = UserAccessTokenUtil.generateAccessToken(userEntity.id!!)
        val refreshToken = UserAccessTokenUtil.generateRefreshToken(userEntity.id!!)
        val expiresIn = UserAccessTokenUtil.getAccessTokenExpirationSeconds()
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = expiresIn,
        )
    }

    @Transactional
    fun logIn(
        email: String,
        password: String,
    ): AuthResponse {
        val userEntity = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        if (!BCrypt.checkpw(password, userEntity.password)) {
            throw LogInInvalidPasswordException()
        }
        val accessToken = UserAccessTokenUtil.generateAccessToken(userEntity.id!!)
        val refreshToken = UserAccessTokenUtil.generateRefreshToken(userEntity.id!!)
        val expiresIn = UserAccessTokenUtil.getAccessTokenExpirationSeconds()
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = expiresIn,
        )
    }

    @Transactional
    fun getProfile(userId: String): User {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        return User.fromEntity(userEntity)
    }

    @Transactional
    fun updateProfile(
        userId: String,
        request: ProfileRequest,
    ) {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        request.name?.let { userEntity.name = it }
        request.birthDate?.let {
            userEntity.birthDate =
                try {
                    LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                } catch (e: DateTimeParseException) {
                    throw InvalidBirthDateFormatException()
                }
        }
        request.gender?.let { userEntity.gender = it }
        request.address?.let { userEntity.address = it }
        request.postcode?.let { userEntity.postcode = it }
        request.maritalStatus?.let { userEntity.maritalStatus = it }
        request.educationLevel?.let { userEntity.educationLevel = it }
        request.householdSize?.let { userEntity.householdSize = it }
        request.householdIncome?.let { userEntity.householdIncome = it }
        request.employmentStatus?.let { userEntity.employmentStatus = it }

        request.tags?.let { newTags ->
            userEntity.tags.clear()

            val tagEntities =
                newTags.map { tagName ->
                    TagEntity().apply {
                        name = tagName
                        user = userEntity
                    }
                }.toMutableSet()

            userEntity.tags.addAll(tagEntities)
        }

        userRepository.save(userEntity)
    }

    @Transactional
    fun updateUserPreferences(
        userId: String,
        request: List<PreferenceRequest>,
    ) {
        val preferenceEmbedding = FloatArray(AppConstants.EMBEDDING_DIMENSION) { 0.0f }
        val scoreSum = request.sumOf { r -> r.score }

        request.forEach { r ->
            val programEntity = programExampleRepository.findByIdOrNull(r.id) ?: throw ProgramNotFoundException()
            val embedding = programEntity.embedding
            val weight = r.score.toFloat() / scoreSum

            for (i in embedding.indices) {
                preferenceEmbedding[i] += (embedding[i] * weight)
            }
        }

        val userEntity: UserEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        userEntity.embedding = preferenceEmbedding
        userRepository.save(userEntity)
    }

    @Transactional
    fun getBookmarkedPrograms(
        user: User,
        sortType: BookmarkSortType,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()

        val bookmarkPage =
            when (sortType) {
                BookmarkSortType.LATEST -> {
                    val sort = Sort.by(Sort.Direction.DESC, "createdAt")
                    val customPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)
                    bookmarkRepository.findByUserWithProgram(userEntity, customPageable)
                }

                BookmarkSortType.DEADLINE -> {
                    val basePageable = PageRequest.of(pageable.pageNumber, pageable.pageSize)
                    bookmarkRepository.findByUserWithProgramOrderByDeadline(userEntity, basePageable)
                }
            }

        return PageResponse.from(bookmarkPage) { bookmarkEntity ->
            ProgramSummaryResponse.fromEntity(bookmarkEntity.program)
        }
    }
}
