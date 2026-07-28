package es.davidnavarro.androidcleanarchitecture

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CatalogNavigationBarTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectedDestination_comesFromTheActiveRoute() {
        var clickedDestination: CatalogDestination? = null
        var locationsLabel = ""
        var episodesLabel = ""
        composeRule.setContent {
            locationsLabel = stringResource(CatalogDestination.Locations.label)
            episodesLabel = stringResource(CatalogDestination.Episodes.label)
            MaterialTheme {
                CatalogNavigationBar(
                    currentDestinationRoutes = setOf(CatalogDestination.Locations.route),
                    onDestinationClick = { clickedDestination = it }
                )
            }
        }

        composeRule.onNodeWithText(locationsLabel).assertIsSelected()
        composeRule.onNodeWithText(episodesLabel).performClick()
        composeRule.runOnIdle {
            assertEquals(CatalogDestination.Episodes, clickedDestination)
        }
    }

    @Test
    fun largeText_keepsEveryDestinationAccessibleWithoutClippingLabels() {
        var labels = emptyList<String>()
        composeRule.setContent {
            val currentDensity = LocalDensity.current
            labels = CatalogDestination.entries.map { stringResource(it.label) }
            CompositionLocalProvider(
                LocalDensity provides Density(density = currentDensity.density, fontScale = 2f)
            ) {
                MaterialTheme {
                    CatalogNavigationBar(
                        currentDestinationRoutes = setOf(CatalogDestination.Episodes.route),
                        onDestinationClick = {}
                    )
                }
            }
        }

        labels.forEach { label -> composeRule.onNodeWithText(label).assertIsDisplayed() }
        composeRule.onNodeWithText(labels.last()).assertIsSelected()
    }

    @Test
    fun compactWidth_stacksNavigationAndKeepsLabelsVisible() {
        var labels = emptyList<String>()
        composeRule.setContent {
            labels = CatalogDestination.entries.map { stringResource(it.label) }
            MaterialTheme {
                Box(modifier = Modifier.width(320.dp)) {
                    CatalogNavigationBar(
                        currentDestinationRoutes = setOf(CatalogDestination.Characters.route),
                        onDestinationClick = {}
                    )
                }
            }
        }

        labels.forEach { label -> composeRule.onNodeWithText(label).assertIsDisplayed() }
        composeRule.onNodeWithText(labels.first()).assertIsSelected()
    }
}
