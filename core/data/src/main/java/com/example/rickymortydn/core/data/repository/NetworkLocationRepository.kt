package com.example.rickymortydn.core.data.repository

import com.example.rickymortydn.core.data.mapper.toDomain
import com.example.rickymortydn.core.data.mapper.toDomainPage
import com.example.rickymortydn.core.data.network.RickAndMortyApi
import com.example.rickymortydn.core.domain.repository.LocationRepository
import com.example.rickymortydn.core.model.Location
import com.example.rickymortydn.core.model.Page
import javax.inject.Inject

internal class NetworkLocationRepository @Inject constructor(
    private val api: RickAndMortyApi,
) : LocationRepository {
    override suspend fun getLocations(page: Int): Result<Page<Location>> = runCatching {
        api.getLocations(page).toDomainPage(page) { it.toDomain() }
    }
}
