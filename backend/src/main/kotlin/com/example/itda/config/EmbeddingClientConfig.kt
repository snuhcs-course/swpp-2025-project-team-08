package com.example.itda.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@Profile("!test")
class EmbeddingClientConfig() {
    @Bean
    fun embeddingWebClient(builder: WebClient.Builder): WebClient {
        val embeddingServerUrl =
            System.getProperty("EMBEDDING_SERVER_URL")
                ?: System.getenv("EMBEDDING_SERVER_URL")
                ?: throw IllegalStateException("EMBEDDING_SERVER_URL is not set!")

        val embeddingServerApiKey =
            System.getProperty("EMBEDDING_SERVER_API_KEY")
                ?: System.getenv("EMBEDDING_SERVER_API_KEY")
                ?: throw IllegalStateException("EMBEDDING_SERVER_API_KEY is not set!")

        return builder
            .baseUrl(embeddingServerUrl)
            .defaultHeader("X-API-Key", embeddingServerApiKey)
            .build()
    }
}
