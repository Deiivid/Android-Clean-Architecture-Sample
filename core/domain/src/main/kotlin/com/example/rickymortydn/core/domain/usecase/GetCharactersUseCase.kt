package com.example.rickymortydn.core.domain.usecase

import com.example.rickymortydn.core.domain.repository.CharacterRepository
import com.example.rickymortydn.core.model.Character
import com.example.rickymortydn.core.model.Page

class GetCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(page: Int = 1): Result<Page<Character>> =
        repository.getCharacters(page)
}
