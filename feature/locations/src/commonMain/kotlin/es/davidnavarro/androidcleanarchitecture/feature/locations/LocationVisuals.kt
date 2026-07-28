@file:Suppress("MagicNumber")

package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_card_frame_reference
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_list_background_clean_v6
import kotlin.math.min
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

internal val SpaceBackground = Color(0xFF030A12)
internal val SpaceSurface = Color(0xE6111D27)
internal val SpaceSurfaceStrong = Color(0xFA0B151E)
internal val PortalGreen = Color(0xFFB8FF6A)
internal val PortalGreenDark = Color(0xFF386B27)
internal val PortalCyan = Color(0xFF63DCF2)
internal val SpaceText = Color(0xFFF5F7F2)
internal val SpaceTextMuted = Color(0xFFADB8BF)

@Composable
internal fun LocationListBackdrop(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.location_list_background_clean_v6),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .1f))
        )
    }
}

@Composable
internal fun SpaceBackdrop(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020812), Color(0xFF07131C), Color(0xFF02070C))
                )
            )
    ) {
        val stars = listOf(
            .07f to .10f, .18f to .24f, .33f to .08f, .47f to .18f,
            .63f to .06f, .78f to .27f, .91f to .12f, .12f to .48f,
            .29f to .62f, .54f to .43f, .72f to .58f, .94f to .72f,
            .08f to .84f, .41f to .91f, .67f to .79f, .86f to .94f
        )
        stars.forEachIndexed { index, (x, y) ->
            drawCircle(
                color = if (index % 4 == 0) PortalCyan.copy(alpha = .55f) else Color.White.copy(alpha = .32f),
                radius = if (index % 3 == 0) 2.2f else 1.3f,
                center = Offset(size.width * x, size.height * y)
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(PortalGreen.copy(alpha = .16f), Color.Transparent),
                center = Offset(size.width * .92f, size.height * .12f),
                radius = size.width * .45f
            ),
            radius = size.width * .45f,
            center = Offset(size.width * .92f, size.height * .12f)
        )
    }
}

@Composable
internal fun PortalMarker(size: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = min(this.size.width, this.size.height) / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PortalGreen.copy(alpha = .42f), PortalGreenDark.copy(alpha = .18f), Color.Transparent)
                )
            )
            repeat(3) { ring ->
                drawCircle(
                    color = PortalGreen.copy(alpha = .9f - ring * .22f),
                    radius = radius * (.68f + ring * .12f),
                    style = Stroke(width = (4f - ring).coerceAtLeast(1f), cap = StrokeCap.Round)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(size * .54f)
                .background(SpaceSurfaceStrong, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = PortalGreen,
                modifier = Modifier.size(size * .34f)
            )
        }
    }
}

@Composable
internal fun LocationArtwork(location: Location, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(locationArtwork(location)),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.CenterEnd,
        colorFilter = LocationArtworkBrightness
    )
}

@Composable
internal fun LocationDetailArtwork(location: Location, modifier: Modifier = Modifier) {
    val artwork = imageResource(locationDetailArtwork(location))
    Image(
        bitmap = artwork,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        filterQuality = FilterQuality.High
    )
}

@Composable
internal fun TechFrame(modifier: Modifier = Modifier, accent: Color = PortalCyan) {
    Canvas(modifier) {
        val cut = 18f
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
        drawPath(frame, accent.copy(alpha = .72f), style = Stroke(width = 2.4f))
        drawLine(accent.copy(.55f), Offset(cut + 18f, 7f), Offset(size.width * .38f, 7f), 1.5f)
        drawLine(accent.copy(.55f), Offset(size.width * .62f, 7f), Offset(size.width - cut - 18f, 7f), 1.5f)
        drawLine(
            PortalGreen.copy(.55f),
            Offset(size.width * .42f, size.height - 7f),
            Offset(size.width * .58f, size.height - 7f),
            1.5f
        )
        listOf(
            Offset(cut + 6f, 7f),
            Offset(size.width - cut - 6f, 7f),
            Offset(cut + 6f, size.height - 7f),
            Offset(size.width - cut - 6f, size.height - 7f)
        ).forEach { drawCircle(accent.copy(.85f), 2.2f, it) }
    }
}

@Composable
internal fun DetailTechFrame(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val inset = 2f
        val radius = 28f
        drawRoundRect(
            color = PortalCyan.copy(alpha = .66f),
            topLeft = Offset(inset, inset),
            size = size.copy(width = size.width - inset * 2, height = size.height - inset * 2),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = 2.2f)
        )
        drawLine(
            PortalGreen.copy(alpha = .62f),
            Offset(size.width * .08f, 7f),
            Offset(size.width * .36f, 7f),
            1.5f
        )
        drawLine(
            PortalCyan.copy(alpha = .62f),
            Offset(size.width * .64f, 7f),
            Offset(size.width * .92f, 7f),
            1.5f
        )
        drawLine(
            PortalGreen.copy(alpha = .48f),
            Offset(size.width * .42f, size.height - 7f),
            Offset(size.width * .58f, size.height - 7f),
            1.5f
        )
    }
}

@Composable
internal fun ReferenceTechFrame(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.location_card_frame_reference),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}

@Composable
internal fun HeroCircuitDivider(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val middle = size.width * .72f
        val path = Path().apply {
            moveTo(0f, size.height * .45f)
            lineTo(middle - 28f, size.height * .45f)
            lineTo(middle - 18f, size.height * .8f)
            lineTo(middle + 18f, size.height * .8f)
            lineTo(middle + 28f, size.height * .45f)
            lineTo(size.width, size.height * .45f)
        }
        drawPath(path, PortalGreen.copy(alpha = .72f), style = Stroke(width = 2f))
    }
}

private val LocationArtworkBrightness = ColorFilter.colorMatrix(
    ColorMatrix().apply { setToScale(redScale = 1.14f, greenScale = 1.14f, blueScale = 1.14f, alphaScale = 1f) }
)
