@file:Suppress("LongMethod", "MagicNumber", "TooManyFunctions")

package es.davidnavarro.androidcleanarchitecture

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens

@Composable
internal fun CatalogNavigationBar(
    currentDestinationRoutes: Set<String>,
    onDestinationClick: (CatalogDestination) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                start = CatalogDimens.dp12,
                top = CatalogDimens.dp8,
                end = CatalogDimens.dp12,
                bottom = CatalogDimens.dp4
            )
    ) {
        val fontScale = LocalDensity.current.fontScale
        val stacked = fontScale >= LARGE_FONT_SCALE || maxWidth < COMPACT_NAVIGATION_WIDTH
        if (!stacked) {
            TechCatalogNavigation(
                currentDestinationRoutes = currentDestinationRoutes,
                onDestinationClick = onDestinationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        (maxWidth / NAVIGATION_REFERENCE_ASPECT_RATIO)
                            .coerceIn(CatalogDimens.dp54, CatalogDimens.dp82)
                    )
            )
            return@BoxWithConstraints
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (stacked) CatalogDimens.dp184 else CatalogDimens.dp68),
            shape = CutCornerShape(CatalogDimens.dp14),
            color = SpaceNavigationSurface,
            border = BorderStroke(
                width = CatalogDimens.dp1,
                color = NavigationCyan.copy(alpha = .72f)
            ),
            shadowElevation = CatalogDimens.dp12
        ) {
            Box {
                CatalogNavigationContent(
                    currentDestinationRoutes = currentDestinationRoutes,
                    onDestinationClick = onDestinationClick,
                    stacked = stacked
                )
                NavigationTechFrame(Modifier.matchParentSize())
            }
        }
    }
}

