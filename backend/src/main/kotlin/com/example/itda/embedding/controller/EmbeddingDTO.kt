package com.example.itda.embedding.controller

data class EmbeddingRequest(val text: String)

data class EmbeddingResponse(val embedding: List<Float>)

data class EmbeddingStatusResponse(
    val status: String,
    val model_loaded: Boolean,
)
