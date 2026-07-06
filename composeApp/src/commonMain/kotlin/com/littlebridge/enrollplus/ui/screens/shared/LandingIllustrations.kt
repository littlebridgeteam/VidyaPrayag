package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion
import com.littlebridge.enrollplus.ui.tokens.VColors

/**
 * Notion-style minimal illustrations drawn with Canvas.
 * Clean geometric shapes, soft colors, no gradients, no excessive detail.
 */

@Composable
fun ParentIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawParentIllustration()
    }
}

@Composable
fun SchoolIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawSchoolIllustration()
    }
}

private fun DrawScope.drawParentIllustration() {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    // Soft background circle
    drawCircle(
        color = VColors.coral.copy(alpha = 0.06f),
        radius = w * 0.38f,
        center = Offset(cx, cy),
    )

    // Phone outline (rounded rect)
    val phoneW = w * 0.28f
    val phoneH = h * 0.52f
    val phoneX = cx - phoneW / 2f
    val phoneY = cy - phoneH / 2f
    val phoneRadius = 12f

    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.08f),
        topLeft = Offset(phoneX, phoneY),
        size = Size(phoneW, phoneH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(phoneRadius),
    )

    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.15f),
        topLeft = Offset(phoneX, phoneY),
        size = Size(phoneW, phoneH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(phoneRadius),
        style = Stroke(width = 2f),
    )

    // Screen area inside phone
    val screenPad = phoneW * 0.08f
    drawRoundRect(
        color = VColors.cream,
        topLeft = Offset(phoneX + screenPad, phoneY + screenPad * 2f),
        size = Size(phoneW - screenPad * 2f, phoneH - screenPad * 3f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f),
    )

    // Notification cards inside phone
    val cardX = phoneX + screenPad * 1.5f
    val cardW = phoneW - screenPad * 3f
    val cardH = phoneH * 0.08f
    val cardStartY = phoneY + screenPad * 3f
    val cardGap = phoneH * 0.04f

    // Card 1 - attendance (green dot)
    drawRoundRect(
        color = VColors.surfaceCard,
        topLeft = Offset(cardX, cardStartY),
        size = Size(cardW, cardH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
    )
    drawCircle(
        color = VColors.success,
        radius = 3f,
        center = Offset(cardX + 8f, cardStartY + cardH / 2f),
    )
    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.1f),
        topLeft = Offset(cardX + 16f, cardStartY + cardH * 0.3f),
        size = Size(cardW * 0.5f, cardH * 0.15f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
    )

    // Card 2 - fees (gold dot)
    drawRoundRect(
        color = VColors.surfaceCard,
        topLeft = Offset(cardX, cardStartY + cardH + cardGap),
        size = Size(cardW, cardH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
    )
    drawCircle(
        color = VColors.gold,
        radius = 3f,
        center = Offset(cardX + 8f, cardStartY + cardH + cardGap + cardH / 2f),
    )
    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.1f),
        topLeft = Offset(cardX + 16f, cardStartY + cardH + cardGap + cardH * 0.3f),
        size = Size(cardW * 0.6f, cardH * 0.15f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
    )

    // Card 3 - progress (violet dot)
    drawRoundRect(
        color = VColors.surfaceCard,
        topLeft = Offset(cardX, cardStartY + (cardH + cardGap) * 2f),
        size = Size(cardW, cardH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
    )
    drawCircle(
        color = VColors.violet,
        radius = 3f,
        center = Offset(cardX + 8f, cardStartY + (cardH + cardGap) * 2f + cardH / 2f),
    )
    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.1f),
        topLeft = Offset(cardX + 16f, cardStartY + (cardH + cardGap) * 2f + cardH * 0.3f),
        size = Size(cardW * 0.45f, cardH * 0.15f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
    )

    // Floating notification badge - top right
    val badgeX = phoneX + phoneW * 0.85f
    val badgeY = phoneY - phoneH * 0.02f
    drawCircle(
        color = VColors.coral,
        radius = w * 0.06f,
        center = Offset(badgeX, badgeY),
    )
    drawCircle(
        color = VColors.white,
        radius = w * 0.025f,
        center = Offset(badgeX, badgeY),
    )

    // Small heart icon - bottom left floating
    val heartX = phoneX - phoneW * 0.12f
    val heartY = phoneY + phoneH * 0.75f
    drawCircle(
        color = VColors.coral.copy(alpha = 0.08f),
        radius = w * 0.07f,
        center = Offset(heartX, heartY),
    )
    drawCircle(
        color = VColors.coral,
        radius = 3f,
        center = Offset(heartX, heartY),
    )

    // Dotted line connecting badge to phone
    drawLine(
        color = VColors.ink.copy(alpha = 0.08f),
        start = Offset(badgeX - w * 0.06f, badgeY),
        end = Offset(phoneX + phoneW, phoneY + phoneH * 0.1f),
        strokeWidth = 1.5f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)),
    )
}

