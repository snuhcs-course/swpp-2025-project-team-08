package com.example.itda.user.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Int> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): UserEntity?
}
