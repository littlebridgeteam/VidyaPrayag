package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive shape tokens — lifted verbatim from HTML `:root` CSS variables.
 *
 * | HTML variable    | Kotlin constant | dp  |
 * |------------------|-----------------|-----|
 * | --shape-xs       | ShapeXs         | 4   |
 * | --shape-sm       | ShapeSm         | 8   |
 * | --shape-md       | ShapeMd         | 12  |
 * | --shape-lg       | ShapeLg         | 16  |
 * | --shape-xl       | ShapeXl         | 24  |
 * | --shape-2xl      | Shape2xl        | 28  |
 * | --shape-full     | ShapeFull       | 999 |
 */
object VShapes {
    val Xs = RoundedCornerShape(4.dp)
    val Sm = RoundedCornerShape(8.dp)
    val Md = RoundedCornerShape(12.dp)
    val Lg = RoundedCornerShape(16.dp)
    val Xl = RoundedCornerShape(24.dp)
    val TwoXl = RoundedCornerShape(28.dp)
    val Full = CircleShape

    // Convenience dp values for animateDpAsState targets
    val XsDp = 4.dp
    val SmDp = 8.dp
    val MdDp = 12.dp
    val LgDp = 16.dp
    val XlDp = 24.dp
    val TwoXlDp = 28.dp
    val FullDp = 999.dp

    // Phone frame
    val PhoneRadius = 56.dp
    val ScreenRadius = 44.dp
    val IslandRadius = 22.dp

    // Login hero bottom radius (0 0 2xl 2xl)
    val LoginHeroBottom = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
}
