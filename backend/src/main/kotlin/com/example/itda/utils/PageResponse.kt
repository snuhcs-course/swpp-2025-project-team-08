package com.example.itda.utils

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val page: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
) {
    companion object {
        fun <S, T> from(
            page: Page<S>,
            contentMapper: (S) -> T,
        ): PageResponse<T> {
            return PageResponse(
                content = page.content.map(contentMapper),
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                page = page.number,
                size = page.size,
                isFirst = page.isFirst,
                isLast = page.isLast,
            )
        }

        fun <T> from(page: Page<T>): PageResponse<T> {
            return PageResponse(
                content = page.content,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                page = page.number,
                size = page.size,
                isFirst = page.isFirst,
                isLast = page.isLast,
            )
        }

        fun <T> empty(pageable: Pageable): PageResponse<T> {
            return PageResponse(
                content = emptyList(),
                totalPages = 0,
                totalElements = 0,
                page = pageable.pageNumber,
                size = pageable.pageSize,
                isFirst = true,
                isLast = true,
            )
        }
    }
}
