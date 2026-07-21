package es.davidnavarro.androidcleanarchitecture.feature.episodes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Episode

@Composable
fun EpisodeDetailScreen(episode: Episode, onBack: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    val airDate = remember(episode.airDate, locale) { formatAirDate(episode.airDate, locale) }
    Box(Modifier.fillMaxSize()) {
        EpisodeSpaceBackdrop()
        EpisodeDetailArtwork(episode.id, Modifier.fillMaxSize())
        Scaffold(containerColor = Color.Transparent) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + CatalogDimens.dp14,
                    bottom = CatalogDimens.dp32
                ),
                verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp0)
            ) {
                item { EpisodeDetailHero(episode, onBack) }
                item {
                    EpisodeInformationCard(
                        episode = episode,
                        airDate = airDate,
                        modifier = Modifier.padding(horizontal = CatalogDimens.dp14)
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeDetailHero(episode: Episode, onBack: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val responsiveHeight = (maxWidth * HERO_HEIGHT_RATIO).coerceIn(CatalogDimens.dp250, CatalogDimens.dp330)
        Box(
            Modifier
                .fillMaxWidth()
                .height(responsiveHeight)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                EpisodeBackground.copy(alpha = .1f),
                                Color.Transparent,
                                Color.Transparent,
                                EpisodeBackground.copy(alpha = .58f)
                            )
                        )
                    )
            )
            EpisodeDetailTopBar(
                onBack = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = CatalogDimens.dp16, vertical = CatalogDimens.dp8)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, EpisodeBackground.copy(alpha = .68f))
                        )
                    )
                    .padding(
                        start = CatalogDimens.dp24,
                        top = CatalogDimens.dp32,
                        end = CatalogDimens.dp24
                    )
            ) {
                Text(
                    text = episode.name,
                    modifier = Modifier.semantics { heading() },
                    color = EpisodeText,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black
                )
                EpisodeCircuitDivider(
                    Modifier
                        .fillMaxWidth(.62f)
                        .height(CatalogDimens.dp8)
                )
            }
        }
    }
}

@Composable
private fun EpisodeDetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(CatalogDimens.dp44),
            shape = CircleShape,
            color = TransmissionGreen.copy(alpha = .1f),
            border = BorderStroke(CatalogDimens.dp1, TransmissionGreen.copy(alpha = .62f)),
            shadowElevation = CatalogDimens.dp3
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.episode_back),
                    tint = TransmissionGreen
                )
            }
        }
        Spacer(Modifier.width(CatalogDimens.dp16))
        Text(
            text = stringResource(R.string.episode_detail_title),
            color = EpisodeText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun EpisodeInformationCard(episode: Episode, airDate: String, modifier: Modifier = Modifier) {
    val panelShape = CutCornerShape(
        topStart = CatalogDimens.dp18,
        topEnd = CatalogDimens.dp4,
        bottomEnd = CatalogDimens.dp18,
        bottomStart = CatalogDimens.dp4
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        EpisodeSurfaceStrong.copy(alpha = .86f),
                        EpisodeSurface.copy(alpha = .76f),
                        TransmissionCyan.copy(alpha = .06f)
                    )
                ),
                shape = panelShape
            )
            .border(
                border = BorderStroke(CatalogDimens.dp1, TransmissionCyan.copy(alpha = .48f)),
                shape = panelShape
            )
            .padding(CatalogDimens.dp16)
    ) {
        EpisodeInformationHeader(episode.code)
        EpisodeInformationDivider()
        EpisodeMetadata(episode, airDate)
    }
}

@Composable
private fun EpisodeInformationHeader(code: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(CatalogDimens.dp44),
            shape = CircleShape,
            color = TransmissionGreen.copy(alpha = .1f),
            border = BorderStroke(CatalogDimens.dp1, TransmissionGreen.copy(alpha = .52f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.PlayCircle, null, Modifier.size(CatalogDimens.dp22), TransmissionGreen)
            }
        }
        Spacer(Modifier.width(CatalogDimens.dp12))
        Text(
            stringResource(R.string.episode_transmission_label),
            Modifier.weight(1f),
            EpisodeText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Surface(
            shape = CutCornerShape(CatalogDimens.dp8),
            color = TransmissionGreen.copy(alpha = .1f),
            border = BorderStroke(CatalogDimens.dp1, TransmissionGreen.copy(alpha = .44f))
        ) {
            Text(
                code,
                Modifier.padding(horizontal = CatalogDimens.dp12, vertical = CatalogDimens.dp8),
                TransmissionGreen,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EpisodeInformationDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = CatalogDimens.dp14)
            .height(CatalogDimens.dp1)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        TransmissionCyan.copy(alpha = .48f),
                        TransmissionGreen.copy(alpha = .34f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun EpisodeMetadata(episode: Episode, airDate: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        EpisodeMetadataItem(
            Icons.Outlined.CalendarMonth,
            stringResource(R.string.episode_air_date_label),
            airDate,
            Modifier.weight(1f)
        )
        MetadataDivider()
        EpisodeMetadataItem(
            Icons.Outlined.Groups,
            stringResource(R.string.episode_characters_label),
            pluralStringResource(R.plurals.episode_characters, episode.characterCount, episode.characterCount),
            Modifier.weight(1f)
        )
        MetadataDivider()
        EpisodeMetadataItem(
            Icons.Outlined.Info,
            stringResource(R.string.episode_identifier_label),
            stringResource(R.string.episode_identifier_value, episode.id),
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun EpisodeMetadataItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = CatalogDimens.dp8)
            .semantics(mergeDescendants = true) {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, Modifier.size(CatalogDimens.dp18), tint = TransmissionGreen)
            Spacer(Modifier.width(CatalogDimens.dp6))
            Text(
                text = label,
                color = EpisodeTextMuted,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(CatalogDimens.dp8))
        Text(
            text = value,
            color = EpisodeText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MetadataDivider() {
    Box(
        Modifier
            .width(CatalogDimens.dp1)
            .height(CatalogDimens.dp56)
            .background(TransmissionCyan.copy(alpha = .18f))
    )
}

private const val HERO_HEIGHT_RATIO = .78f
