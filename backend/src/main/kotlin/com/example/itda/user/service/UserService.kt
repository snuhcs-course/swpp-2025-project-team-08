package com.example.itda.user.service

import com.example.itda.user.persistence.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
)
