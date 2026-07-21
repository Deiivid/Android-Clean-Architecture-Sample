@file:Suppress("MagicNumber")

package es.davidnavarro.androidcleanarchitecture.feature.characters

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

internal fun DrawScope.drawAliveBiometrics(
    radius: Float,
    rotation: Float,
    counterRotation: Float,
    phase: Float,
    accent: Color
) {
    val beat = heartbeatStrength(phase)
    drawCircle(
        Brush.radialGradient(
            listOf(accent.copy(alpha = .3f + beat * .12f), CharacterCyan.copy(alpha = .08f), Color.Transparent)
        )
    )
    listOf(.59f, .69f, .8f, .91f).forEachIndexed { index, factor ->
        drawGlowCircle(
            color = if (index % 2 == 0) accent else CharacterCyan,
            radius = radius * (factor + beat * .008f),
            strokeWidth = if (index == 0) 3.2f else 2f,
            alpha = .58f + beat * .18f
        )
    }
    repeat(3) { index ->
        drawArcOnRing(
            color = if (index == 1) CharacterCyan else accent,
            radius = radius * (.68f + index * .11f),
            startAngle = rotation * (if (index % 2 == 0) 1f else -1f) + index * 74f,
            sweepAngle = 34f + index * 10f,
            strokeWidth = 4.5f - index
        )
    }
    repeat(8) { index ->
        val point = orbitPoint(
            center = center,
            radius = radius * if (index % 2 == 0) .89f else .79f,
            angleDegrees = -90f + rotation + index * 45f
        )
        drawGlowNode(
            color = if (index % 3 == 0) CharacterCyan else accent,
            point = point,
            nodeRadius = radius * if (index % 3 == 0) .026f else .018f
        )
    }
    drawGlowNode(
        CharacterCyan,
        orbitPoint(center, radius * .7f, counterRotation),
        radius * .024f
    )
}

internal fun DrawScope.drawDeadBiometrics(radius: Float, rotation: Float, phase: Float) {
    val ember = .32f + abs(sin(phase * Math.PI.toFloat())) * .38f
    drawCircle(
        Brush.radialGradient(
            listOf(CharacterRed.copy(alpha = .18f), CharacterPurple.copy(alpha = .06f), Color.Transparent)
        )
    )
    repeat(4) { ring ->
        repeat(3) { segment ->
            drawArcOnRing(
                color = if (ring % 2 == 0) CharacterRed else CharacterPurple,
                radius = radius * (.59f + ring * .1f),
                startAngle =
                rotation * (if (ring % 2 == 0) .18f else -.06f) +
                    segment * 121f +
                    ring * 17f,
                sweepAngle = 54f - ring * 4f,
                strokeWidth = if (ring == 0) 3.2f else 2.2f,
                alpha = .5f + ember * .25f
            )
        }
    }
    repeat(5) { index ->
        drawGlowNode(
            color = if (index % 2 == 0) CharacterRed else CharacterPurple,
            point = orbitPoint(
                center,
                radius * (.76f + index % 2 * .13f),
                rotation * .12f + index * 79f
            ),
            nodeRadius = radius * .024f,
            alpha = ember
        )
    }
}

internal fun DrawScope.drawUnknownBiometrics(
    radius: Float,
    rotation: Float,
    counterRotation: Float,
    phase: Float,
    questionPaint: Paint
) {
    drawCircle(
        Brush.radialGradient(
            listOf(CharacterPurple.copy(alpha = .2f), CharacterCyan.copy(alpha = .08f), Color.Transparent)
        )
    )
    val palette = listOf(CharacterPurple, CharacterCyan, CharacterAmber)
    repeat(4) { ring ->
        repeat(8) { segment ->
            val blink = .38f + .5f * abs(sin((phase + segment * .13f + ring * .19f) * Math.PI.toFloat()))
            drawArcOnRing(
                color = palette[(segment + ring) % palette.size],
                radius = radius * (.59f + ring * .1f),
                startAngle = (if (ring % 2 == 0) rotation else counterRotation) + segment * 45f + ring * 9f,
                sweepAngle = 18f + (segment % 3) * 4f,
                strokeWidth = if (ring == 0) 3f else 2f,
                alpha = blink
            )
        }
    }
    repeat(12) { index ->
        val questionColor = palette[index % palette.size]
        val floatWave = sin((phase + index * .17f) * Math.PI.toFloat() * 2f)
        val angle = -15f + index * 30f
        val point = orbitPoint(
            center = center,
            radius = radius * (if (index % 2 == 0) .88f else 1f) + radius * floatWave * .07f,
            angleDegrees = angle + counterRotation * if (index % 2 == 0) .08f else -.06f
        )
        questionPaint.color = questionColor.toArgb()
        questionPaint.textSize = radius * (.16f + floatWave * .012f)
        questionPaint.alpha = (205f + floatWave * 35f).toInt()
        questionPaint.setShadowLayer(radius * .035f, 0f, 0f, questionColor.copy(alpha = .7f).toArgb())
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                "?",
                point.x - questionPaint.measureText("?") / 2f,
                point.y - (questionPaint.ascent() + questionPaint.descent()) / 2f,
                questionPaint
            )
        }
    }
}

private fun DrawScope.drawGlowCircle(color: Color, radius: Float, strokeWidth: Float, alpha: Float) {
    drawCircle(color.copy(alpha = alpha * .12f), radius, style = Stroke(width = strokeWidth * 5f))
    drawCircle(color.copy(alpha = alpha * .3f), radius, style = Stroke(width = strokeWidth * 2.3f))
    drawCircle(color.copy(alpha = alpha), radius, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}

@Suppress("LongParameterList")
private fun DrawScope.drawArcOnRing(
    color: Color,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    strokeWidth: Float,
    alpha: Float = .86f
) {
    val topLeft = Offset(center.x - radius, center.y - radius)
    val arcSize = Size(radius * 2f, radius * 2f)
    drawArc(
        color.copy(alpha = alpha * .15f),
        startAngle,
        sweepAngle,
        false,
        topLeft,
        arcSize,
        style = Stroke(width = strokeWidth * 4f, cap = StrokeCap.Round)
    )
    drawArc(
        color.copy(alpha = alpha),
        startAngle,
        sweepAngle,
        false,
        topLeft,
        arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawGlowNode(color: Color, point: Offset, nodeRadius: Float, alpha: Float = 1f) {
    drawCircle(color.copy(alpha = alpha * .12f), nodeRadius * 3.8f, point)
    drawCircle(color.copy(alpha = alpha * .34f), nodeRadius * 2.2f, point)
    drawCircle(color.copy(alpha = alpha), nodeRadius, point)
}

private fun orbitPoint(center: Offset, radius: Float, angleDegrees: Float): Offset {
    val angle = Math.toRadians(angleDegrees.toDouble())
    return Offset(
        x = center.x + cos(angle).toFloat() * radius,
        y = center.y + sin(angle).toFloat() * radius
    )
}

private fun heartbeatStrength(phase: Float): Float {
    val firstBeat = max(0f, 1f - abs(phase - .12f) / .08f)
    val secondBeat = max(0f, 1f - abs(phase - .3f) / .11f) * .7f
    return max(firstBeat, secondBeat)
}
