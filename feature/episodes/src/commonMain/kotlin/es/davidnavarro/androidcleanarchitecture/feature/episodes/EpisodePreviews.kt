@file:Suppress("DEPRECATION")

package es.davidnavarro.androidcleanarchitecture.feature.episodes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview(name = "Content", showBackground = true)
@Composable
private fun EpisodesScreenPreview() {
    FeaturePreviewTheme {
        EpisodesScreen(
            uiState = EpisodesUiState.Content(
                episodes = listOf(
                    Episode(
                        id = 1,
                        name = "Pilot",
                        airDate = "December 2, 2013",
                        code = "S01E01",
                        characterCount = 19
                    )
                ),
                page = 1,
                totalPages = 3,
                totalEpisodes = 51
            ),
            onRetry = {},
            onLoadMore = {},
            onEpisodeClick = {}
        )
    }
}

@Preview(name = "Detail", showBackground = true)
@Composable
private fun EpisodeDetailPreview() {
    FeaturePreviewTheme {
        EpisodeDetailScreen(
            episode = Episode(
                id = 1,
                name = "Pilot",
                airDate = "December 2, 2013",
                code = "S01E01",
                characterCount = 19
            ),
            onBack = {}
        )
    }
}

@Composable
private fun FeaturePreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        content = content
    )
}
