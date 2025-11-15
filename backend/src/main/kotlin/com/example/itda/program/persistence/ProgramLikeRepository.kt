package com.example.itda.program.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProgramLikeRepository : JpaRepository<ProgramLikeEntity, Long>
