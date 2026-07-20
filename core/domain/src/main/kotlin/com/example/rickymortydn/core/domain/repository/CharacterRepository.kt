package com.example.rickymortydn.core.domain.repository

import com.example.rickymortydn.core.model.Character
import com.example.rickymortydn.core.model.Page

interface CharacterRepository {
    suspend fun getCharacters(page: Int): Result<Page<Character>>
}
