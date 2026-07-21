@file:Suppress("MagicNumber")

package es.davidnavarro.androidcleanarchitecture.feature.episodes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

internal val EpisodeBackground = Color(0xFF020810)
internal val EpisodeSurface = Color(0xEB0C1822)
internal val EpisodeSurfaceStrong = Color(0xFC07121B)
internal val TransmissionGreen = Color(0xFFB8FF6A)
internal val TransmissionCyan = Color(0xFF64DFF4)
internal val EpisodeText = Color(0xFFF6F8F4)
internal val EpisodeTextMuted = Color(0xFFAAB5BC)

internal val EpisodeTransmissionCardShape = GenericShape { size, _ ->
    val cut = size.height * .09f
    moveTo(cut, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width * .91f, size.height)
    lineTo(cut, size.height)
    lineTo(0f, size.height - cut)
    lineTo(0f, cut)
    close()
}

internal val EpisodeCodeShape = GenericShape { size, _ ->
    val cut = size.height * .22f
    moveTo(cut, 0f)
    lineTo(size.width - cut, 0f)
    lineTo(size.width, size.height * .5f)
    lineTo(size.width - cut, size.height)
    lineTo(cut, size.height)
    lineTo(0f, size.height * .5f)
    close()
}

@Composable
internal fun EpisodeSpaceBackdrop(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF01060C), Color(0xFF07141E), Color(0xFF02070C))))
    ) {
        val stars = listOf(
            .05f to .09f, .16f to .22f, .31f to .07f, .44f to .19f, .59f to .05f,
            .73f to .28f, .9f to .13f, .11f to .45f, .28f to .61f, .52f to .41f,
            .69f to .57f, .93f to .7f, .08f to .83f, .39f to .92f, .65f to .78f, .85f to .95f
        )
        stars.forEachIndexed { index, (x, y) ->
            drawCircle(
                color = if (index % 4 == 0) TransmissionCyan.copy(alpha = .62f) else Color.White.copy(alpha = .34f),
                radius = if (index % 3 == 0) 2.2f else 1.25f,
                center = Offset(size.width * x, size.height * y)
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                listOf(TransmissionGreen.copy(alpha = .16f), TransmissionCyan.copy(alpha = .05f), Color.Transparent),
                center = Offset(size.width * .88f, size.height * .12f),
                radius = size.width * .5f
            ),
            radius = size.width * .5f,
            center = Offset(size.width * .88f, size.height * .12f)
        )
    }
}

@Composable
internal fun TransmissionMarker(size: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = min(this.size.width, this.size.height) / 2f
            drawCircle(Brush.radialGradient(listOf(TransmissionGreen.copy(.4f), Color.Transparent)))
            repeat(4) { ring ->
                drawCircle(
                    color = TransmissionGreen.copy(alpha = .92f - ring * .2f),
                    radius = radius * (.58f + ring * .12f),
                    style = Stroke(width = (4f - ring * .7f).coerceAtLeast(1f), cap = StrokeCap.Round)
                )
            }
        }
        Box(
            Modifier
                .size(size * .5f)
                .background(EpisodeSurfaceStrong, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PlayArrow, null, Modifier.size(size * .34f), tint = TransmissionGreen)
        }
    }
}

@Composable
internal fun AnimatedEpisodeDial(size: Dp, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "episodeDial")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "artworkRotation"
    )
    val artwork = ImageBitmap.imageResource(R.drawable.episode_list_background)
    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = rotation
                shape = CircleShape
                clip = true
            }
    ) {
        drawImage(
            image = artwork,
            srcOffset = IntOffset(x = 515, y = 70),
            srcSize = IntSize(width = 220, height = 220),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(
                width = this.size.width.roundToInt(),
                height = this.size.height.roundToInt()
            )
        )
    }
}

@Composable
internal fun EpisodeListBackdrop(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.episode_list_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .12f))
        )
    }
}

@Composable
internal fun EpisodeArtwork(episodeId: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(episodeArtworkResource(episodeId)),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.CenterEnd
    )
}

@Composable
internal fun EpisodeDetailArtwork(episodeId: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(EpisodeBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(episodeArtworkResource(episodeId)),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(.16f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Column(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(episodeArtworkResource(episodeId)),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            Image(
                painter = painterResource(episodeArtworkResource(episodeId)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { scaleY = -1f }
                    .alpha(.24f),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.BottomCenter
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = .08f),
                        .24f to Color.Transparent,
                        .42f to EpisodeBackground.copy(alpha = .58f),
                        .68f to EpisodeBackground.copy(alpha = .84f),
                        1f to EpisodeBackground.copy(alpha = .95f)
                    )
                )
        )
    }
}

