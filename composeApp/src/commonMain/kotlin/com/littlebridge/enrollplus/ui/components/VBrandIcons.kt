package com.littlebridge.enrollplus.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GoogleIcon: ImageVector = ImageVector.Builder(
    name = "Google",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color(0xFF4285F4))) {
        moveTo(22.56f, 12.25f)
        curveToRelative(0f, -0.78f, -0.07f, -1.53f, -0.2f, -2.25f)
        horizontalLineTo(12f)
        verticalLineTo(14.26f)
        horizontalLineToRelative(5.92f)
        curveToRelative(-0.26f, 1.37f, -1.04f, 2.53f, -2.21f, 3.31f)
        verticalLineToRelative(2.77f)
        horizontalLineToRelative(3.57f)
        curveToRelative(2.08f, -1.92f, 3.28f, -4.74f, 3.28f, -8.09f)
        close()
    }
    path(fill = SolidColor(Color(0xFF34A853))) {
        moveTo(12f, 23f)
        curveToRelative(2.97f, 0f, 5.46f, -0.98f, 7.28f, -2.66f)
        lineToRelative(-3.57f, -2.77f)
        curveToRelative(-0.98f, 0.66f, -2.23f, 1.06f, -3.71f, 1.06f)
        curveToRelative(-2.86f, 0f, -5.29f, -1.93f, -6.16f, -4.53f)
        horizontalLineTo(2.18f)
        verticalLineToRelative(2.84f)
        curveTo(3.99f, 20.53f, 7.7f, 23f, 12f, 23f)
        close()
    }
    path(fill = SolidColor(Color(0xFFFBBC05))) {
        moveTo(5.84f, 14.09f)
        curveToRelative(-0.22f, -0.66f, -0.35f, -1.36f, -0.35f, -2.09f)
        reflectiveCurveToRelative(0.13f, -1.43f, 0.35f, -2.09f)
        verticalLineTo(7.07f)
        horizontalLineTo(2.18f)
        curveTo(1.43f, 8.55f, 1f, 10.22f, 1f, 12f)
        reflectiveCurveToRelative(0.43f, 3.45f, 1.18f, 4.93f)
        lineToRelative(2.85f, -2.22f)
        lineToRelative(0.81f, -0.62f)
        close()
    }
    path(fill = SolidColor(Color(0xFFEA4335))) {
        moveTo(12f, 5.38f)
        curveToRelative(1.62f, 0f, 3.06f, 0.56f, 4.21f, 1.64f)
        lineToRelative(3.15f, -3.15f)
        curveTo(17.45f, 2.09f, 14.97f, 1f, 12f, 1f)
        curveTo(7.7f, 1f, 3.99f, 3.47f, 2.18f, 7.07f)
        lineToRelative(3.66f, 2.84f)
        curveToRelative(0.87f, -2.6f, 3.3f, -4.53f, 6.16f, -4.53f)
        close()
    }
}.build()

val AppleIcon: ImageVector = ImageVector.Builder(
    name = "Apple",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color(0xFF000000))) {
        moveTo(17.05f, 12.04f)
        curveToRelative(-0.03f, -2.6f, 2.13f, -3.85f, 2.22f, -3.91f)
        curveToRelative(-1.21f, -1.77f, -3.09f, -2.01f, -3.76f, -2.04f)
        curveToRelative(-1.6f, -0.16f, -3.12f, 0.94f, -3.93f, 0.94f)
        curveToRelative(-0.81f, 0f, -2.06f, -0.92f, -3.39f, -0.89f)
        curveToRelative(-1.74f, 0.03f, -3.35f, 1.01f, -4.25f, 2.58f)
        curveToRelative(-1.81f, 3.14f, -0.46f, 7.8f, 1.31f, 10.36f)
        curveToRelative(0.86f, 1.25f, 1.89f, 2.66f, 3.23f, 2.61f)
        curveToRelative(1.29f, -0.05f, 1.78f, -0.83f, 3.34f, -0.83f)
        curveToRelative(1.56f, 0f, 2f, 0.83f, 3.37f, 0.81f)
        curveToRelative(1.39f, -0.03f, 2.27f, -1.29f, 3.12f, -2.55f)
        curveToRelative(0.98f, -1.45f, 1.38f, -2.86f, 1.4f, -2.93f)
        curveToRelative(-0.03f, -0.01f, -2.69f, -1.03f, -2.71f, -4.09f)
        close()
        moveTo(14.66f, 4.62f)
        curveToRelative(0.71f, -0.86f, 1.19f, -2.06f, 1.06f, -3.25f)
        curveToRelative(-1.02f, 0.04f, -2.26f, 0.68f, -2.99f, 1.54f)
        curveToRelative(-0.66f, 0.76f, -1.23f, 1.98f, -1.08f, 3.15f)
        curveToRelative(1.14f, 0.09f, 2.3f, -0.58f, 3.01f, -1.44f)
        close()
    }
}.build()
