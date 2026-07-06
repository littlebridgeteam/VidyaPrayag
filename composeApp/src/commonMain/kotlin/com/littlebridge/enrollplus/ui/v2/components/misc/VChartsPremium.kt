package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/** One slice of a [VDonutPremium] or row of [VBarsPremium]. */
data class VChartDatumPremium(val label: String, val value: Float, val color: Color? = null)

/**
 * Premium donut chart — animated multi-segment ring with center slot.
 * Uses M3 Expressive tokens for track and default segment colors.
 */
@Composable
fun VDonutPremium(
    data: List<VChartDatumPremium>,
    modifier: Modifier = Modifier,
    size: Dp = 168.dp,
    thickness: Dp = 18.dp,
    center: (@Composable () -> Unit)? = null,
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f
    val progress by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "donut")
    val trackColor = VColors.SurfaceContainerHigh
    val defaultSegColor = VColors.Primary

    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val sw = thickness.toPx()
            val inset = sw / 2f
            val arcSize = Size(this.size.width - sw, this.size.height - sw)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(width = sw),
            )
            var startAngle = -90f
            data.forEach { d ->
                val sweep = (d.value / total) * 360f * progress
                val segColor = d.color ?: defaultSegColor
                drawArc(
                    color = segColor,
                    startAngle = startAngle, sweepAngle = sweep, useCenter = false,
                    topLeft = topLeft, size = arcSize,
                    style = Stroke(width = sw),
                )
                startAngle += (d.value / total) * 360f
            }
        }
        center?.invoke()
    }
}

/**
 * Premium sparkline — filled area + line micro chart with end dot.
 * Uses M3 Expressive tokens for default color.
 */
@Composable
fun VSparklinePremium(
    values: List<Float>,
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 36.dp,
    color: Color? = null,
) {
    val lineColor = color ?: VColors.Tertiary
    if (values.size < 2) {
        Box(modifier.size(width, height))
        return
    }
    val animated by animateFloatAsState(targetValue = 1f, animationSpec = tween(1100), label = "spark")

    Canvas(modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height
        val min = values.min()
        val max = values.max()
        val span = (max - min).takeIf { it != 0f } ?: 1f
        val stepX = w / (values.size - 1)

        fun pointAt(i: Int): Offset {
            val x = i * stepX
            val y = h - ((values[i] - min) / span) * (h - 4f) - 2f
            return Offset(x, y)
        }

        val pts = values.indices.map { pointAt(it) }

        val area = Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            area,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.28f), lineColor.copy(alpha = 0f)),
            ),
        )

        val revealX = w * animated
        val line = Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) {
                if (pts[i].x <= revealX) lineTo(pts[i].x, pts[i].y)
            }
        }
        drawPath(line, color = lineColor, style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val last = pts.last()
        drawCircle(color = lineColor, radius = 3f, center = last)
    }
}

/**
 * Premium bar chart — vertical bars with last bar highlighted.
 * Uses M3 Expressive tokens for colors and typography.
 */
@Composable
fun VBarsPremium(
    data: List<VChartDatumPremium>,
    modifier: Modifier = Modifier,
    height: Dp = 140.dp,
) {
    val max = (data.maxOfOrNull { it.value } ?: 1f).takeIf { it > 0f } ?: 1f
    val progress by animateFloatAsState(targetValue = 1f, animationSpec = tween(600), label = "bars")

    Row(
        modifier.height(height).fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        data.forEachIndexed { i, d ->
            val isLast = i == data.lastIndex
            val barColor = if (isLast) VColors.Primary else VColors.Primary.copy(alpha = 0.35f)
            val frac = (d.value / max) * progress
            Column(
                Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                if (isLast) {
                    Text(
                        d.value.toInt().toString(),
                        style = VTypography.QuickStatValue.copy(color = VColors.Primary),
                    )
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(frac.coerceIn(0.001f, 1f))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(barColor),
                )
                Text(
                    d.label,
                    style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

/** Premium legend dot — color swatch + label + optional value. */
@Composable
fun VLegendDotPremium(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
    value: String? = null,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, style = VTypography.UpdateTime.copy(color = VColors.OnSurfaceVariant))
        if (value != null) {
            Text(
                value,
                style = VTypography.NavLabel.copy(color = VColors.OnSurface, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
            )
        }
    }
}
