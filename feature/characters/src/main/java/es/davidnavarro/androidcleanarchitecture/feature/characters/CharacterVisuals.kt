@file:Suppress("LongMethod", "MagicNumber")

package es.davidnavarro.androidcleanarchitecture.feature.characters

import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal val CharacterBackground = Color(0xFF020810)
internal val CharacterSurface = Color(0xEE0A1720)
internal val CharacterSurfaceStrong = Color(0xFC06121A)
internal val CharacterGreen = Color(0xFFB8FF6A)
internal val CharacterCyan = Color(0xFF63DCF2)
internal val CharacterPurple = Color(0xFF8B67F5)
internal val CharacterRed = Color(0xFFFF4F64)
internal val CharacterAmber = Color(0xFFFFB547)
internal val CharacterText = Color(0xFFF5F7F2)
internal val CharacterTextMuted = Color(0xFFADB8BF)

@Composable
internal fun CharacterSpaceBackdrop(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF01060C), Color(0xFF06131D), Color(0xFF02070C))
                )
            )
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(CharacterPurple.copy(alpha = .15f), Color.Transparent),
                center = Offset(size.width * .08f, size.height * .42f),
                radius = size.width * .58f
            ),
            radius = size.width * .58f,
            center = Offset(size.width * .08f, size.height * .42f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(CharacterGreen.copy(alpha = .13f), CharacterCyan.copy(alpha = .04f), Color.Transparent),
                center = Offset(size.width * .9f, size.height * .1f),
                radius = size.width * .52f
            ),
            radius = size.width * .52f,
            center = Offset(size.width * .9f, size.height * .1f)
        )
        val stars = listOf(
            .04f to .08f, .15f to .21f, .28f to .06f, .42f to .17f,
            .58f to .05f, .72f to .27f, .91f to .11f, .1f to .46f,
            .25f to .63f, .49f to .4f, .67f to .55f, .94f to .7f,
            .07f to .84f, .38f to .93f, .64f to .78f, .86f to .95f
        )
        stars.forEachIndexed { index, (x, y) ->
            drawCircle(
                color = if (index % 4 == 0) CharacterCyan.copy(alpha = .6f) else Color.White.copy(alpha = .34f),
                radius = if (index % 3 == 0) 2.2f else 1.25f,
                center = Offset(size.width * x, size.height * y)
            )
        }
    }
}

@Composable
internal fun CharacterNetworkScanner(size: Dp, modifier: Modifier = Modifier) {
    val scannerTransition = rememberInfiniteTransition(label = "character network scanner")
    val orbitRotation = scannerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "network orbit rotation"
    )
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = min(this.size.width, this.size.height) / 2f
            val center = this.center
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(CharacterGreen.copy(alpha = .35f), CharacterCyan.copy(alpha = .08f), Color.Transparent)
                )
            )
            repeat(5) { ring ->
                drawCircle(
                    color = if (ring % 2 == 0) {
                        CharacterGreen.copy(alpha = .86f - ring * .13f)
                    } else {
                        CharacterCyan.copy(alpha = .72f - ring * .1f)
                    },
                    radius = radius * (.44f + ring * .12f),
                    style = Stroke(width = (3.4f - ring * .42f).coerceAtLeast(1f), cap = StrokeCap.Round)
                )
            }
            val sweepAngle = Math.toRadians((orbitRotation.value - 90f).toDouble())
            drawLine(
                color = CharacterGreen.copy(alpha = .28f),
                start = center,
                end = Offset(
                    x = center.x + cos(sweepAngle).toFloat() * radius * .94f,
                    y = center.y + sin(sweepAngle).toFloat() * radius * .94f
                ),
                strokeWidth = 2f
            )
            repeat(5) { index ->
                val angle = Math.toRadians((-90 + orbitRotation.value + index * 72).toDouble())
                val nodeCenter = Offset(
                    x = center.x + cos(angle).toFloat() * radius * .78f,
                    y = center.y + sin(angle).toFloat() * radius * .78f
                )
                drawCircle(CharacterSurfaceStrong, radius * .105f, nodeCenter)
                drawCircle(
                    CharacterCyan.copy(alpha = .8f),
                    radius * .105f,
                    nodeCenter,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(CharacterGreen, radius * .032f, nodeCenter - Offset(0f, radius * .025f))
                drawArc(
                    color = CharacterGreen,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = nodeCenter - Offset(radius * .052f, 0f),
                    size = androidx.compose.ui.geometry.Size(radius * .104f, radius * .075f),
                    style = Stroke(width = 1.4f)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(size * .42f)
                .background(CharacterSurfaceStrong, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Groups,
                contentDescription = null,
                tint = CharacterGreen,
                modifier = Modifier.size(size * .25f)
            )
        }
    }
}

@Composable
internal fun CharacterTechFrame(modifier: Modifier = Modifier, accent: Color = CharacterCyan) {
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
        drawPath(frame, accent.copy(alpha = .75f), style = Stroke(width = 2.4f))
        drawLine(accent.copy(alpha = .56f), Offset(cut + 18f, 7f), Offset(size.width * .38f, 7f), 1.5f)
        drawLine(accent.copy(alpha = .56f), Offset(size.width * .62f, 7f), Offset(size.width - cut - 18f, 7f), 1.5f)
        drawLine(
            accent.copy(alpha = .58f),
            Offset(size.width * .42f, size.height - 7f),
            Offset(size.width * .58f, size.height - 7f),
            1.5f
        )
    }
}

@Composable
internal fun CharacterBiometricRings(
    modifier: Modifier = Modifier,
    accent: Color = CharacterGreen,
    status: String = "Alive"
) {
    val statusKind = characterStatusStyle(status).kind
    val orbitTransition = rememberInfiniteTransition(label = "character status orbit")
    val orbitRotation = orbitTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 14_000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit rotation"
    )
    val counterRotation = orbitTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 17_000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter orbit rotation"
    )
    val statusPhase = orbitTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2_400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "status phase"
    )
    val questionPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }
    Canvas(modifier) {
        val radius = min(size.width, size.height) / 2f
        when (statusKind) {
            CharacterStatusKind.ALIVE -> drawAliveBiometrics(
                radius = radius,
                rotation = orbitRotation.value,
                counterRotation = counterRotation.value,
                phase = statusPhase.value,
                accent = accent
            )
            CharacterStatusKind.DEAD -> drawDeadBiometrics(
                radius = radius,
                rotation = orbitRotation.value,
                phase = statusPhase.value
            )
            CharacterStatusKind.UNKNOWN -> drawUnknownBiometrics(
                radius = radius,
                rotation = orbitRotation.value,
                counterRotation = counterRotation.value,
                phase = statusPhase.value,
                questionPaint = questionPaint
            )
        }
    }
}

@Composable
internal fun CharacterCircuitDivider(modifier: Modifier = Modifier, accent: Color = CharacterGreen) {
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
        drawPath(path, accent.copy(alpha = .72f), style = Stroke(width = 2f))
    }
}
