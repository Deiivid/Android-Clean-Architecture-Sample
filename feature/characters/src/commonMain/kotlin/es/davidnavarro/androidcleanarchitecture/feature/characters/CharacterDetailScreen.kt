@file:Suppress("DEPRECATION")

package es.davidnavarro.androidcleanarchitecture.feature.characters

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_back
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_detail_title
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_image_description
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_location_label
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_origin_label
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_label
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_status_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CharacterDetailScreen(character: Character, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        CharacterSpaceBackdrop()
        Scaffold(containerColor = Color.Transparent) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + CatalogDimens.dp14,
                    bottom = CatalogDimens.dp32
                ),
                verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp16)
            ) {
                item(key = "hero") { CharacterDetailHero(character, onBack) }
                item(key = "information") {
                    CharacterInformationCard(
                        character = character,
                        modifier = Modifier.padding(horizontal = CatalogDimens.dp14)
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailHero(character: Character, onBack: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val statusAccent = characterStatusStyle(character.status).color
        val responsiveHeight = (maxWidth * HERO_HEIGHT_RATIO).coerceIn(CatalogDimens.dp380, CatalogDimens.dp520)
        val scannerSize = (maxWidth * SCANNER_WIDTH_RATIO).coerceAtMost(CatalogDimens.dp380)
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
                            listOf(Color.Transparent, Color.Transparent, CharacterBackground.copy(alpha = .62f))
                        )
                    )
            )
            CharacterPortraitScanner(
                character = character,
                accent = statusAccent,
                modifier = Modifier
                    .size(scannerSize)
                    .align(Alignment.TopEnd)
                    .offset(y = CatalogDimens.dp40)
            )
            CharacterDetailTopBar(
                onBack = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = CatalogDimens.dp16, vertical = CatalogDimens.dp8)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = CatalogDimens.dp24)
            ) {
                Text(
                    text = character.name,
                    modifier = Modifier.semantics { heading() },
                    color = CharacterText,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black
                )
                CharacterCircuitDivider(
                    Modifier
                        .fillMaxWidth(.62f)
                        .height(CatalogDimens.dp8),
                    accent = statusAccent
                )
            }
        }
    }
}

@Composable
private fun CharacterPortraitScanner(character: Character, accent: Color, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CharacterBiometricRings(
            modifier = Modifier.fillMaxSize(),
            accent = accent,
            status = character.status
        )
        AsyncImage(
            model = character.imageUrl,
            contentDescription = stringResource(Res.string.character_image_description, character.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize(.58f)
                .clip(CircleShape)
                .border(
                    BorderStroke(CatalogDimens.dp3, accent.copy(alpha = .82f)),
                    CircleShape
                )
        )
    }
}

@Composable
private fun CharacterDetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(CatalogDimens.dp44),
            shape = CircleShape,
            color = CharacterGreen.copy(alpha = .1f),
            border = BorderStroke(CatalogDimens.dp1, CharacterGreen.copy(alpha = .62f)),
            shadowElevation = CatalogDimens.dp3
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.character_back),
                    tint = CharacterGreen
                )
            }
        }
        Spacer(Modifier.width(CatalogDimens.dp16))
        Text(
            text = stringResource(Res.string.character_detail_title),
            color = CharacterText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun CharacterInformationCard(character: Character, modifier: Modifier = Modifier) {
    Box(modifier) {
        Card(
            shape = CutCornerShape(CatalogDimens.dp14),
            colors = CardDefaults.cardColors(containerColor = CharacterSurface),
            border = BorderStroke(CatalogDimens.dp1, CharacterCyan.copy(alpha = .52f)),
            elevation = CardDefaults.cardElevation(CatalogDimens.dp3)
        ) {
            Column(Modifier.padding(horizontal = CatalogDimens.dp16, vertical = CatalogDimens.dp14)) {
                CharacterDetailRow(
                    icon = Icons.Outlined.MonitorHeart,
                    label = stringResource(Res.string.character_status_label),
                    value = localizedStatus(character.status),
                    status = character.status
                )
                CharacterDetailRow(
                    icon = Icons.Outlined.Person,
                    label = stringResource(Res.string.character_species_label),
                    value = localizedSpecies(character.species)
                )
                CharacterDetailRow(
                    icon = Icons.Outlined.Public,
                    label = stringResource(Res.string.character_origin_label),
                    value = localizedBackendValue(character.origin)
                )
                CharacterDetailRow(
                    icon = Icons.Outlined.LocationOn,
                    label = stringResource(Res.string.character_location_label),
                    value = localizedBackendValue(character.location),
                    showDivider = false
                )
            }
        }
        CharacterTechFrame(Modifier.matchParentSize())
    }
}

@Composable
private fun CharacterDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    showDivider: Boolean = true,
    status: String? = null
) {
    val highlighted = status != null
    val accent = status?.let(::characterStatusStyle)?.color ?: CharacterGreen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (highlighted) {
                    Modifier
                        .background(accent.copy(alpha = .08f), CutCornerShape(CatalogDimens.dp8))
                        .border(
                            CatalogDimens.dp1,
                            accent.copy(alpha = .38f),
                            CutCornerShape(CatalogDimens.dp8)
                        )
                        .padding(horizontal = CatalogDimens.dp8)
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = CatalogDimens.dp10)
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(CatalogDimens.dp40),
                shape = CircleShape,
                color = CharacterSurfaceStrong,
                border = BorderStroke(CatalogDimens.dp1, accent.copy(alpha = .55f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (status != null) {
                        StatusGlyph(status = status, modifier = Modifier.size(CatalogDimens.dp20))
                    } else {
                        Icon(icon, null, Modifier.size(CatalogDimens.dp18), tint = accent)
                    }
                }
            }
            Spacer(Modifier.width(CatalogDimens.dp12))
            Column(Modifier.weight(1f)) {
                Text(label, color = CharacterTextMuted, style = MaterialTheme.typography.labelMedium)
                Text(value, color = CharacterText, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = CatalogDimens.dp34),
                color = CharacterCyan.copy(alpha = .14f)
            )
        }
    }
}

@Preview(name = "Detail", showBackground = true)
@Composable
private fun CharacterDetailPreview() {
    FeaturePreviewTheme {
        CharacterDetailScreen(character = previewCharacter(), onBack = {})
    }
}

private const val HERO_HEIGHT_RATIO = 1.08f
private const val SCANNER_WIDTH_RATIO = .78f
