package com.example.rickymortydn.core.data.repository

import com.example.rickymortydn.core.data.mapper.toDomain
import com.example.rickymortydn.core.data.mapper.toDomainPage
import com.example.rickymortydn.core.data.network.RickAndMortyApi
import com.example.rickymortydn.core.domain.repository.CharacterRepository
import com.example.rickymortydn.core.model.Character
import com.example.rickymortydn.core.model.Page
import javax.inject.Inject

internal class NetworkCharacterRepository @Inject constructor(
    private val api: RickAndMortyApi,
) : CharacterRepository {
    override suspend fun getCharacters(page: Int): Result<Page<Character>> = runCatching {
        api.getCharacters(page).toDomainPage(page) { it.toDomain() }
    }
}
