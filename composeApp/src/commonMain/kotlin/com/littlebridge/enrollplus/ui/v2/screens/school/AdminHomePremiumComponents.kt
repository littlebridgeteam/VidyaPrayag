package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminHomeAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.HomeAnalyticsBreakdown
import com.littlebridge.enrollplus.feature.admin.domain.model.HomeAnalyticsPoint
import kotlinx.coroutines.delay

internal object AdminHomeTokens {
    val Cream = Color(0xFFFBF8F4)
    val Card = Color.White
    val Ink = Color(0xFF1A1614)
    val Ink2 = Color(0xFF5C544E)
    val Ink3 = Color(0xFF8A8078)
    val Line = Color(0xFFE8E0D6)
    val LineSoft = Color(0xFFF0EAE0)
    val Violet = Color(0xFF5B41D5)
    val VioletDark = Color(0xFF3A26A8)
    val VioletInk = Color(0xFF16006E)
    val VioletSoft = Color(0xFFEEE8FB)
    val Mint = Color(0xFF2DCE89)
    val MintSoft = Color(0xFFDCF5E8)
    val Gold = Color(0xFFFCB400)
    val GoldSoft = Color(0xFFFFF4D1)
    val Coral = Color(0xFFF82B60)
    val CoralSoft = Color(0xFFFFE4EC)
    val Sky = Color(0xFF18BFFF)
    val SkySoft = Color(0xFFE0F6FF)
    val Success = Color(0xFF2D7A4A)
    val SurfaceTint = Color(0xFFF8F4EF)

    val Sm = RoundedCornerShape(6.dp)
    val Md = RoundedCornerShape(10.dp)
    val Lg = RoundedCornerShape(14.dp)
    val Xl = RoundedCornerShape(20.dp)
    val Xxl = RoundedCornerShape(28.dp)
    val Full = RoundedCornerShape(999.dp)
}

@Composable
internal fun AdminSectionHeader(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, color = AdminHomeTokens.Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        if (!action.isNullOrBlank()) {
            Text(
                action,
                color = AdminHomeTokens.Violet,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    enabled = onAction != null,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onAction?.invoke() },
            )
        }
    }
}

@Composable
internal fun AdminPremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Int = 16,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val lift by animateFloatAsState(if (pressed) -2f else 0f, tween(150), label = "adminCardLift")
    val clickable = if (onClick == null) modifier else modifier.clickable(
        interactionSource = interaction,
        indication = null,
        onClick = onClick,
    )
    Column(
        modifier = clickable
            .graphicsLayer { translationY = lift }
            .clip(AdminHomeTokens.Lg)
            .background(AdminHomeTokens.Card)
            .border(1.dp, AdminHomeTokens.Line, AdminHomeTokens.Lg)
            .padding(padding.dp),
    ) { content() }
}

@Composable
internal fun AdminLinearProgress(value: Double, color: Color, modifier: Modifier = Modifier, height: Int = 5) {
    Box(modifier.clip(AdminHomeTokens.Full).background(AdminHomeTokens.LineSoft).height(height.dp)) {
        Box(
            Modifier.fillMaxHeight().fillMaxWidth(value.coerceIn(0.0, 100.0).toFloat() / 100f)
                .clip(AdminHomeTokens.Full).background(color),
        )
    }
}

