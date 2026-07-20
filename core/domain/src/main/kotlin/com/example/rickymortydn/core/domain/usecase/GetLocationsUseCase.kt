package com.example.rickymortydn.core.domain.usecase

import com.example.rickymortydn.core.domain.repository.LocationRepository
import com.example.rickymortydn.core.model.Location
import com.example.rickymortydn.core.model.Page

class GetLocationsUseCase(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(page: Int = 1): Result<Page<Location>> =
        repository.getLocations(page)
}
