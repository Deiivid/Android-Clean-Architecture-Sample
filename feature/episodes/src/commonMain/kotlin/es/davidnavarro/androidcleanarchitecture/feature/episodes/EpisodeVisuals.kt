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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_01
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_02
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_03
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_04
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_05
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_06
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_07
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_08
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_09
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_10
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_11
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_12
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_13
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_14
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_15
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_16
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_17
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_18
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_19
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_20
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_21
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_22
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_23
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_24
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_25
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_26
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_27
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_28
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_29
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_30
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_31
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_32
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_33
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_34
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_35
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_36
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_37
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_38
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_39
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_40
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_41
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_42
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_43
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_44
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_45
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_46
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_47
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_48
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_49
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_50
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_51
import es.davidnavarro.androidcleanarchitecture.feature.episodes.generated.resources.episode_list_background
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

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
    val artwork = imageResource(Res.drawable.episode_list_background)
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
            painter = painterResource(Res.drawable.episode_list_background),
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

private fun episodeArtworkResource(episodeId: Int): DrawableResource =
    episodeArtworkResources[(episodeId - 1).coerceIn(episodeArtworkResources.indices)]

private val episodeArtworkResources = listOf(
    Res.drawable.episode_01,
    Res.drawable.episode_02,
    Res.drawable.episode_03,
    Res.drawable.episode_04,
    Res.drawable.episode_05,
    Res.drawable.episode_06,
    Res.drawable.episode_07,
    Res.drawable.episode_08,
    Res.drawable.episode_09,
    Res.drawable.episode_10,
    Res.drawable.episode_11,
    Res.drawable.episode_12,
    Res.drawable.episode_13,
    Res.drawable.episode_14,
    Res.drawable.episode_15,
    Res.drawable.episode_16,
    Res.drawable.episode_17,
    Res.drawable.episode_18,
    Res.drawable.episode_19,
    Res.drawable.episode_20,
    Res.drawable.episode_21,
    Res.drawable.episode_22,
    Res.drawable.episode_23,
    Res.drawable.episode_24,
    Res.drawable.episode_25,
    Res.drawable.episode_26,
    Res.drawable.episode_27,
    Res.drawable.episode_28,
    Res.drawable.episode_29,
    Res.drawable.episode_30,
    Res.drawable.episode_31,
    Res.drawable.episode_32,
    Res.drawable.episode_33,
    Res.drawable.episode_34,
    Res.drawable.episode_35,
    Res.drawable.episode_36,
    Res.drawable.episode_37,
    Res.drawable.episode_38,
    Res.drawable.episode_39,
    Res.drawable.episode_40,
    Res.drawable.episode_41,
    Res.drawable.episode_42,
    Res.drawable.episode_43,
    Res.drawable.episode_44,
    Res.drawable.episode_45,
    Res.drawable.episode_46,
    Res.drawable.episode_47,
    Res.drawable.episode_48,
    Res.drawable.episode_49,
    Res.drawable.episode_50,
    Res.drawable.episode_51
)
