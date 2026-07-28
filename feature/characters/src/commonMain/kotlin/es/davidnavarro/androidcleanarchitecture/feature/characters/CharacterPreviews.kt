@file:Suppress("DEPRECATION")

package es.davidnavarro.androidcleanarchitecture.feature.characters

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview(name = "Content", showBackground = true)
@Composable
private fun CharactersScreenPreview() {
    FeaturePreviewTheme {
        CharactersScreen(
            uiState = CharactersUiState.Content(
                characters = listOf(previewCharacter()),
                page = 1,
                totalPages = 42,
                totalCharacters = 826
            ),
            onRetry = {},
            onLoadMore = {},
            onCharacterClick = {}
        )
    }
}

@Composable
internal fun FeaturePreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        content = content
    )
}

internal fun previewCharacter() = Character(
    id = 1,
    name = "Rick Sanchez",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = "Earth (C-137)",
    location = "Citadel of Ricks",
    imageUrl = "",
    episodeCount = 51
)
