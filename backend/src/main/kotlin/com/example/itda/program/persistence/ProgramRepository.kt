package com.example.itda.program.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProgramRepository : JpaRepository<ProgramEntity, Long>
