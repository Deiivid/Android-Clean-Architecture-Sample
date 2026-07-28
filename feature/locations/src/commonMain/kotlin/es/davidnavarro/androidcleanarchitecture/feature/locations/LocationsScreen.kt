package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_dimension_label
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_header_pin
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_header_platform
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_residents
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_residents_label
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_empty
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_loaded
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_loading
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_subtitle
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_title
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationsRoute(viewModel: LocationsViewModel, onLocationClick: (Location) -> Unit, scrollToTopRequest: Int = 0) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LocationsScreen(uiState, viewModel::retry, viewModel::loadNextPage, onLocationClick, scrollToTopRequest)
}

@Composable
fun LocationsScreen(
    uiState: LocationsUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onLocationClick: (Location) -> Unit,
    scrollToTopRequest: Int = 0
) {
    Box(Modifier.fillMaxSize()) {
        LocationListBackdrop()
        Scaffold(containerColor = Color.Transparent) { padding ->
            when (uiState) {
                LocationsUiState.Loading -> LoadingContent(padding, stringResource(Res.string.locations_loading))
                LocationsUiState.Empty -> MessageContent(padding, stringResource(Res.string.locations_empty))
                is LocationsUiState.Error -> ErrorContent(padding, locationErrorMessage(uiState.error), onRetry)
                is LocationsUiState.Content -> {
                    LocationList(uiState, padding, onLoadMore, onLocationClick, scrollToTopRequest)
                }
            }
        }
    }
}

