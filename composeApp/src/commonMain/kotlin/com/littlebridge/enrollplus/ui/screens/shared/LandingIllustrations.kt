package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.littlebridge.enrollplus.ui.tokens.VColors

@Composable
fun ParentIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawParentScene()
    }
}

@Composable
fun SchoolIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawSchoolScene()
    }
}

private fun DrawScope.drawParentScene() {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    val coral = VColors.coral
    val coralBg = VColors.coralSoft
    val ink = VColors.ink
    val inkLight = VColors.ink3
    val cream = VColors.cream
    val white = VColors.white
    val success = VColors.success
    val gold = VColors.gold
    val violet = VColors.violet
    val line = VColors.line

    // Organic blob background
    val blob = Path().apply {
        moveTo(cx, cy - h * 0.35f)
        cubicTo(cx + w * 0.32f, cy - h * 0.35f, cx + w * 0.38f, cy + h * 0.05f, cx + w * 0.28f, cy + h * 0.22f)
        cubicTo(cx + w * 0.18f, cy + h * 0.38f, cx - w * 0.18f, cy + h * 0.38f, cx - w * 0.28f, cy + h * 0.22f)
        cubicTo(cx - w * 0.38f, cy + h * 0.05f, cx - w * 0.32f, cy - h * 0.35f, cx, cy - h * 0.35f)
        close()
    }
    drawPath(blob, coralBg.copy(alpha = 0.5f))

    // Open book - centered, slightly lower
    val bookW = w * 0.42f
    val bookH = h * 0.22f
    val bookX = cx - bookW / 2f
    val bookY = cy - bookH * 0.1f

    // Book left page
    val leftPage = Path().apply {
        moveTo(bookX, bookY)
        lineTo(cx, bookY + bookH * 0.08f)
        lineTo(cx, bookY + bookH)
        lineTo(bookX + bookW * 0.06f, bookY + bookH * 0.92f)
        close()
    }
    drawPath(leftPage, white)

    // Book right page
    val rightPage = Path().apply {
        moveTo(cx, bookY + bookH * 0.08f)
        lineTo(bookX + bookW, bookY)
        lineTo(bookX + bookW * 0.94f, bookY + bookH * 0.92f)
        lineTo(cx, bookY + bookH)
        close()
    }
    drawPath(rightPage, white)

    // Book spine shadow
    drawLine(
        color = line,
        start = Offset(cx, bookY + bookH * 0.08f),
        end = Offset(cx, bookY + bookH),
        strokeWidth = 1.5f,
    )

    // Book outline
    val bookOutline = Path().apply {
        moveTo(bookX, bookY)
        lineTo(cx, bookY + bookH * 0.08f)
        lineTo(bookX + bookW, bookY)
        lineTo(bookX + bookW * 0.94f, bookY + bookH * 0.92f)
        lineTo(cx, bookY + bookH)
        lineTo(bookX + bookW * 0.06f, bookY + bookH * 0.92f)
        close()
    }
    drawPath(bookOutline, ink.copy(alpha = 0.12f), style = Stroke(width = 2f))

    // Text lines on left page
    val lineYStart = bookY + bookH * 0.22f
    val lineSpacing = bookH * 0.13f
    repeat(4) { i ->
        val ly = lineYStart + i * lineSpacing
        val lineEnd = cx - bookW * (0.08f + i * 0.04f)
        drawLine(
            color = inkLight.copy(alpha = 0.25f),
            start = Offset(bookX + bookW * 0.1f, ly),
            end = Offset(lineEnd, ly),
            strokeWidth = 1.5f,
        )
    }

    // Text lines on right page
    repeat(4) { i ->
        val ly = lineYStart + i * lineSpacing
        val lineStart = cx + bookW * (0.08f + i * 0.04f)
        drawLine(
            color = inkLight.copy(alpha = 0.25f),
            start = Offset(lineStart, ly),
            end = Offset(bookX + bookW * 0.9f, ly),
            strokeWidth = 1.5f,
        )
    }

    // Plant sprouting from book center - stem
    val stemBaseX = cx
    val stemBaseY = bookY + bookH * 0.08f
    val stemTopY = bookY - h * 0.14f

    drawLine(
        color = success.copy(alpha = 0.7f),
        start = Offset(stemBaseX, stemBaseY),
        end = Offset(stemBaseX, stemTopY),
        strokeWidth = 2.5f,
    )

    // Left leaf
    val leftLeaf = Path().apply {
        moveTo(stemBaseX, stemTopY + h * 0.04f)
        cubicTo(
            stemBaseX - w * 0.1f, stemTopY + h * 0.02f,
            stemBaseX - w * 0.08f, stemTopY - h * 0.02f,
            stemBaseX, stemTopY + h * 0.01f,
        )
        close()
    }
    drawPath(leftLeaf, success.copy(alpha = 0.15f))
    drawPath(leftLeaf, success.copy(alpha = 0.5f), style = Stroke(width = 2f))

    // Right leaf
    val rightLeaf = Path().apply {
        moveTo(stemBaseX, stemTopY + h * 0.06f)
        cubicTo(
            stemBaseX + w * 0.1f, stemTopY + h * 0.04f,
            stemBaseX + w * 0.08f, stemTopY + h * 0.0f,
            stemBaseX, stemTopY + h * 0.03f,
        )
        close()
    }
    drawPath(rightLeaf, success.copy(alpha = 0.15f))
    drawPath(rightLeaf, success.copy(alpha = 0.5f), style = Stroke(width = 2f))

    // Small heart floating top-right
    val heartX = cx + w * 0.26f
    val heartY = cy - h * 0.22f
    val heartSize = w * 0.035f
    val heart = Path().apply {
        moveTo(heartX, heartY + heartSize * 0.3f)
        cubicTo(
            heartX - heartSize, heartY - heartSize * 0.5f,
            heartX - heartSize * 0.5f, heartY - heartSize,
            heartX, heartY - heartSize * 0.3f,
        )
        cubicTo(
            heartX + heartSize * 0.5f, heartY - heartSize,
            heartX + heartSize, heartY - heartSize * 0.5f,
            heartX, heartY + heartSize * 0.3f,
        )
        close()
    }
    drawPath(heart, coral.copy(alpha = 0.12f))
    drawPath(heart, coral.copy(alpha = 0.6f), style = Stroke(width = 2f))

    // Small star/sparkle top-left
    val starX = cx - w * 0.25f
    val starY = cy - h * 0.25f
    val starR = w * 0.012f
    drawCircle(
        color = gold.copy(alpha = 0.2f),
        radius = starR * 2.5f,
        center = Offset(starX, starY),
    )
    drawCircle(
        color = gold.copy(alpha = 0.6f),
        radius = starR,
        center = Offset(starX, starY),
    )

    // Small dot bottom-right
    drawCircle(
        color = violet.copy(alpha = 0.15f),
        radius = w * 0.01f,
        center = Offset(cx + w * 0.2f, cy + h * 0.25f),
    )
}

