package es.davidnavarro.androidcleanarchitecture.feature.episodes

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episodes_loading
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EpisodesScreenAccessibilityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun episodeCard_isOneGroupedSemanticsNode() {
        var clicked = false
        composeRule.setContent {
            MaterialTheme {
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
                        totalPages = 1,
                        totalEpisodes = 1
                    ),
                    onRetry = {},
                    onLoadMore = {},
                    onEpisodeClick = { clicked = true }
                )
            }
        }

        composeRule
            .onNode(hasText("Pilot") and hasText("S01E01") and hasClickAction())
            .assertExists()
            .performClick()
        composeRule.runOnIdle { assertTrue(clicked) }
    }

    @Test
    fun loadingState_isAnnouncedPolitely() {
        var loadingLabel = ""
        composeRule.setContent {
            loadingLabel = stringResource(Res.string.episodes_loading)
            MaterialTheme {
                EpisodesScreen(
                    uiState = EpisodesUiState.Loading,
                    onRetry = {},
                    onLoadMore = {},
                    onEpisodeClick = {}
                )
            }
        }

        composeRule
            .onNode(hasStateDescription(loadingLabel))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.LiveRegion,
                    LiveRegionMode.Polite
                )
            )
    }
}
