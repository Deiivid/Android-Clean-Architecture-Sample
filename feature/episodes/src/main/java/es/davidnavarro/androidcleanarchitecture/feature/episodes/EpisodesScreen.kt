@file:Suppress("TooManyFunctions")

package es.davidnavarro.androidcleanarchitecture.feature.episodes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun EpisodesRoute(
    onEpisodeClick: (Episode) -> Unit,
    scrollToTopRequest: Int = 0,
    viewModel: EpisodesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EpisodesScreen(uiState, viewModel::retry, viewModel::loadNextPage, onEpisodeClick, scrollToTopRequest)
}

@Composable
fun EpisodesScreen(
    uiState: EpisodesUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    scrollToTopRequest: Int = 0
) {
    Box(Modifier.fillMaxSize()) {
        EpisodeListBackdrop()
        Scaffold(containerColor = Color.Transparent) { padding ->
            when (uiState) {
                EpisodesUiState.Loading -> LoadingContent(padding, stringResource(R.string.episodes_loading))
                EpisodesUiState.Empty -> MessageContent(padding, stringResource(R.string.episodes_empty))
                is EpisodesUiState.Error -> ErrorContent(padding, episodeErrorMessage(uiState.error), onRetry)
                is EpisodesUiState.Content -> {
                    EpisodeList(uiState, padding, onLoadMore, onEpisodeClick, scrollToTopRequest)
                }
            }
        }
    }
}

@Composable
private fun EpisodeList(
    state: EpisodesUiState.Content,
    contentPadding: PaddingValues,
    onLoadMore: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    scrollToTopRequest: Int
) {
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    LaunchedEffect(listState, state.canLoadMore, state.isLoadingMore, state.loadMoreFailed) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            listState.layoutInfo.totalItemsCount > 0 &&
                lastVisible >= listState.layoutInfo.totalItemsCount - 4
        }
            .distinctUntilChanged()
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
                title = stringResource(R.string.episodes_title),
                subtitle = stringResource(R.string.episodes_subtitle),
                count = pluralStringResource(
                    R.plurals.episodes_loaded,
                    state.episodes.size,
                    state.episodes.size,
                    state.totalEpisodes
                )
            )
        }
        itemsIndexed(state.episodes, key = { _, episode -> episode.id }) { index, episode ->
            EpisodeTimelineItem(
                episode = episode,
                isFirst = index == 0,
                isLast = index == state.episodes.lastIndex,
                onClick = { onEpisodeClick(episode) }
            )
        }
        if (state.isLoadingMore || state.loadMoreFailed) {
            item(key = "pagination") { PaginationContent(state, onLoadMore) }
        }
    }
}

@Composable
private fun CatalogHeader(title: String, subtitle: String, count: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(CatalogDimens.dp140)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            EpisodeBackground.copy(alpha = .72f),
                            EpisodeBackground.copy(alpha = .34f),
                            Color.Transparent
                        )
                    )
                )
        )
        AnimatedEpisodeDial(
            size = CatalogDimens.dp140,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = CatalogDimens.dp8),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CatalogHeaderTitle(title, subtitle)
            CatalogHeaderCount(count)
        }
    }
}