@Composable
private fun LocationList(
    state: LocationsUiState.Content,
    contentPadding: PaddingValues,
    onLoadMore: () -> Unit,
    onLocationClick: (Location) -> Unit,
    scrollToTopRequest: Int
) {
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    LaunchedEffect(listState, state.canLoadMore, state.isLoadingMore, state.loadMoreFailed) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            listState.layoutInfo.totalItemsCount > 0 && lastVisible >= listState.layoutInfo.totalItemsCount - 4
        }.distinctUntilChanged()
            .filter { it && state.canLoadMore && !state.isLoadingMore && !state.loadMoreFailed }
            .collect { onLoadMore() }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = CatalogDimens.dp16,
            top = contentPadding.calculateTopPadding() + CatalogDimens.dp18,
            end = CatalogDimens.dp16,
            bottom = CatalogDimens.dp24
        ),
        verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp8)
    ) {
        item(key = "header") {
            CatalogHeader(
                title = stringResource(Res.string.locations_title),
                subtitle = stringResource(Res.string.locations_subtitle),
                count = pluralStringResource(
                    Res.plurals.locations_loaded,
                    state.locations.size,
                    state.locations.size,
                    state.totalLocations
                )
            )
        }
        items(state.locations, key = Location::id) { location ->
            LocationCard(location) { onLocationClick(location) }
        }
        if (state.isLoadingMore || state.loadMoreFailed) {
            item(key = "pagination") { PaginationContent(state, onLoadMore) }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun CatalogHeader(title: String, subtitle: String, count: String) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val headerHeight = (maxWidth * HEADER_HEIGHT_RATIO).coerceIn(CatalogDimens.dp124, CatalogDimens.dp156)
        val markerTravel = with(LocalDensity.current) { CatalogDimens.dp6.toPx() }
        val markerTransition = rememberInfiniteTransition(label = "location marker float")
        val markerOffset by markerTransition.animateFloat(
            initialValue = -markerTravel,
            targetValue = markerTravel,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1_800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "location marker vertical offset"
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(headerHeight)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to SpaceBackground.copy(alpha = .18f),
                            .14f to Color.Transparent,
                            .86f to Color.Transparent,
                            1f to SpaceBackground.copy(alpha = .08f)
                        )
                    )
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0f to SpaceBackground.copy(alpha = .38f),
                                .42f to SpaceBackground.copy(alpha = .22f),
                                .68f to Color.Transparent,
                                1f to Color.Transparent
                            )
                        )
                    )
            )
            Image(
                painter = painterResource(Res.drawable.location_header_platform),
                contentDescription = null,
                modifier = Modifier
                    .size(headerHeight * .92f)
                    .align(Alignment.CenterEnd)
            )
            Image(
                painter = painterResource(Res.drawable.location_header_pin),
                contentDescription = null,
                modifier = Modifier
                    .size(headerHeight * .92f)
                    .align(Alignment.CenterEnd)
                    .graphicsLayer { translationY = markerOffset }
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = CatalogDimens.dp8),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(start = CatalogDimens.dp20)
                    ) {
                        Text(
                            title,
                            Modifier.semantics { heading() },
                            color = SpaceText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black
                        )
                        Text(subtitle, color = SpaceTextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = PortalGreen.copy(alpha = .12f),
                    border = BorderStroke(CatalogDimens.dp1, PortalGreen.copy(alpha = .48f))
                ) {
                    Row(
                        Modifier.padding(horizontal = CatalogDimens.dp12, vertical = CatalogDimens.dp8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PortalMarker(CatalogDimens.dp24)
                        Spacer(Modifier.width(CatalogDimens.dp8))
                        Text(
                            count,
                            color = PortalGreen,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun LocationCard(location: Location, onClick: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
        val compact = maxWidth < COMPACT_CARD_WIDTH
        val cardHeight = if (stacked) {
            CatalogDimens.dp250
        } else {
            (maxWidth / CARD_REFERENCE_ASPECT_RATIO).coerceIn(CatalogDimens.dp124, CatalogDimens.dp156)
        }
        val cardShape = CutCornerShape(CatalogDimens.dp6)
        ElevatedCard(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .semantics(mergeDescendants = true) { role = Role.Button },
            shape = cardShape,
            colors = CardDefaults.elevatedCardColors(containerColor = SpaceSurface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = CatalogDimens.dp3)
        ) {
            Box(Modifier.fillMaxSize()) {
                LocationArtwork(location, Modifier.fillMaxSize())
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0f to SpaceSurfaceStrong,
                                    .46f to SpaceSurfaceStrong.copy(alpha = .82f),
                                    .76f to Color.Transparent,
                                    1f to Color.Transparent
                                )
                            )
                        )
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(if (compact) CatalogDimens.dp10 else CatalogDimens.dp14),
                    verticalArrangement = Arrangement.spacedBy(
                        if (compact) CatalogDimens.dp4 else CatalogDimens.dp8
                    )
                ) {
                    LocationCardHeader(location, stacked, compact)
                    MetadataRow(
                        Icons.Outlined.Explore,
                        stringResource(Res.string.location_dimension_label),
                        localizedDimensionValue(location.dimension),
                        compact
                    )
                    MetadataRow(
                        Icons.Outlined.Groups,
                        stringResource(Res.string.location_residents_label),
                        pluralStringResource(
                            Res.plurals.location_residents,
                            location.residentCount,
                            location.residentCount
                        ),
                        compact
                    )
                }
                ReferenceTechFrame(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun LocationCardHeader(location: Location, stacked: Boolean, compact: Boolean) {
    if (stacked) {
        Column(verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp8)) {
            LocationName(location.name, compact = compact)
            LocationTypeBadge(location.type, compact)
        }
    } else {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            LocationName(location.name, Modifier.weight(1f), compact)
            Spacer(Modifier.width(if (compact) CatalogDimens.dp6 else CatalogDimens.dp10))
            LocationTypeBadge(location.type, compact)
        }
    }
}

@Composable
private fun LocationName(name: String, modifier: Modifier = Modifier, compact: Boolean = false) {
    Text(
        name,
        modifier,
        color = SpaceText,
        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        maxLines = 1
    )
}

@Composable
private fun LocationTypeBadge(type: String, compact: Boolean) {
    Surface(
        color = PortalGreen.copy(alpha = .14f),
        shape = RoundedCornerShape(CatalogDimens.dp10),
        border = BorderStroke(CatalogDimens.dp1, PortalGreen.copy(alpha = .28f))
    ) {
        Text(
            localizedLocationType(type),
            Modifier.padding(
                horizontal = if (compact) CatalogDimens.dp8 else CatalogDimens.dp10,
                vertical = if (compact) CatalogDimens.dp4 else CatalogDimens.dp6
            ),
            color = PortalGreen,
            style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}

@Composable
private fun MetadataRow(icon: ImageVector, label: String, value: String, compact: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            Modifier.size(if (compact) CatalogDimens.dp34 else CatalogDimens.dp42),
            CircleShape,
            PortalGreen.copy(alpha = .10f),
            border = BorderStroke(CatalogDimens.dp1, PortalGreen.copy(alpha = .42f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    null,
                    Modifier.size(if (compact) CatalogDimens.dp18 else CatalogDimens.dp21),
                    tint = PortalGreen
                )
            }
        }
        Spacer(Modifier.width(if (compact) CatalogDimens.dp8 else CatalogDimens.dp12))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                color = SpaceTextMuted,
                style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
            )
            Text(
                value,
                color = SpaceText,
                style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }
    }
}

private const val LARGE_FONT_SCALE = 1.3f
private const val HEADER_HEIGHT_RATIO = .39f
private const val CARD_REFERENCE_ASPECT_RATIO = 2.32f
private val COMPACT_CARD_WIDTH = CatalogDimens.dp330
