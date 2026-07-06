package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private fun buildIcon(
    name: String,
    pathBuilder: ImageVector.Builder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply(pathBuilder).build()

private fun ImageVector.Builder.strokePath(
    pathData: String,
    width: Float = 2f,
) = path(
    fill = SolidColor(Color.Transparent),
    stroke = SolidColor(Color(0xFF1A1614)),
    strokeLineWidth = width,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round,
    pathFillType = PathFillType.EvenOdd,
) {
    // Parse simple SVG path data
    parseSvgPath(pathData)
}

// Simple SVG path parser — supports M, L, H, V, C, S, Q, T, A, Z commands
private fun androidx.compose.ui.graphics.vector.PathBuilder.parseSvgPath(d: String) {
    val tokens = d.replace(",", " ").split(Regex("(?=[A-Za-z])|(?<=[A-Za-z])")).filter { it.isNotBlank() }
    var i = 0
    while (i < tokens.size) {
        when (tokens[i].uppercase()) {
            "M" -> {
                i++
                val x = tokens[i].toFloat(); i++
                val y = tokens[i].toFloat(); i++
                moveTo(x, y)
            }
            "L" -> {
                i++
                val x = tokens[i].toFloat(); i++
                val y = tokens[i].toFloat(); i++
                lineTo(x, y)
            }
            "H" -> {
                i++
                val x = tokens[i].toFloat(); i++
                horizontalLineTo(x)
            }
            "V" -> {
                i++
                val y = tokens[i].toFloat(); i++
                verticalLineTo(y)
            }
            "C" -> {
                i++
                val x1 = tokens[i].toFloat(); i++
                val y1 = tokens[i].toFloat(); i++
                val x2 = tokens[i].toFloat(); i++
                val y2 = tokens[i].toFloat(); i++
                val x = tokens[i].toFloat(); i++
                val y = tokens[i].toFloat(); i++
                curveTo(x1, y1, x2, y2, x, y)
            }
            "S" -> {
                i++
                val x2 = tokens[i].toFloat(); i++
                val y2 = tokens[i].toFloat(); i++
                val x = tokens[i].toFloat(); i++
                val y = tokens[i].toFloat(); i++
                reflectiveCurveTo(x2, y2, x, y)
            }
            "Q" -> {
                i++
                val x1 = tokens[i].toFloat(); i++
                val y1 = tokens[i].toFloat(); i++
                val x = tokens[i].toFloat(); i++
                val y = tokens[i].toFloat(); i++
                quadTo(x1, y1, x, y)
            }
            "A" -> {
                i++
                val rx = tokens[i].toFloat(); i++
                val ry = tokens[i].toFloat(); i++
                i++ // xAxisRotation
                i++ // largeArcFlag
                i++ // sweepFlag
                val x = tokens[i].toFloat(); i++
                val y = tokens[i].toFloat(); i++
                arcTo(rx, ry, 0f, false, false, x, y)
            }
            "Z" -> {
                close()
                i++
            }
            else -> i++
        }
    }
}

val TIHome: ImageVector = buildIcon("TIHome") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(3f, 12f)
        lineTo(12f, 3f)
        lineTo(21f, 12f)
        moveTo(5f, 10f)
        verticalLineTo(20f)
        horizontalLineTo(9f)
        verticalLineTo(14f)
        horizontalLineTo(15f)
        verticalLineTo(20f)
        horizontalLineTo(19f)
        verticalLineTo(10f)
    }
}

val TIEdit: ImageVector = buildIcon("TIEdit") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(11f, 4f)
        horizontalLineTo(4f)
        arcTo(2f, 2f, 0f, false, false, 2f, 6f)
        verticalLineTo(20f)
        arcTo(2f, 2f, 0f, false, false, 4f, 22f)
        horizontalLineTo(18f)
        arcTo(2f, 2f, 0f, false, false, 20f, 20f)
        verticalLineTo(13f)
        moveTo(18.5f, 2.5f)
        arcTo(2.12f, 2.12f, 0f, false, true, 21.5f, 5.5f)
        lineTo(12f, 15f)
        lineTo(8f, 16f)
        lineTo(9f, 12f)
        lineTo(18.5f, 2.5f)
        close()
    }
}

