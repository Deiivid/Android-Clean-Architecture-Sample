package com.example.rickymortydn.core.domain.repository

import com.example.rickymortydn.core.model.Location
import com.example.rickymortydn.core.model.Page

interface LocationRepository {
    suspend fun getLocations(page: Int): Result<Page<Location>>
}
