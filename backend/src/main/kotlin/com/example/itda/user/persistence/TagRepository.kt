package com.example.itda.user.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, String>
