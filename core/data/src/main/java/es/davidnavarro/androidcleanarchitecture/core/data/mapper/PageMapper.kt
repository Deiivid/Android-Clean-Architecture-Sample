package es.davidnavarro.androidcleanarchitecture.core.data.mapper

import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PagedResponseDto
import es.davidnavarro.androidcleanarchitecture.core.model.Page

internal inline fun <T, R> PagedResponseDto<T>.toDomainPage(pageNumber: Int, transform: (T) -> R) = Page(
    items = results.map(transform),
    number = pageNumber,
    totalPages = info.pages,
    totalItems = info.count
)