@Composable
private fun CatalogHeaderTitle(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth(.64f).padding(start = CatalogDimens.dp12)) {
        Text(
            title,
            Modifier.semantics { heading() },
            EpisodeText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )
        Text(subtitle, color = EpisodeTextMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CatalogHeaderCount(count: String) {
    Surface(
        shape = CircleShape,
        color = TransmissionGreen.copy(alpha = .12f),
        border = BorderStroke(CatalogDimens.dp1, TransmissionGreen.copy(alpha = .48f))
    ) {
        Row(
            Modifier.padding(horizontal = CatalogDimens.dp12, vertical = CatalogDimens.dp8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransmissionMarker(CatalogDimens.dp24)
            Spacer(Modifier.width(CatalogDimens.dp8))
            Text(
                count,
                color = TransmissionGreen,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EpisodeTimelineItem(episode: Episode, isFirst: Boolean, isLast: Boolean, onClick: () -> Unit) {
    val largeText = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    val cardHeight = if (largeText) CatalogDimens.dp250 else CatalogDimens.dp132
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EpisodeTimelineRail(
            isFirst = isFirst,
            isLast = isLast,
            modifier = Modifier
                .width(CatalogDimens.dp24)
                .fillMaxHeight()
        )
        Spacer(Modifier.width(CatalogDimens.dp4))
        EpisodeCard(
            episode = episode,
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun EpisodeCard(episode: Episode, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val locale = LocalConfiguration.current.locales[0]
    val airDate = remember(episode.airDate, locale) { formatAirDate(episode.airDate, locale) }
    val useVerticalHeader = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    val accent = if (episode.id % 3 == 0) TransmissionCyan else TransmissionGreen
    Card(
        onClick = onClick,
        modifier = modifier
            .semantics(mergeDescendants = true) { role = Role.Button },
        shape = EpisodeTransmissionCardShape,
        colors = CardDefaults.cardColors(containerColor = EpisodeSurface),
        elevation = CardDefaults.cardElevation(CatalogDimens.dp3)
    ) {
        Box(Modifier.fillMaxSize()) {
            EpisodeArtwork(episode.id, Modifier.fillMaxSize())
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0f to EpisodeSurfaceStrong,
                                .48f to EpisodeSurfaceStrong.copy(alpha = .94f),
                                .72f to EpisodeSurfaceStrong.copy(alpha = .34f),
                                1f to Color.Transparent
                            )
                        )
                    )
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(if (useVerticalHeader) CatalogDimens.dp14 else CatalogDimens.dp10),
                verticalArrangement = Arrangement.spacedBy(
                    if (useVerticalHeader) CatalogDimens.dp8 else CatalogDimens.dp4
                )
            ) {
                EpisodeCardHeader(episode = episode, stacked = useVerticalHeader, compact = !useVerticalHeader)
                MetadataRow(
                    Icons.Outlined.CalendarMonth,
                    stringResource(R.string.episode_air_date_label),
                    airDate,
                    compact = !useVerticalHeader
                )
                MetadataRow(
                    Icons.Outlined.Groups,
                    stringResource(R.string.episode_characters_label),
                    pluralStringResource(
                        R.plurals.episode_characters,
                        episode.characterCount,
                        episode.characterCount
                    ),
                    compact = !useVerticalHeader
                )
            }
            EpisodeTechFrame(Modifier.fillMaxSize(), accent)
        }
    }
}

@Composable
private fun EpisodeCardHeader(episode: Episode, stacked: Boolean, compact: Boolean) {
    if (stacked) {
        Column(verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp8)) {
            EpisodeName(episode.name, compact = compact)
            EpisodeCode(
                code = episode.code,
                modifier = Modifier.padding(end = CatalogDimens.dp8, top = CatalogDimens.dp4)
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EpisodeName(name = episode.name, compact = compact, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(CatalogDimens.dp10))
            EpisodeCode(
                code = episode.code,
                modifier = Modifier.padding(end = CatalogDimens.dp8)
            )
        }
    }
}

@Composable
private fun EpisodeName(name: String, compact: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        color = EpisodeText,
        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun EpisodeCode(code: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = EpisodeSurfaceStrong.copy(alpha = .96f),
        shape = EpisodeCodeShape,
        border = BorderStroke(CatalogDimens.dp1, TransmissionGreen.copy(alpha = .72f)),
        shadowElevation = CatalogDimens.dp3
    ) {
        Text(
            text = code,
            modifier = Modifier.padding(horizontal = CatalogDimens.dp10, vertical = CatalogDimens.dp6),
            style = MaterialTheme.typography.labelMedium,
            color = TransmissionGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MetadataRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    compact: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(if (compact) CatalogDimens.dp28 else CatalogDimens.dp34),
            shape = CircleShape,
            color = TransmissionGreen.copy(alpha = .1f),
            border = BorderStroke(CatalogDimens.dp1, TransmissionGreen.copy(alpha = .42f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(if (compact) CatalogDimens.dp16 else CatalogDimens.dp18),
                    tint = TransmissionGreen
                )
            }
        }
        Spacer(Modifier.width(if (compact) CatalogDimens.dp8 else CatalogDimens.dp12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = EpisodeTextMuted
            )
            Text(
                value,
                color = EpisodeText,
                style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge
            )
        }
    }
}

internal fun formatAirDate(value: String, locale: Locale): String = runCatching {
    LocalDate.parse(value, ApiAirDateFormatter).format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
    )
}.getOrDefault(value)

private val ApiAirDateFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu", Locale.US)

private const val LARGE_FONT_SCALE = 1.3f
