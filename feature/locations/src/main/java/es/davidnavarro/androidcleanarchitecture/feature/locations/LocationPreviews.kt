package es.davidnavarro.androidcleanarchitecture.feature.locations

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import es.davidnavarro.androidcleanarchitecture.core.model.Location

@Preview(name = "Content", showBackground = true)
@Preview(
    name = "Dark · large text",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.5f
)
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
@Preview(
    name = "Detail · dark · large text",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.5f
)
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
