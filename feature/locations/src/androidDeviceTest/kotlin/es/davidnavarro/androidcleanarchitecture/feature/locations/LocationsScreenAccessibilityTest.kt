package es.davidnavarro.androidcleanarchitecture.feature.locations

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
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_planet
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_loading
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LocationsScreenAccessibilityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun everyBackendLocation_hasUniqueArtwork() {
        val artwork = (1..126).map { id ->
            locationArtwork(
                Location(
                    id = id,
                    name = "Location $id",
                    type = "Planet",
                    dimension = "Dimension $id",
                    residentCount = 0
                )
            )
        }

        assertEquals(126, artwork.toSet().size)
    }

    @Test
    fun everyBackendLocation_hasUniquePortraitDetailArtwork() {
        val locations = (1..126).map(::location)
        val detailArtwork = locations.map(::locationDetailArtwork)

        locations.forEach { location ->
            assertNotEquals(locationArtwork(location), locationDetailArtwork(location))
        }
        assertEquals(126, detailArtwork.toSet().size)
    }

    @Test
    fun locationCard_isOneGroupedSemanticsNode() {
        var clicked = false
        var planetLabel = ""
        composeRule.setContent {
            planetLabel = stringResource(Res.string.location_type_planet)
            MaterialTheme {
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
                        totalPages = 1,
                        totalLocations = 1
                    ),
                    onRetry = {},
                    onLoadMore = {},
                    onLocationClick = { clicked = true }
                )
            }
        }

        composeRule
            .onNode(hasText("Earth (C-137)") and hasText(planetLabel) and hasClickAction())
            .assertExists()
            .performClick()
        composeRule.runOnIdle { assertTrue(clicked) }
    }

    @Test
    fun loadingState_isAnnouncedPolitely() {
        var loadingLabel = ""
        composeRule.setContent {
            loadingLabel = stringResource(Res.string.locations_loading)
            MaterialTheme {
                LocationsScreen(
                    uiState = LocationsUiState.Loading,
                    onRetry = {},
                    onLoadMore = {},
                    onLocationClick = {}
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

    private fun location(id: Int) = Location(
        id = id,
        name = "Location $id",
        type = "Planet",
        dimension = "Dimension $id",
        residentCount = 0
    )
}