@Composable
private fun TechCatalogNavigation(
    currentDestinationRoutes: Set<String>,
    onDestinationClick: (CatalogDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDestination = CatalogDestination.entries.firstOrNull {
        it.route in currentDestinationRoutes
    } ?: CatalogDestination.Characters
    val navigationArtwork = when (selectedDestination) {
        CatalogDestination.Characters -> R.drawable.character_navigation_reference
        CatalogDestination.Locations -> R.drawable.location_navigation_reference
        CatalogDestination.Episodes -> R.drawable.episode_navigation_reference
    }
    Box(modifier) {
        Image(
            painter = painterResource(navigationArtwork),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
        ReferenceNavigationActions(
            selectedDestination = selectedDestination,
            onDestinationClick = onDestinationClick
        )
    }
}

@Composable
private fun ReferenceNavigationActions(
    selectedDestination: CatalogDestination,
    onDestinationClick: (CatalogDestination) -> Unit
) {
    Row(Modifier.fillMaxSize().selectableGroup()) {
        CatalogDestination.entries.forEach { destination ->
            val label = stringResource(destination.label)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .semantics {
                        selected = destination == selectedDestination
                        role = Role.Tab
                        contentDescription = label
                        text = AnnotatedString(label)
                    }
                    .clickable(onClick = { onDestinationClick(destination) })
            )
        }
    }
}

@Composable
private fun CatalogNavigationContent(
    currentDestinationRoutes: Set<String>,
    onDestinationClick: (CatalogDestination) -> Unit,
    stacked: Boolean
) {
    val premiumTheme = true
    if (stacked) {
        Column(
            modifier = Modifier
                .padding(CatalogDimens.dp7)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp4)
        ) {
            CatalogDestination.entries.forEach { destination ->
                CatalogDestinationItem(
                    destination = destination,
                    selected = destination.route in currentDestinationRoutes,
                    premiumTheme = premiumTheme,
                    onClick = { onDestinationClick(destination) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .padding(CatalogDimens.dp7)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(CatalogDimens.dp6),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CatalogDestination.entries.forEach { destination ->
                CatalogDestinationItem(
                    destination = destination,
                    selected = destination.route in currentDestinationRoutes,
                    premiumTheme = premiumTheme,
                    onClick = { onDestinationClick(destination) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CatalogDestinationItem(
    destination: CatalogDestination,
    selected: Boolean,
    premiumTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(destination.label)
    val premiumSelected = selected
    val containerColor = animateColorAsState(
        targetValue = if (premiumSelected) {
            PortalNavigationGreen.copy(alpha = .11f)
        } else if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else if (premiumTheme) {
            SpaceNavigationSurface
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }
    )
    val contentColor = animateColorAsState(
        targetValue = if (premiumSelected) {
            PortalNavigationGreen
        } else if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else if (premiumTheme) {
            SpaceNavigationMuted
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
    val elevation = animateDpAsState(
        targetValue = if (selected) CatalogDimens.dp3 else CatalogDimens.dp0
    )
    val itemShape = if (premiumSelected) {
        SelectedLocationShape
    } else {
        RoundedCornerShape(CatalogDimens.dp26)
    }
    Surface(
        onClick = onClick,
        modifier = modifier
            .then(
                if (premiumSelected) {
                    Modifier.shadow(
                        elevation = CatalogDimens.dp8,
                        shape = itemShape,
                        ambientColor = PortalNavigationGreen,
                        spotColor = PortalNavigationGreen
                    )
                } else {
                    Modifier
                }
            )
            .height(CatalogDimens.dp54)
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
        shape = itemShape,
        color = containerColor.value,
        contentColor = contentColor.value,
        border = if (premiumSelected) {
            BorderStroke(CatalogDimens.dp1, PortalNavigationGreen.copy(alpha = .78f))
        } else {
            null
        },
        shadowElevation = elevation.value
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = destination.icon, contentDescription = null)
            Text(
                text = label,
                modifier = Modifier.padding(start = CatalogDimens.dp8),
                style = MaterialTheme.typography.labelLarge,
                maxLines = NAVIGATION_LABEL_MAX_LINES
            )
        }
    }
}

private const val LARGE_FONT_SCALE = 1.3f
private const val NAVIGATION_LABEL_MAX_LINES = 1
private const val NAVIGATION_REFERENCE_ASPECT_RATIO = 6.4578f
private val COMPACT_NAVIGATION_WIDTH = CatalogDimens.dp330
private const val SPACE_NAVIGATION_SURFACE_ARGB = 0xF20B151E
private const val SPACE_NAVIGATION_MUTED_ARGB = 0xFFADB8BF
private const val PORTAL_NAVIGATION_GREEN_ARGB = 0xFFB8FF6A
private val SpaceNavigationSurface = Color(SPACE_NAVIGATION_SURFACE_ARGB)
private val SpaceNavigationMuted = Color(SPACE_NAVIGATION_MUTED_ARGB)
private val PortalNavigationGreen = Color(PORTAL_NAVIGATION_GREEN_ARGB)
private val SelectedLocationShape = GenericShape { size, _ ->
    moveTo(size.width * .16f, 0f)
    lineTo(size.width * .84f, 0f)
    lineTo(size.width, size.height * .2f)
    lineTo(size.width * .92f, size.height * .72f)
    lineTo(size.width * .72f, size.height)
    lineTo(size.width * .28f, size.height)
    lineTo(size.width * .08f, size.height * .72f)
    lineTo(0f, size.height * .2f)
    close()
}

@Composable
private fun NavigationTechFrame(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cut = CatalogDimens.dp14.toPx()
        val frame = Path().apply {
            moveTo(cut, 1f)
            lineTo(size.width - cut, 1f)
            lineTo(size.width - 1f, cut)
            lineTo(size.width - 1f, size.height - cut)
            lineTo(size.width - cut, size.height - 1f)
            lineTo(cut, size.height - 1f)
            lineTo(1f, size.height - cut)
            lineTo(1f, cut)
            close()
        }
        drawPath(frame, NavigationCyan.copy(alpha = .72f), style = Stroke(width = CatalogDimens.dp1.toPx()))
        drawLine(
            PortalNavigationGreen.copy(.7f),
            Offset(size.width * .4f, size.height - CatalogDimens.dp4.toPx()),
            Offset(size.width * .6f, size.height - CatalogDimens.dp4.toPx()),
            CatalogDimens.dp1.toPx()
        )
        repeat(5) { index ->
            val x = size.width * (.43f + index * .035f)
            drawCircle(PortalNavigationGreen.copy(alpha = .75f), CatalogDimens.dp1.toPx(), Offset(x, size.height - 2f))
        }
    }
}

private val NavigationCyan = Color(0xFF63DCF2)

internal enum class CatalogDestination(val route: String, @param:StringRes val label: Int, val icon: ImageVector) {
    Characters("characters", R.string.navigation_characters, Icons.Default.Person),
    Locations("locations", R.string.navigation_locations, Icons.Default.Place),
    Episodes("episodes", R.string.navigation_episodes, Icons.Default.PlayArrow)
}

internal const val CHARACTERS_LIST_ROUTE = "characters/list"
internal const val CHARACTER_ID_ARGUMENT = "characterId"
internal const val CHARACTER_DETAIL_ROUTE = "characters/detail/{$CHARACTER_ID_ARGUMENT}"
internal const val LOCATIONS_LIST_ROUTE = "locations/list"
internal const val LOCATION_ID_ARGUMENT = "locationId"
internal const val LOCATION_DETAIL_ROUTE = "locations/detail/{$LOCATION_ID_ARGUMENT}"
internal const val EPISODES_LIST_ROUTE = "episodes/list"
internal const val EPISODE_ID_ARGUMENT = "episodeId"
internal const val EPISODE_DETAIL_ROUTE = "episodes/detail/{$EPISODE_ID_ARGUMENT}"

internal fun characterDetailRoute(characterId: Int) = "characters/detail/$characterId"
internal fun locationDetailRoute(locationId: Int) = "locations/detail/$locationId"
internal fun episodeDetailRoute(episodeId: Int) = "episodes/detail/$episodeId"
