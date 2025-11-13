package com.example.itda

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@Profile("test")
class TestConfig {
    @Bean
    fun embeddingWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl("http://dummy-test-server.com").build()
    }
}