private fun DrawScope.drawSchoolScene() {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    val violet = VColors.violet
    val violetBg = VColors.violetSoft
    val ink = VColors.ink
    val inkLight = VColors.ink3
    val cream = VColors.cream
    val white = VColors.white
    val success = VColors.success
    val gold = VColors.gold
    val coral = VColors.coral
    val line = VColors.line

    // Organic blob background
    val blob = Path().apply {
        moveTo(cx, cy - h * 0.35f)
        cubicTo(cx + w * 0.34f, cy - h * 0.32f, cx + w * 0.38f, cy + h * 0.08f, cx + w * 0.26f, cy + h * 0.24f)
        cubicTo(cx + w * 0.16f, cy + h * 0.38f, cx - w * 0.2f, cy + h * 0.36f, cx - w * 0.3f, cy + h * 0.2f)
        cubicTo(cx - w * 0.38f, cy + h * 0.04f, cx - w * 0.3f, cy - h * 0.34f, cx, cy - h * 0.35f)
        close()
    }
    drawPath(blob, violetBg.copy(alpha = 0.5f))

    // School building - centered
    val bldgW = w * 0.36f
    val bldgH = h * 0.28f
    val bldgX = cx - bldgW / 2f
    val bldgY = cy - bldgH * 0.15f

    // Building body
    drawRoundRect(
        color = white,
        topLeft = Offset(bldgX, bldgY + bldgH * 0.2f),
        size = Size(bldgW, bldgH * 0.8f),
        cornerRadius = CornerRadius(6f),
    )

    // Building outline
    drawRoundRect(
        color = ink.copy(alpha = 0.1f),
        topLeft = Offset(bldgX, bldgY + bldgH * 0.2f),
        size = Size(bldgW, bldgH * 0.8f),
        cornerRadius = CornerRadius(6f),
        style = Stroke(width = 2f),
    )

    // Roof - triangle
    val roof = Path().apply {
        moveTo(bldgX - bldgW * 0.06f, bldgY + bldgH * 0.22f)
        lineTo(cx, bldgY - bldgH * 0.02f)
        lineTo(bldgX + bldgW * 1.06f, bldgY + bldgH * 0.22f)
        close()
    }
    drawPath(roof, violet.copy(alpha = 0.12f))
    drawPath(roof, violet.copy(alpha = 0.4f), style = Stroke(width = 2f))

    // Windows - 2x2 grid
    val winSize = bldgW * 0.12f
    val winGap = bldgW * 0.08f
    val winStartX = bldgX + (bldgW - winSize * 2 - winGap) / 2f
    val winStartY = bldgY + bldgH * 0.32f

    for (row in 0..1) {
        for (col in 0..1) {
            val wx = winStartX + col * (winSize + winGap)
            val wy = winStartY + row * (winSize + winGap)
            drawRoundRect(
                color = violet.copy(alpha = 0.08f),
                topLeft = Offset(wx, wy),
                size = Size(winSize, winSize),
                cornerRadius = CornerRadius(3f),
            )
            drawRoundRect(
                color = violet.copy(alpha = 0.25f),
                topLeft = Offset(wx, wy),
                size = Size(winSize, winSize),
                cornerRadius = CornerRadius(3f),
                style = Stroke(width = 1.5f),
            )
        }
    }

    // Door - center bottom
    val doorW = bldgW * 0.14f
    val doorH = bldgH * 0.22f
    drawRoundRect(
        color = violet.copy(alpha = 0.1f),
        topLeft = Offset(cx - doorW / 2f, bldgY + bldgH * 0.78f),
        size = Size(doorW, doorH),
        cornerRadius = CornerRadius(3f, 3f),
    )
    drawRoundRect(
        color = violet.copy(alpha = 0.3f),
        topLeft = Offset(cx - doorW / 2f, bldgY + bldgH * 0.78f),
        size = Size(doorW, doorH),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 1.5f),
    )

    // Flag on roof
    val flagPoleX = cx
    val flagPoleTopY = bldgY - h * 0.08f
    val flagPoleBottomY = bldgY + bldgH * 0.05f
    drawLine(
        color = ink.copy(alpha = 0.2f),
        start = Offset(flagPoleX, flagPoleTopY),
        end = Offset(flagPoleX, flagPoleBottomY),
        strokeWidth = 2f,
    )
    // Flag itself
    val flag = Path().apply {
        moveTo(flagPoleX, flagPoleTopY)
        lineTo(flagPoleX + w * 0.06f, flagPoleTopY + h * 0.02f)
        lineTo(flagPoleX, flagPoleTopY + h * 0.04f)
        close()
    }
    drawPath(flag, coral.copy(alpha = 0.5f))

    // Floating chart card - top right
    val cardW = w * 0.2f
    val cardH = h * 0.12f
    val cardX = cx + w * 0.18f
    val cardY = cy - h * 0.28f

    drawRoundRect(
        color = white,
        topLeft = Offset(cardX, cardY),
        size = Size(cardW, cardH),
        cornerRadius = CornerRadius(5f),
    )
    drawRoundRect(
        color = ink.copy(alpha = 0.08f),
        topLeft = Offset(cardX, cardY),
        size = Size(cardW, cardH),
        cornerRadius = CornerRadius(5f),
        style = Stroke(width = 1.5f),
    )

    // Mini bar chart inside card
    val miniBarX = cardX + cardW * 0.15f
    val miniBarY = cardY + cardH * 0.25f
    val miniBarW = cardW * 0.7f
    val miniBarH = cardH * 0.5f
    val miniBarCount = 3
    val miniBarGap = miniBarW / (miniBarCount * 2 - 1)
    val miniBarWidth = miniBarGap
    val miniBarHeights = floatArrayOf(0.5f, 0.75f, 0.9f)

    miniBarHeights.forEachIndexed { i, ratio ->
        val bh = miniBarH * ratio
        drawRoundRect(
            color = violet.copy(alpha = 0.3f + i * 0.15f),
            topLeft = Offset(miniBarX + i * miniBarGap * 2f, miniBarY + miniBarH - bh),
            size = Size(miniBarWidth, bh),
            cornerRadius = CornerRadius(1.5f),
        )
    }

    // Small checkmark circle - bottom left
    val checkX = cx - w * 0.22f
    val checkY = cy + h * 0.24f
    drawCircle(
        color = success.copy(alpha = 0.12f),
        radius = w * 0.04f,
        center = Offset(checkX, checkY),
    )
    drawCircle(
        color = success.copy(alpha = 0.5f),
        radius = w * 0.025f,
        center = Offset(checkX, checkY),
    )
    val checkPath = Path().apply {
        moveTo(checkX - w * 0.01f, checkY)
        lineTo(checkX - w * 0.003f, checkY + w * 0.008f)
        lineTo(checkX + w * 0.012f, checkY - w * 0.008f)
    }
    drawPath(checkPath, white, style = Stroke(width = 2f))

    // Small dot top-left
    drawCircle(
        color = gold.copy(alpha = 0.3f),
        radius = w * 0.012f,
        center = Offset(cx - w * 0.22f, cy - h * 0.2f),
    )
}
