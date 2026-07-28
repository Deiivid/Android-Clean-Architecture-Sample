@file:Suppress("MagicNumber")

package es.davidnavarro.androidcleanarchitecture.feature.characters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_episodes_label
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_episodes_short
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_location_label
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_origin_label
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CharacterCard(character: Character, onClick: () -> Unit) {
    val useVerticalLayout = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    val statusStyle = characterStatusStyle(character.status)
    val accent = when (character.status.lowercase()) {
        "dead", "unknown" -> statusStyle.color
        else -> if (character.id % 3 == 0) CharacterCyan else CharacterGreen
    }
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (useVerticalLayout) CatalogDimens.dp280 else CatalogDimens.dp164)
            .semantics(mergeDescendants = true) { role = Role.Button },
        shape = CutCornerShape(CatalogDimens.dp6),
        colors = CardDefaults.cardColors(containerColor = CharacterSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = CatalogDimens.dp3)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (useVerticalLayout) {
                Column(Modifier.padding(CatalogDimens.dp14)) {
                    CharacterPortrait(
                        character = character,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(LARGE_CARD_ASPECT_RATIO)
                    )
                    Spacer(Modifier.height(CatalogDimens.dp12))
                    CharacterSummary(character)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(CatalogDimens.dp12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterPortrait(
                        character = character,
                        modifier = Modifier.size(
                            width = CatalogDimens.dp108,
                            height = CatalogDimens.dp140
                        )
                    )
                    Spacer(Modifier.width(CatalogDimens.dp14))
                    CharacterSummary(
                        character = character,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            CharacterTechFrame(Modifier.fillMaxSize(), accent)
        }
    }
}

@Composable
private fun CharacterPortrait(character: Character, modifier: Modifier) {
    AsyncImage(
        model = character.imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(CatalogDimens.dp8))
            .border(
                BorderStroke(CatalogDimens.dp1, CharacterCyan.copy(alpha = .4f)),
                RoundedCornerShape(CatalogDimens.dp8)
            )
    )
}

@Composable
private fun CharacterSummary(character: Character, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp4)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = character.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = CharacterText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(CatalogDimens.dp8))
            StatusPill(character.status)
        }
        CharacterMetadata(
            icon = Icons.Outlined.LocationOn,
            label = stringResource(Res.string.character_location_label),
            value = localizedBackendValue(character.location)
        )
        CharacterMetadata(
            icon = Icons.Outlined.Public,
            label = stringResource(Res.string.character_origin_label),
            value = localizedBackendValue(character.origin)
        )
        CharacterMetadata(
            icon = Icons.Outlined.Tv,
            label = stringResource(Res.string.character_episodes_label),
            value = pluralStringResource(
                Res.plurals.character_episodes_short,
                character.episodeCount,
                character.episodeCount
            )
        )
    }
}

@Composable
internal fun StatusPill(status: String) {
    val label = localizedStatus(status)
    val style = characterStatusStyle(status)
    Surface(
        shape = CircleShape,
        color = style.color.copy(alpha = .12f),
        border = BorderStroke(CatalogDimens.dp1, style.color.copy(alpha = .55f)),
        shadowElevation = CatalogDimens.dp1
    ) {
        Row(
            modifier = Modifier.padding(horizontal = CatalogDimens.dp8, vertical = CatalogDimens.dp4),
            horizontalArrangement = Arrangement.spacedBy(CatalogDimens.dp6),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusGlyph(status = status, modifier = Modifier.size(CatalogDimens.dp16))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = style.color
            )
        }
    }
}

@Composable
private fun CharacterMetadata(icon: ImageVector, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(CatalogDimens.dp8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(CatalogDimens.dp26),
            shape = CircleShape,
            color = CharacterGreen.copy(alpha = .08f),
            border = BorderStroke(CatalogDimens.dp1, CharacterGreen.copy(alpha = .34f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(CatalogDimens.dp16),
                    tint = CharacterGreen
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = CharacterTextMuted,
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = CharacterText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun StatusGlyph(status: String, modifier: Modifier = Modifier) {
    val style = characterStatusStyle(status)
    if (style.kind == CharacterStatusKind.UNKNOWN) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                repeat(5) { segment ->
                    drawArc(
                        color = style.color,
                        startAngle = segment * 72f + 8f,
                        sweepAngle = 43f,
                        useCenter = false,
                        style = Stroke(width = size.minDimension * .12f)
                    )
                }
            }
            Text(
                text = "?",
                color = style.color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black
            )
        }
    } else {
        Canvas(modifier) {
            val middle = size.height * .5f
            val path = Path().apply {
                moveTo(0f, middle)
                lineTo(size.width * .18f, middle)
                lineTo(size.width * .29f, size.height * .7f)
                lineTo(size.width * .43f, size.height * .18f)
                lineTo(size.width * .56f, size.height * .78f)
                lineTo(size.width * .68f, middle)
                if (style.kind == CharacterStatusKind.ALIVE) {
                    lineTo(size.width, middle)
                } else {
                    lineTo(size.width * .79f, middle)
                    moveTo(size.width * .9f, middle)
                    lineTo(size.width, middle)
                }
            }
            drawPath(path, style.color, style = Stroke(width = size.minDimension * .12f))
        }
    }
}

internal enum class CharacterStatusKind { ALIVE, DEAD, UNKNOWN }

internal data class CharacterStatusStyle(val color: Color, val kind: CharacterStatusKind)

internal fun characterStatusStyle(status: String): CharacterStatusStyle = when (status.lowercase()) {
    "alive" -> CharacterStatusStyle(CharacterGreen, CharacterStatusKind.ALIVE)
    "dead" -> CharacterStatusStyle(Color(0xFFFF5C70), CharacterStatusKind.DEAD)
    else -> CharacterStatusStyle(Color(0xFFFFC857), CharacterStatusKind.UNKNOWN)
}

private const val LARGE_FONT_SCALE = 1.3f
private const val LARGE_CARD_ASPECT_RATIO = 1.6f