val TIClasses: ImageVector = buildIcon("TIClasses") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(22f, 10f)
        verticalLineTo(16f)
        moveTo(2f, 10f)
        lineTo(12f, 5f)
        lineTo(22f, 10f)
        lineTo(12f, 15f)
        close()
        moveTo(6f, 12f)
        verticalLineTo(17f)
        arcTo(3f, 3f, 0f, false, false, 12f, 20f)
        arcTo(3f, 3f, 0f, false, false, 18f, 17f)
        verticalLineTo(12f)
    }
}

val TICalendar: ImageVector = buildIcon("TICalendar") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(3f, 4f)
        arcTo(2f, 2f, 0f, false, false, 1f, 6f)
        verticalLineTo(20f)
        arcTo(2f, 2f, 0f, false, false, 3f, 22f)
        horizontalLineTo(21f)
        arcTo(2f, 2f, 0f, false, false, 23f, 20f)
        verticalLineTo(6f)
        arcTo(2f, 2f, 0f, false, false, 21f, 4f)
        horizontalLineTo(3f)
        close()
        moveTo(16f, 2f)
        verticalLineTo(4f)
        moveTo(8f, 2f)
        verticalLineTo(4f)
        moveTo(3f, 10f)
        horizontalLineTo(21f)
    }
}

val TIUser: ImageVector = buildIcon("TIUser") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(20f, 21f)
        verticalLineTo(19f)
        arcTo(4f, 4f, 0f, false, false, 16f, 15f)
        horizontalLineTo(8f)
        arcTo(4f, 4f, 0f, false, false, 4f, 19f)
        verticalLineTo(21f)
        moveTo(12f, 11f)
        arcTo(4f, 4f, 0f, true, false, 12f, 3f)
        arcTo(4f, 4f, 0f, false, false, 12f, 11f)
        close()
    }
}

val TIBell: ImageVector = buildIcon("TIBell") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(18f, 8f)
        arcTo(6f, 6f, 0f, false, false, 6f, 8f)
        arcTo(6f, 6f, 0f, false, false, 6f, 8f)
        quadTo(6f, 15f, 3f, 17f)
        horizontalLineTo(21f)
        quadTo(18f, 15f, 18f, 8f)
        close()
        moveTo(13.73f, 21f)
        arcTo(2f, 2f, 0f, false, true, 10.27f, 21f)
    }
}

val TICheck: ImageVector = buildIcon("TICheck") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(5f, 13f)
        lineTo(9f, 17f)
        lineTo(19f, 7f)
    }
}

val TIBook: ImageVector = buildIcon("TIBook") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(4f, 19.5f)
        arcTo(2.5f, 2.5f, 0f, false, true, 6.5f, 17f)
        horizontalLineTo(20f)
        moveTo(6.5f, 2f)
        horizontalLineTo(20f)
        verticalLineTo(22f)
        horizontalLineTo(6.5f)
        arcTo(2.5f, 2.5f, 0f, false, true, 4f, 19.5f)
        verticalLineTo(4.5f)
        arcTo(2.5f, 2.5f, 0f, false, true, 6.5f, 2f)
        close()
    }
}

val TIAward: ImageVector = buildIcon("TIAward") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(12f, 2f)
        arcTo(6f, 6f, 0f, false, false, 6f, 8f)
        arcTo(6f, 6f, 0f, false, false, 18f, 8f)
        arcTo(6f, 6f, 0f, false, false, 12f, 2f)
        close()
        moveTo(15.477f, 12.89f)
        lineTo(17f, 22f)
        lineTo(12f, 19f)
        lineTo(7f, 22f)
        lineTo(8.523f, 12.89f)
    }
}

