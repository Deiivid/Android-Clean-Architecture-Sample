package com.example.rickymortydn.feature.episodes

import app.cash.turbine.test
import com.example.rickymortydn.core.domain.repository.EpisodeRepository
import com.example.rickymortydn.core.domain.usecase.GetEpisodesUseCase
import com.example.rickymortydn.core.model.Episode
import com.example.rickymortydn.core.model.Page
import com.example.rickymortydn.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EpisodesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads episodes on creation`() = runTest {
        val episodes = listOf(Episode(1, "Pilot", "December 2, 2013", "S01E01", 19))
        val page = Page(episodes, number = 1, totalPages = 3, totalItems = 51)
        val viewModel = EpisodesViewModel(GetEpisodesUseCase(FakeRepository(page)))

        viewModel.uiState.test {
            assertEquals(EpisodesUiState.Loading, awaitItem())
            assertEquals(
                EpisodesUiState.Content(episodes, page = 1, totalPages = 3, totalEpisodes = 51),
                awaitItem(),
            )
        }
    }

    private class FakeRepository(
        private val page: Page<Episode>,
    ) : EpisodeRepository {
        override suspend fun getEpisodes(page: Int): Result<Page<Episode>> = Result.success(this.page)
    }
}
