package com.example.itda.embedding.service

import com.example.itda.embedding.controller.EmbeddingRequest
import com.example.itda.embedding.controller.EmbeddingResponse
import com.example.itda.embedding.controller.EmbeddingStatusResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class EmbeddingService(
    private val embeddingWebClient: WebClient,
) {
    fun checkServerHealth(): EmbeddingStatusResponse? {
        return embeddingWebClient.get()
            .uri("/v1/health")
            .retrieve()
            .bodyToMono<EmbeddingStatusResponse>()
            .block()
    }

    fun getEmbedding(request: EmbeddingRequest): EmbeddingResponse? {
        return embeddingWebClient.post()
            .uri("/v1/embed")
            .bodyValue(request)
            .retrieve()
            .bodyToMono<EmbeddingResponse>()
            .block()
    }
}
