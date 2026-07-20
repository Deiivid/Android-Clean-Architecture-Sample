package com.example.rickymortydn.core.data.mapper

import com.example.rickymortydn.core.data.network.model.PagedResponseDto
import com.example.rickymortydn.core.model.Page

internal inline fun <T, R> PagedResponseDto<T>.toDomainPage(
    pageNumber: Int,
    transform: (T) -> R,
) = Page(
    items = results.map(transform),
    number = pageNumber,
    totalPages = info.pages,
    totalItems = info.count,
)
