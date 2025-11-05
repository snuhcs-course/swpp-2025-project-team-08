package com.example.itda.program.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProgramExampleRepository : JpaRepository<ProgramExampleEntity, Long>
