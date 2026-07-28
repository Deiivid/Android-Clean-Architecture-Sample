package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_back
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_detail_information
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_detail_title
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_dimension_label
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_identifier_label
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_identifier_value
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_residents
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_residents_label
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_label
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationDetailScreen(location: Location, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SpaceBackdrop()
        LocationDetailArtwork(
            location = location,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = -CatalogDimens.dp28)
        )
        Scaffold(containerColor = Color.Transparent) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = CatalogDimens.dp0,
                    top = padding.calculateTopPadding() + CatalogDimens.dp14,
                    end = CatalogDimens.dp0,
                    bottom = CatalogDimens.dp32
                ),
                verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp8)
            ) {
                item { LocationDetailHero(location, onBack) }
                item {
                    LocationInformationCard(
                        location,
                        Modifier.padding(horizontal = CatalogDimens.dp14)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CatalogDimens.dp56)
    ) {
        Surface(
            modifier = Modifier
                .width(CatalogDimens.dp132)
                .height(CatalogDimens.dp50)
                .align(Alignment.Center),
            shape = RoundedCornerShape(CatalogDimens.dp18),
            color = SpaceSurfaceStrong.copy(alpha = .88f),
            border = BorderStroke(CatalogDimens.dp1, PortalCyan.copy(alpha = .72f)),
            shadowElevation = CatalogDimens.dp8
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    stringResource(Res.string.location_detail_title),
                    color = SpaceText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Surface(
            modifier = Modifier
                .size(CatalogDimens.dp44)
                .align(Alignment.CenterStart),
            shape = CircleShape,
            color = PortalGreen.copy(alpha = .10f),
            border = BorderStroke(CatalogDimens.dp1, PortalGreen.copy(alpha = .62f)),
            shadowElevation = CatalogDimens.dp3
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(Res.string.location_back),
                    tint = PortalGreen
                )
            }
        }
    }
}

@Composable
private fun LocationDetailHero(location: Location, onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(CatalogDimens.dp156)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Transparent, SpaceBackground.copy(.54f))
                    )
                )
        )
        DetailTopBar(
            onBack,
            Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = CatalogDimens.dp16, vertical = CatalogDimens.dp8)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = CatalogDimens.dp24,
                    top = CatalogDimens.dp82,
                    end = CatalogDimens.dp24
                )
        ) {
            Text(
                text = location.name,
                modifier = Modifier.semantics { heading() },
                color = SpaceText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black
            )
            HeroCircuitDivider(
                Modifier
                    .fillMaxWidth(.62f)
                    .height(CatalogDimens.dp8)
            )
        }
    }
}

@Composable
private fun LocationInformationCard(location: Location, modifier: Modifier = Modifier) {
    Box(modifier) {
        Card(
            shape = RoundedCornerShape(CatalogDimens.dp24),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(CatalogDimens.dp1, PortalCyan.copy(alpha = .68f)),
            elevation = CardDefaults.cardElevation(CatalogDimens.dp8)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                SpaceSurfaceStrong.copy(alpha = .96f),
                                SpaceSurface.copy(alpha = .9f)
                            )
                        )
                    )
                    .padding(CatalogDimens.dp12),
                verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp6)
            ) {
                InformationHeader()
                androidx.compose.material3.HorizontalDivider(color = PortalCyan.copy(alpha = .24f))
                LocationMetadataGrid(location)
            }
        }
        DetailTechFrame(Modifier.matchParentSize())
    }
}

@Composable
private fun LocationMetadataGrid(location: Location) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(CatalogDimens.dp6)) {
        DetailTile(
            Icons.Outlined.Place,
            stringResource(Res.string.location_type_label),
            localizedLocationType(location.type),
            Modifier.weight(1f)
        )
        DetailTile(
            Icons.Outlined.Explore,
            stringResource(Res.string.location_dimension_label),
            localizedDimensionValue(location.dimension),
            Modifier.weight(1f)
        )
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(CatalogDimens.dp6)) {
        DetailTile(
            Icons.Outlined.Groups,
            stringResource(Res.string.location_residents_label),
            pluralStringResource(Res.plurals.location_residents, location.residentCount, location.residentCount),
            Modifier.weight(1f)
        )
        DetailTile(
            Icons.Outlined.Info,
            stringResource(Res.string.location_identifier_label),
            stringResource(Res.string.location_identifier_value, location.id),
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun InformationHeader() {
    Row(
        modifier = Modifier.padding(horizontal = CatalogDimens.dp6, vertical = CatalogDimens.dp4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(CatalogDimens.dp32),
            shape = CircleShape,
            color = PortalGreen.copy(alpha = .14f),
            border = BorderStroke(CatalogDimens.dp1, PortalGreen.copy(alpha = .5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Info, null, Modifier.size(CatalogDimens.dp16), tint = PortalGreen)
            }
        }
        Spacer(Modifier.width(CatalogDimens.dp10))
        Text(
            stringResource(Res.string.location_detail_information),
            color = SpaceText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailTile(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                SpaceBackground.copy(alpha = .34f),
                RoundedCornerShape(CatalogDimens.dp14)
            )
            .padding(horizontal = CatalogDimens.dp10, vertical = CatalogDimens.dp12)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(CatalogDimens.dp40),
            shape = CircleShape,
            color = SpaceSurfaceStrong,
            border = BorderStroke(CatalogDimens.dp1, PortalGreen.copy(alpha = .45f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size(CatalogDimens.dp18), tint = PortalGreen)
            }
        }
        Spacer(Modifier.width(CatalogDimens.dp10))
        Column(Modifier.weight(1f)) {
            Text(label, color = SpaceTextMuted, style = MaterialTheme.typography.labelMedium)
            Text(value, color = SpaceText, style = MaterialTheme.typography.bodyLarge, maxLines = 2)
        }
    }
}
