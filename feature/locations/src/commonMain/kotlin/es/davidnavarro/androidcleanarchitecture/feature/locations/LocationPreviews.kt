@file:Suppress("DEPRECATION")

package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview(name = "Content", showBackground = true)
@Composable
private fun LocationsScreenPreview() {
    FeaturePreviewTheme {
        LocationsScreen(
            uiState = LocationsUiState.Content(
                locations = listOf(
                    Location(
                        id = 1,
                        name = "Earth (C-137)",
                        type = "Planet",
                        dimension = "Dimension C-137",
                        residentCount = 27
                    )
                ),
                page = 1,
                totalPages = 7,
                totalLocations = 126
            ),
            onRetry = {},
            onLoadMore = {},
            onLocationClick = {}
        )
    }
}

@Preview(name = "Detail", showBackground = true)
@Composable
private fun LocationDetailPreview() {
    FeaturePreviewTheme {
        LocationDetailScreen(
            location = Location(
                id = 1,
                name = "Earth (C-137)",
                type = "Planet",
                dimension = "Dimension C-137",
                residentCount = 27
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
