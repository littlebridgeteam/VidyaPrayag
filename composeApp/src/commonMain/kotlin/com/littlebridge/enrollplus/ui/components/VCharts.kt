package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VBarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    barColor: Color = VColors.violet,
    height: androidx.compose.ui.unit.Dp = 160.dp,
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.second }.coerceAtLeast(1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
        ) {
            val barWidth = size.width / data.size * 0.6f
            val gap = size.width / data.size * 0.4f
            val chartHeight = size.height - 20f

            data.forEachIndexed { index, (label, value) ->
                val barHeight = (value / maxValue) * chartHeight
                val x = index * (barWidth + gap) + gap / 2f
                val y = chartHeight - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
    }
}

@Composable
fun VLineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = VColors.violet,
    height: androidx.compose.ui.unit.Dp = 120.dp,
) {
    if (data.size < 2) return

    val maxValue = data.max().coerceAtLeast(1f)
    val minValue = data.min().coerceAtLeast(0f)
    val range = (maxValue - minValue).coerceAtLeast(1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 16.dp),
    ) {
        val stepX = size.width / (data.size - 1)
        val chartHeight = size.height - 10f

        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = chartHeight - ((value - minValue) / range) * chartHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3f),
        )

        // Draw points
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = chartHeight - ((value - minValue) / range) * chartHeight
            drawCircle(
                color = lineColor,
                radius = 4f,
                center = Offset(x, y),
            )
        }
    }
}

@Composable
fun VDonutChart(
    segments: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 16.dp,
) {
    if (segments.isEmpty()) return

    val total = segments.sumOf { it.first.toDouble() }.toFloat().coerceAtLeast(1f)

    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = strokeWidth.toPx())
        val diameter = size.toPx() - strokeWidth.toPx()
        val topLeft = Offset(
            x = (size.toPx() - diameter) / 2f,
            y = (size.toPx() - diameter) / 2f,
        )
        val arcSize = Size(diameter, diameter)

        var startAngle = -90f
        segments.forEach { (value, color) ->
            val sweepAngle = (value / total) * 360f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
            startAngle += sweepAngle
        }
    }
}
