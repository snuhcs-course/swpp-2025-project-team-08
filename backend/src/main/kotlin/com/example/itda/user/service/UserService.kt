package com.example.itda.user.service

import com.example.itda.program.ProgramNotFoundException
import com.example.itda.program.persistence.ProgramRepository
import com.example.itda.user.AuthenticateException
import com.example.itda.user.InvalidBirthDateFormatException
import com.example.itda.user.LogInInvalidPasswordException
import com.example.itda.user.SignUpBadPasswordException
import com.example.itda.user.SignUpEmailConflictException
import com.example.itda.user.SignUpInvalidEmailException
import com.example.itda.user.UserAccessTokenUtil
import com.example.itda.user.UserNotFoundException
import com.example.itda.user.controller.AuthResponse
import com.example.itda.user.controller.ProfileRequest
import com.example.itda.user.controller.User
import com.example.itda.user.persistence.UserEntity
import com.example.itda.user.persistence.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val programRepository: ProgramRepository,
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
        request.maritalStatus?.let { userEntity.maritalStatus = it }
        request.educationLevel?.let { userEntity.educationLevel = it }
        request.householdSize?.let { userEntity.householdSize = it }
        request.householdIncome?.let { userEntity.householdIncome = it }
        request.employmentStatus?.let { userEntity.employmentStatus = it }
        userRepository.save(userEntity)
    }

    @Transactional
    fun updateUserPreferenceVector(
        userId: String,
        satisfactionScores: List<Int>,
    ) {
        val programIds: List<Long> = listOf(26, 27, 28, 29, 30)
        val programEmbeddings: List<FloatArray> =
            programIds.map { id ->
                val programEntity = programRepository.findByIdOrNull(id) ?: throw ProgramNotFoundException()
                programEntity.embedding
            }

        val finalVector = FloatArray(1024) { 0.0f }

        for (index in 0 until 5) {
            val score = satisfactionScores[index]
            val vector = programEmbeddings[index]

            for (i in vector.indices) {
                finalVector[i] += vector[i] * score
            }
        }

        val userEntity: UserEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        userEntity.preferenceEmbedding = finalVector
        userRepository.save(userEntity)
    }
}