val TIMap: ImageVector = buildIcon("TIMap") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(9f, 18f)
        lineTo(3f, 21f)
        verticalLineTo(6f)
        lineTo(9f, 3f)
        lineTo(15f, 6f)
        lineTo(21f, 3f)
        verticalLineTo(18f)
        lineTo(15f, 21f)
        close()
        moveTo(9f, 3f)
        verticalLineTo(18f)
        moveTo(15f, 6f)
        verticalLineTo(21f)
    }
}

val TIClock: ImageVector = buildIcon("TIClock") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(12f, 2f)
        arcTo(10f, 10f, 0f, false, false, 2f, 12f)
        arcTo(10f, 10f, 0f, false, false, 22f, 12f)
        arcTo(10f, 10f, 0f, false, false, 12f, 2f)
        close()
        moveTo(12f, 6f)
        verticalLineTo(12f)
        lineTo(16f, 14f)
    }
}

val TILock: ImageVector = buildIcon("TILock") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(3f, 11f)
        arcTo(2f, 2f, 0f, false, false, 1f, 13f)
        verticalLineTo(20f)
        arcTo(2f, 2f, 0f, false, false, 3f, 22f)
        horizontalLineTo(21f)
        arcTo(2f, 2f, 0f, false, false, 23f, 20f)
        verticalLineTo(13f)
        arcTo(2f, 2f, 0f, false, false, 21f, 11f)
        horizontalLineTo(3f)
        close()
        moveTo(7f, 11f)
        verticalLineTo(7f)
        arcTo(5f, 5f, 0f, false, true, 17f, 7f)
        verticalLineTo(11f)
    }
}

val TIPalette: ImageVector = buildIcon("TIPalette") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(12f, 2f)
        arcTo(10f, 10f, 0f, false, false, 2f, 12f)
        arcTo(10f, 10f, 0f, false, false, 12f, 22f)
        arcTo(1.65f, 1.65f, 0f, false, false, 13.648f, 20.312f)
        arcTo(1.65f, 1.65f, 0f, false, false, 13.211f, 19.187f)
        arcTo(1.65f, 1.65f, 0f, false, false, 13.211f, 18.062f)
        arcTo(1.64f, 1.64f, 0f, false, true, 14.879f, 16.395f)
        horizontalLineTo(16.875f)
        arcTo(5.555f, 5.555f, 0f, false, false, 22f, 10.836f)
        arcTo(10f, 10f, 0f, false, false, 12f, 2f)
        close()
    }
}

val TILogout: ImageVector = buildIcon("TILogout") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(9f, 21f)
        horizontalLineTo(5f)
        arcTo(2f, 2f, 0f, false, true, 3f, 19f)
        verticalLineTo(5f)
        arcTo(2f, 2f, 0f, false, true, 5f, 3f)
        horizontalLineTo(9f)
        moveTo(16f, 17f)
        lineTo(21f, 12f)
        lineTo(16f, 7f)
        moveTo(21f, 12f)
        horizontalLineTo(9f)
    }
}

val TIAlert: ImageVector = buildIcon("TIAlert") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(12f, 2f)
        lineTo(1f, 22f)
        horizontalLineTo(23f)
        close()
        moveTo(12f, 9f)
        verticalLineTo(14f)
        moveTo(12f, 18f)
        verticalLineTo(18.5f)
    }
}

val TIChevronRight: ImageVector = buildIcon("TIChevronRight") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(9f, 18f)
        lineTo(15f, 12f)
        lineTo(9f, 6f)
    }
}

val TIArrowRight: ImageVector = buildIcon("TIArrowRight") {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(Color(0xFF1A1614)),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(5f, 12f)
        horizontalLineTo(19f)
        moveTo(13f, 6f)
        lineTo(19f, 12f)
        lineTo(13f, 18f)
    }
}
