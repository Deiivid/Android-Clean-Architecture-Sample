package com.example.rickymortydn.feature.characters

import app.cash.turbine.test
import com.example.rickymortydn.core.domain.repository.CharacterRepository
import com.example.rickymortydn.core.domain.usecase.GetCharactersUseCase
import com.example.rickymortydn.core.model.Character
import com.example.rickymortydn.core.model.Page
import com.example.rickymortydn.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CharactersViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads content on creation`() = runTest {
        val characters = listOf(character())
        val expected = Page(characters, number = 1, totalPages = 42, totalItems = 826)
        val viewModel = CharactersViewModel(
            GetCharactersUseCase(FakeRepository(Result.success(expected))),
        )

        viewModel.uiState.test {
            assertEquals(CharactersUiState.Loading, awaitItem())
            assertEquals(
                CharactersUiState.Content(characters, page = 1, totalPages = 42, totalCharacters = 826),
                awaitItem(),
            )
        }
    }

    @Test
    fun `appends the next page`() = runTest {
        val first = character()
        val second = first.copy(id = 2, name = "Morty Smith")
        val pages = mapOf(
            1 to Page(listOf(first), number = 1, totalPages = 2, totalItems = 2),
            2 to Page(listOf(second), number = 2, totalPages = 2, totalItems = 2),
        )
        val viewModel = CharactersViewModel(GetCharactersUseCase(PagingRepository(pages)))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.loadNextPage()
            assertEquals(true, (awaitItem() as CharactersUiState.Content).isLoadingMore)
            assertEquals(listOf(first, second), (awaitItem() as CharactersUiState.Content).characters)
        }
    }

    private class FakeRepository(
        private val result: Result<Page<Character>>,
    ) : CharacterRepository {
        override suspend fun getCharacters(page: Int): Result<Page<Character>> = result
    }

    private class PagingRepository(
        private val pages: Map<Int, Page<Character>>,
    ) : CharacterRepository {
        override suspend fun getCharacters(page: Int): Result<Page<Character>> =
            pages[page]?.let { Result.success(it) }
                ?: Result.failure(IllegalArgumentException("Unknown page"))
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