@Composable
internal fun AdminRing(value: Double, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.5.dp.toPx()
        drawArc(AdminHomeTokens.LineSoft, -90f, 360f, false, style = Stroke(stroke))
        drawArc(color, -90f, (value.coerceIn(0.0, 100.0) * 3.6).toFloat(), false, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
internal fun AdminSparkline(points: List<Double>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        if (points.isEmpty()) return@Canvas
        val max = points.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
        val min = points.minOrNull() ?: 0.0
        val range = (max - min).takeIf { it > 0.0 } ?: 1.0
        val line = Path()
        val fill = Path()
        points.forEachIndexed { index, value ->
            val x = if (points.size == 1) size.width else size.width * index / (points.size - 1)
            val y = size.height - ((value - min) / range).toFloat() * (size.height * .8f) - size.height * .1f
            if (index == 0) { line.moveTo(x, y); fill.moveTo(x, size.height); fill.lineTo(x, y) }
            else { line.lineTo(x, y); fill.lineTo(x, y) }
        }
        fill.lineTo(size.width, size.height)
        fill.close()
        drawPath(fill, Brush.verticalGradient(listOf(color.copy(alpha = .24f), Color.Transparent)))
        drawPath(line, color, style = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
internal fun AdminBarChart(
    points: List<HomeAnalyticsPoint>,
    color: Color,
    animate: Boolean,
    modifier: Modifier = Modifier,
) {
    val max = points.maxOfOrNull { it.value }?.takeIf { it > 0.0 } ?: 1.0
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Bottom) {
        points.forEachIndexed { index, point ->
            var target = if (animate) (point.value / max).toFloat() else 0f
            val fraction by animateFloatAsState(target.coerceIn(0f, 1f), tween(650 + index * 45), label = "bar$index")
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(58.dp), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        Modifier.fillMaxWidth().fillMaxHeight(fraction.coerceAtLeast(if (point.value > 0) .04f else 0f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (index == points.lastIndex) color else color.copy(alpha = .18f)),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(point.label, fontSize = 8.sp, color = AdminHomeTokens.Ink3, maxLines = 1)
            }
        }
    }
}

@Composable
internal fun AdminDonut(
    items: List<HomeAnalyticsBreakdown>,
    center: String,
    unit: String,
    modifier: Modifier = Modifier,
) {
    val colors = listOf(AdminHomeTokens.Mint, AdminHomeTokens.Gold, AdminHomeTokens.Coral, AdminHomeTokens.Sky)
    val total = items.sumOf { it.value }.takeIf { it > 0.0 } ?: 1.0
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val inset = stroke / 2
            val arcRect = Rect(Offset(inset, inset), Size(size.width - stroke, size.height - stroke))
            drawArc(AdminHomeTokens.LineSoft, -90f, 360f, false, topLeft = arcRect.topLeft, size = arcRect.size, style = Stroke(stroke))
            var start = -90f
            items.forEachIndexed { index, item ->
                val sweep = (item.value / total * 360.0).toFloat()
                if (sweep > 0f) drawArc(colors[index % colors.size], start, sweep, false, topLeft = arcRect.topLeft, size = arcRect.size, style = Stroke(stroke, cap = StrokeCap.Butt))
                start += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(center, color = AdminHomeTokens.Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(unit, color = AdminHomeTokens.Ink3, fontSize = 8.sp)
        }
    }
}

@Composable
private fun AnalyticsStatCard(stat: com.littlebridge.enrollplus.feature.admin.domain.model.HomeAnalyticsStat, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    AdminPremiumCard(modifier = modifier, padding = 12) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Box(Modifier.size(24.dp).clip(AdminHomeTokens.Sm).background(color), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
            Text(stat.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminHomeTokens.Ink2, maxLines = 1)
        }
        Spacer(Modifier.height(8.dp))
        Text(stat.value, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = AdminHomeTokens.Ink, maxLines = 1)
        Text(
            stat.supportingText,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (stat.direction == "down") AdminHomeTokens.Coral else AdminHomeTokens.Success,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun AdminAnalyticsOverlay(
    visible: Boolean,
    data: AdminHomeAnalytics?,
    loading: Boolean,
    error: String?,
    onSelectFilter: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    Popup(alignment = Alignment.BottomCenter, properties = PopupProperties(focusable = true), onDismissRequest = onDismiss) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .40f)).clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onDismiss,
        )) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(tween(350)) { it } + fadeIn(tween(300)),
                exit = slideOutVertically(tween(350)) { it } + fadeOut(tween(300)),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Column(
                    Modifier.fillMaxWidth().fillMaxHeight(.92f)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(AdminHomeTokens.Cream)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { }
                        .padding(horizontal = 20.dp),
                ) {
                    Box(Modifier.padding(top = 10.dp).width(40.dp).height(5.dp).clip(AdminHomeTokens.Full).background(AdminHomeTokens.Line).align(Alignment.CenterHorizontally))
                    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(data?.title.orEmpty(), Modifier.weight(1f), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AdminHomeTokens.Ink)
                        Box(
                            Modifier.size(32.dp).clip(CircleShape).background(Color.White).border(1.dp, AdminHomeTokens.Line, CircleShape)
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onDismiss),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Filled.Close, "Close", tint = AdminHomeTokens.Ink2, modifier = Modifier.size(16.dp)) }
                    }
                    if (data != null) {
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            data.filters.forEach { filter ->
                                val active = filter.id == data.selectedFilter
                                Text(
                                    filter.label,
                                    color = if (active) Color.White else AdminHomeTokens.Ink2,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clip(AdminHomeTokens.Full)
                                        .background(if (active) AdminHomeTokens.Violet else Color.White)
                                        .border(1.dp, if (active) AdminHomeTokens.Violet else AdminHomeTokens.Line, AdminHomeTokens.Full)
                                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSelectFilter(filter.id) }
                                        .padding(horizontal = 13.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }
                    when {
                        loading && data == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Loading analytics…", color = AdminHomeTokens.Ink3) }
                        error != null && data == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error, color = AdminHomeTokens.Coral) }
                        data != null -> AnalyticsDashboardBody(data, loading)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsDashboardBody(data: AdminHomeAnalytics, loading: Boolean) {
    var animate by remember(data.type, data.selectedFilter) { mutableStateOf(false) }
    LaunchedEffect(data.type, data.selectedFilter) { delay(80); animate = true }
    val palette = when (data.type) {
        "attendance" -> AdminHomeTokens.Mint
        "admissions" -> AdminHomeTokens.Sky
        "staff" -> AdminHomeTokens.Gold
        else -> AdminHomeTokens.Violet
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            data.stats.take(2).forEachIndexed { index, stat ->
                AnalyticsStatCard(stat, if (index == 0) palette else AdminHomeTokens.Coral, if (index == 0) Icons.Filled.TrendingUp else Icons.Filled.WarningAmber, Modifier.weight(1f))
            }
        }
        AdminPremiumCard(padding = 14) {
            Text(data.distributionTitle.uppercase(), color = AdminHomeTokens.Ink3, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AdminDonut(data.distribution, data.distributionCenter, data.distributionUnit, Modifier.size(70.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    val colors = listOf(AdminHomeTokens.Mint, AdminHomeTokens.Gold, AdminHomeTokens.Coral, AdminHomeTokens.Sky)
                    data.distribution.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(AdminHomeTokens.Sm).background(colors[index % colors.size]))
                            Text(item.label, Modifier.padding(start = 7.dp).weight(1f), fontSize = 10.sp, color = AdminHomeTokens.Ink2)
                            Text(item.displayValue, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AdminHomeTokens.Ink)
                        }
                    }
                }
            }
        }
        AdminPremiumCard(padding = 14) {
            Text(data.trendTitle.uppercase(), color = AdminHomeTokens.Ink3, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            AdminBarChart(data.trend, palette, animate && !loading, Modifier.fillMaxWidth().height(80.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminPremiumCard(Modifier.weight(1f), padding = 12) {
                Text("Trend", fontSize = 10.sp, color = AdminHomeTokens.Ink2, fontWeight = FontWeight.SemiBold)
                AdminSparkline(data.trend.map { it.value }, palette, Modifier.fillMaxWidth().height(50.dp))
            }
            AdminPremiumCard(Modifier.weight(1f), padding = 12) {
                Text(data.filterLabel, fontSize = 10.sp, color = AdminHomeTokens.Ink2, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(7.dp))
                Text(data.filters.firstOrNull { it.id == data.selectedFilter }?.label.orEmpty(), color = AdminHomeTokens.Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            }
        }
        AdminPremiumCard(padding = 14) {
            Text(data.breakdownTitle.uppercase(), color = AdminHomeTokens.Ink3, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            data.breakdown.forEachIndexed { index, item ->
                if (index > 0) Spacer(Modifier.height(11.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AdminHomeTokens.Ink)
                    Text(item.displayValue, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette)
                }
                Spacer(Modifier.height(5.dp))
                val max = data.breakdown.maxOfOrNull { it.value }?.takeIf { it > 0.0 } ?: 100.0
                AdminLinearProgress(item.value / max * 100.0, palette, Modifier.fillMaxWidth(), 5)
            }
        }
    }
}