private fun DrawScope.drawSchoolIllustration() {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    // Soft background circle
    drawCircle(
        color = VColors.violet.copy(alpha = 0.06f),
        radius = w * 0.38f,
        center = Offset(cx, cy),
    )

    // Dashboard panel (rounded rect)
    val panelW = w * 0.5f
    val panelH = h * 0.48f
    val panelX = cx - panelW / 2f
    val panelY = cy - panelH / 2f

    drawRoundRect(
        color = VColors.surfaceCard,
        topLeft = Offset(panelX, panelY),
        size = Size(panelW, panelH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
    )

    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.08f),
        topLeft = Offset(panelX, panelY),
        size = Size(panelW, panelH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
        style = Stroke(width = 2f),
    )

    // Header bar inside panel
    drawRoundRect(
        color = VColors.ink.copy(alpha = 0.05f),
        topLeft = Offset(panelX, panelY),
        size = Size(panelW, panelH * 0.12f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
    )

    // Header dots (menu dots)
    val dotY = panelY + panelH * 0.06f
    repeat(3) { i ->
        drawCircle(
            color = VColors.ink.copy(alpha = 0.2f),
            radius = 2f,
            center = Offset(panelX + 12f + i * 8f, dotY),
        )
    }

    // Bar chart inside panel
    val chartX = panelX + panelW * 0.12f
    val chartY = panelY + panelH * 0.25f
    val chartW = panelW * 0.76f
    val chartH = panelH * 0.4f
    val barCount = 4
    val barGap = chartW / (barCount * 2 - 1)
    val barW = barGap

    val barHeights = floatArrayOf(0.4f, 0.65f, 0.5f, 0.85f)
    val barColors = listOf(
        VColors.violet.copy(alpha = 0.3f),
        VColors.violet.copy(alpha = 0.5f),
        VColors.violet.copy(alpha = 0.4f),
        VColors.violet,
    )

    barHeights.forEachIndexed { i, heightRatio ->
        val bh = chartH * heightRatio
        drawRoundRect(
            color = barColors[i],
            topLeft = Offset(chartX + i * barGap * 2f, chartY + chartH - bh),
            size = Size(barW, bh),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
        )
    }

    // Baseline
    drawLine(
        color = VColors.ink.copy(alpha = 0.06f),
        start = Offset(chartX, chartY + chartH),
        end = Offset(chartX + chartW, chartY + chartH),
        strokeWidth = 1.5f,
    )

    // Stat row at bottom of panel
    val statY = panelY + panelH * 0.75f
    val statW = panelW * 0.25f
    val statGap = panelW * 0.08f
    val statStartX = panelX + panelW * 0.12f

    repeat(2) { i ->
        val sx = statStartX + i * (statW + statGap)
        drawRoundRect(
            color = VColors.ink.copy(alpha = 0.04f),
            topLeft = Offset(sx, statY),
            size = Size(statW, panelH * 0.12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
        )
        drawCircle(
            color = if (i == 0) VColors.success else VColors.gold,
            radius = 2.5f,
            center = Offset(sx + 6f, statY + panelH * 0.06f),
        )
        drawRoundRect(
            color = VColors.ink.copy(alpha = 0.08f),
            topLeft = Offset(sx + 12f, statY + panelH * 0.04f),
            size = Size(statW * 0.5f, panelH * 0.02f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
        )
    }

    // Floating gear icon - top right
    val gearX = panelX + panelW * 0.95f
    val gearY = panelY - panelH * 0.05f
    drawCircle(
        color = VColors.violet.copy(alpha = 0.08f),
        radius = w * 0.06f,
        center = Offset(gearX, gearY),
    )
    drawCircle(
        color = VColors.violet,
        radius = w * 0.03f,
        center = Offset(gearX, gearY),
        style = Stroke(width = 2f),
    )
    drawCircle(
        color = VColors.violet,
        radius = w * 0.01f,
        center = Offset(gearX, gearY),
    )

    // Small checkmark badge - bottom left
    val checkX = panelX - panelW * 0.08f
    val checkY = panelY + panelH * 0.85f
    drawCircle(
        color = VColors.success.copy(alpha = 0.1f),
        radius = w * 0.06f,
        center = Offset(checkX, checkY),
    )
    drawCircle(
        color = VColors.success,
        radius = w * 0.035f,
        center = Offset(checkX, checkY),
    )
    // Checkmark
    val checkPath = Path().apply {
        moveTo(checkX - w * 0.015f, checkY)
        lineTo(checkX - w * 0.005f, checkY + w * 0.01f)
        lineTo(checkX + w * 0.018f, checkY - w * 0.012f)
    }
    drawPath(
        path = checkPath,
        color = VColors.white,
        style = Stroke(width = 2.5f),
    )

    // Dotted line connecting gear to panel
    drawLine(
        color = VColors.ink.copy(alpha = 0.08f),
        start = Offset(gearX - w * 0.06f, gearY),
        end = Offset(panelX + panelW, panelY + panelH * 0.1f),
        strokeWidth = 1.5f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)),
    )
}
