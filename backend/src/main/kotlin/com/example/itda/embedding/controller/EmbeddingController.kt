package com.example.itda.embedding.controller

import com.example.itda.embedding.service.EmbeddingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/embedding-test")
class EmbeddingController(
    private val embeddingService: EmbeddingService,
) {
    @GetMapping("/health")
    fun checkHealth(): Any? {
        return embeddingService.checkServerHealth()
    }

    @PostMapping("/embed")
    fun getEmbedding(
        @RequestBody request: EmbeddingRequest,
    ): Any? {
        return embeddingService.getEmbedding(request)
    }
}
