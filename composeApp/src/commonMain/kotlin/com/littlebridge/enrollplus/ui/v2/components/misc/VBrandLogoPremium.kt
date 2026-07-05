package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Premium brand logo — the Enroll+ "Setu" (bridge) mark on a glass plate.
 * Uses M3 Expressive tokens: glass-white plate, primary-colored mark.
 *
 * Two presentations:
 *  - [VBrandLogoPremium] — glass "cube" plate carrying the mark
 *  - [VBridgeMarkPremium] — bare stroked mark (no plate)
 */
@Composable
fun VBrandLogoPremium(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    cornerRadius: Dp = 28.dp,
    plateAlpha: Float = 0.16f,
    borderAlpha: Float = 0.18f,
) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(VColors.GlassWhite15)
            .border(1.dp, VColors.GlassWhite20, RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center,
    ) {
        VBridgeMarkPremium(Modifier.size(size * 0.625f))
    }
}

/**
 * Premium bridge mark — bare stroked mark (no plate).
 * Uses primary color for the arc and on-surface for the deck.
 */
@Composable
fun VBridgeMarkPremium(
    modifier: Modifier = Modifier,
    stroke: Color? = null,
    accent: Color? = null,
) {
    val s = stroke ?: VColors.OnPrimary
    val a = accent ?: VColors.Primary
    Canvas(modifier) { drawBridgePremium(stroke = s, accent = a) }
}

private fun DrawScope.drawBridgePremium(stroke: Color, accent: Color) {
    val s = size.width / 56f
    fun x(v: Float) = v * s
    fun y(v: Float) = v * s

    // arc  M12 32 Q28 12 44 32
    val arc = Path().apply {
        moveTo(x(12f), y(32f))
        quadraticTo(x(28f), y(12f), x(44f), y(32f))
    }
    drawPath(arc, color = accent, style = Stroke(width = 3f * s, cap = StrokeCap.Round))

    // deck  M10 40 H46
    drawLine(stroke, Offset(x(10f), y(40f)), Offset(x(46f), y(40f)), strokeWidth = 3.5f * s, cap = StrokeCap.Round)

    // cables 18 / 28 / 38
    val cable = stroke.copy(alpha = 0.78f)
    drawLine(cable, Offset(x(18f), y(32f)), Offset(x(18f), y(40f)), strokeWidth = 1.6f * s, cap = StrokeCap.Round)
    drawLine(cable, Offset(x(28f), y(22f)), Offset(x(28f), y(40f)), strokeWidth = 1.6f * s, cap = StrokeCap.Round)
    drawLine(cable, Offset(x(38f), y(32f)), Offset(x(38f), y(40f)), strokeWidth = 1.6f * s, cap = StrokeCap.Round)

    // pillar caps + accent centre node
    drawCircle(stroke, radius = 2.6f * s, center = Offset(x(12f), y(32f)))
    drawCircle(stroke, radius = 2.6f * s, center = Offset(x(44f), y(32f)))
    drawCircle(accent, radius = 2.4f * s, center = Offset(x(28f), y(22f)))
}

/**
 * Premium logo with wordmark — bridge mark + "Enroll+" text.
 * Uses M3 Expressive typography and tokens.
 */
@Composable
fun VLogoPremium(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    withWord: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        Box(
            Modifier
                .size(size)
                .clip(VShapes.Md)
                .background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            VBridgeMarkPremium(
                Modifier.size(size * 0.7f),
                stroke = VColors.OnPrimaryContainer,
                accent = VColors.Primary,
            )
        }
        if (withWord) {
            Text(
                text = buildAnnotatedString {
                    append("Enroll")
                    withStyle(SpanStyle(color = VColors.Primary)) { append("+") }
                },
                style = VTypography.BrandText.copy(color = VColors.OnSurface),
            )
        }
    }
}
