package es.davidnavarro.androidcleanarchitecture.feature.characters

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
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.characters_loading
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CharactersScreenAccessibilityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun characterCard_isOneActionableSemanticsNode() {
        var clicked = false
        composeRule.setContent {
            MaterialTheme {
                CharactersScreen(
                    uiState = CharactersUiState.Content(
                        characters = listOf(previewCharacter()),
                        page = 1,
                        totalPages = 1,
                        totalCharacters = 1
                    ),
                    onRetry = {},
                    onLoadMore = {},
                    onCharacterClick = { clicked = true }
                )
            }
        }

        composeRule
            .onNode(hasText("Rick Sanchez") and hasClickAction())
            .assertExists()
            .performClick()
        composeRule.runOnIdle { assertTrue(clicked) }
    }

    @Test
    fun loadingState_isAnnouncedPolitely() {
        var loadingLabel = ""
        composeRule.setContent {
            loadingLabel = stringResource(Res.string.characters_loading)
            MaterialTheme {
                CharactersScreen(
                    uiState = CharactersUiState.Loading,
                    onRetry = {},
                    onLoadMore = {},
                    onCharacterClick = {}
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
