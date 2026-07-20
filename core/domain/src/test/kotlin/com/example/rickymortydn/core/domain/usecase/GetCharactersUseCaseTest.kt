package com.example.rickymortydn.core.domain.usecase

import com.example.rickymortydn.core.domain.repository.CharacterRepository
import com.example.rickymortydn.core.model.Character
import com.example.rickymortydn.core.model.Page
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCharactersUseCaseTest {
    @Test
    fun `delegates character loading to repository`() = runBlocking {
        val expected = Page(listOf(character()), number = 1, totalPages = 1, totalItems = 1)
        val useCase = GetCharactersUseCase(FakeCharacterRepository(Result.success(expected)))

        assertEquals(expected, useCase(page = 1).getOrThrow())
    }

    private class FakeCharacterRepository(
        private val result: Result<Page<Character>>,
    ) : CharacterRepository {
        override suspend fun getCharacters(page: Int): Result<Page<Character>> = result
    }

    private fun character() = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = "Earth",
        location = "Citadel of Ricks",
        imageUrl = "https://example.com/rick.png",
        episodeCount = 51,
    )
}