@Composable
internal fun EpisodeTechFrame(modifier: Modifier = Modifier, accent: Color = TransmissionCyan) {
    Canvas(modifier) {
        val cut = 18f
        val topLeft = Path().apply {
            moveTo(cut, 1f)
            lineTo(size.width * .25f, 1f)
            moveTo(1f, size.height * .46f)
            lineTo(1f, cut)
        }
        val topRight = Path().apply {
            moveTo(size.width * .76f, 1f)
            lineTo(size.width - 1f, 1f)
            lineTo(size.width - cut, size.height * .22f)
        }
        val bottomLeft = Path().apply {
            moveTo(1f, size.height * .64f)
            lineTo(1f, size.height - cut)
            lineTo(cut, size.height - 1f)
            lineTo(size.width * .32f, size.height - 1f)
        }
        val bottomRight = Path().apply {
            moveTo(size.width * .68f, size.height - 1f)
            lineTo(size.width * .91f, size.height - 1f)
            lineTo(size.width - 1f, size.height * .62f)
        }
        listOf(topLeft, topRight, bottomLeft, bottomRight).forEach {
            drawPath(it, accent.copy(alpha = .82f), style = Stroke(width = 2.4f))
        }
        val waveform = Path().apply {
            val baseline = size.height - 8f
            moveTo(size.width * .08f, baseline)
            repeat(48) { index ->
                val x = size.width * (.08f + index / 54f)
                val amplitude = 2f + (index % 5) * 1.15f
                val y = baseline + sin(index * 1.73f) * amplitude
                lineTo(x, y)
            }
        }
        drawPath(waveform, TransmissionCyan.copy(alpha = .68f), style = Stroke(width = 1.35f))
        drawCircle(TransmissionCyan, 3.2f, Offset(size.width * .56f, size.height - 8f), style = Stroke(1.5f))
    }
}

@Composable
internal fun EpisodeTimelineRail(isFirst: Boolean, isLast: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val center = Offset(size.width * .5f, size.height * .5f)
        if (!isFirst) drawLine(TransmissionGreen.copy(.72f), Offset(center.x, 0f), center, 2f)
        if (!isLast) drawLine(TransmissionGreen.copy(.72f), center, Offset(center.x, size.height), 2f)
        drawCircle(TransmissionGreen.copy(.12f), size.width * .44f, center)
        drawCircle(TransmissionGreen.copy(.84f), size.width * .29f, center, style = Stroke(2f))
        drawCircle(TransmissionGreen, size.width * .12f, center)
    }
}

@Composable
internal fun EpisodeCircuitDivider(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val middle = size.width * .68f
        val path = Path().apply {
            moveTo(0f, size.height * .45f)
            lineTo(middle - 28f, size.height * .45f)
            lineTo(middle - 18f, size.height * .8f)
            lineTo(middle + 18f, size.height * .8f)
            lineTo(middle + 28f, size.height * .45f)
            lineTo(size.width, size.height * .45f)
        }
        drawPath(path, TransmissionGreen.copy(alpha = .72f), style = Stroke(width = 2f))
    }
}

private fun episodeArtworkResource(episodeId: Int): Int =
    episodeArtworkResources[(episodeId - 1).coerceIn(episodeArtworkResources.indices)]

private val episodeArtworkResources = intArrayOf(
    R.drawable.episode_01,
    R.drawable.episode_02,
    R.drawable.episode_03,
    R.drawable.episode_04,
    R.drawable.episode_05,
    R.drawable.episode_06,
    R.drawable.episode_07,
    R.drawable.episode_08,
    R.drawable.episode_09,
    R.drawable.episode_10,
    R.drawable.episode_11,
    R.drawable.episode_12,
    R.drawable.episode_13,
    R.drawable.episode_14,
    R.drawable.episode_15,
    R.drawable.episode_16,
    R.drawable.episode_17,
    R.drawable.episode_18,
    R.drawable.episode_19,
    R.drawable.episode_20,
    R.drawable.episode_21,
    R.drawable.episode_22,
    R.drawable.episode_23,
    R.drawable.episode_24,
    R.drawable.episode_25,
    R.drawable.episode_26,
    R.drawable.episode_27,
    R.drawable.episode_28,
    R.drawable.episode_29,
    R.drawable.episode_30,
    R.drawable.episode_31,
    R.drawable.episode_32,
    R.drawable.episode_33,
    R.drawable.episode_34,
    R.drawable.episode_35,
    R.drawable.episode_36,
    R.drawable.episode_37,
    R.drawable.episode_38,
    R.drawable.episode_39,
    R.drawable.episode_40,
    R.drawable.episode_41,
    R.drawable.episode_42,
    R.drawable.episode_43,
    R.drawable.episode_44,
    R.drawable.episode_45,
    R.drawable.episode_46,
    R.drawable.episode_47,
    R.drawable.episode_48,
    R.drawable.episode_49,
    R.drawable.episode_50,
    R.drawable.episode_51
)
