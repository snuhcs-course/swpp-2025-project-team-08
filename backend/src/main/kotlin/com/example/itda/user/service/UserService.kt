package com.example.itda.user.service

import com.example.itda.embedding.service.EmbeddingService
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.BookmarkRepository
import com.example.itda.program.persistence.ProgramExampleRepository
import com.example.itda.program.persistence.enums.BookmarkSortType
import com.example.itda.user.AuthenticateException
import com.example.itda.user.InvalidBirthDateFormatException
import com.example.itda.user.LogInInvalidPasswordException
import com.example.itda.user.RefreshTokenException
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
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.collections.addAll
import kotlin.text.clear

@Service
class UserService(
    private val userRepository: UserRepository,
    private val programExampleRepository: ProgramExampleRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val embeddingService: EmbeddingService,
) {
    @Transactional
    fun authenticate(accessToken: String): User {
        val id = UserAccessTokenUtil.validateAccessTokenGetUserId(accessToken) ?: throw AuthenticateException()
        val user = userRepository.findByIdOrNull(id) ?: throw AuthenticateException()
        return User.fromEntity(user)
    }

    @Transactional
    fun refresh(refreshToken: String): AuthResponse {
        val id = UserAccessTokenUtil.validateAccessTokenGetUserId(refreshToken) ?: throw RefreshTokenException()
        userRepository.findByIdOrNull(id) ?: throw AuthenticateException()

        val newAccessToken = UserAccessTokenUtil.generateAccessToken(id)
        val newRefreshToken = UserAccessTokenUtil.generateRefreshToken(id)
        val expiresIn = UserAccessTokenUtil.getAccessTokenExpirationSeconds()

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            tokenType = "Bearer",
            expiresIn = expiresIn,
        )
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

        val userText = generateUserText(userEntity)
        val embedding = embeddingService.getEmbedding(userText)

        userEntity.embedding = embedding

        userRepository.save(userEntity)
    }

    fun generateUserText(userEntity: UserEntity): String {
        val sb = StringBuilder()

        val age =
            userEntity.birthDate?.let {
                Period.between(it, LocalDate.now()).years
            } ?: 0

        val genderStr = userEntity.gender?.value ?: "사람"

        sb.append("저는 ${age}세 ${genderStr}입니다. ")

        userEntity.address?.let {
            sb.append("저는 ${it}에 거주하고 있습니다. ")
        }

        userEntity.employmentStatus?.let {
            sb.append("저의 고용 상태는 ${it.value}입니다. ")
        }

        userEntity.maritalStatus?.let {
            sb.append("저의 결혼 상태는 ${it.value}입니다. ")
        }

        userEntity.householdSize?.let {
            sb.append("저는 ${it}인 가구입니다. ")
        }

        userEntity.householdIncome?.let {
            sb.append("가구 소득은 연 ${it}만원입니다. ")
        }

        if (userEntity.tags.isNotEmpty()) {
            val interests = userEntity.tags.joinToString(", ") { it.name }
            sb.append("저는 $interests 분야에 관심이 있습니다. ")
        }

        sb.append("저는 제 상황에 맞는 정부 지원금과 복지 혜택, 공공 지원 프로그램을 찾고 있습니다.")

        return sb.toString().trim()
    }

    @Transactional
    fun updateUserPreferences(
        userId: String,
        request: List<PreferenceRequest>,
    ) {
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
