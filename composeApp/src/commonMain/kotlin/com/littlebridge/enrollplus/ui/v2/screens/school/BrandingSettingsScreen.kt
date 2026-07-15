package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.branding.domain.model.SchoolBranding
import com.littlebridge.enrollplus.feature.branding.domain.model.UpdateBrandingRequest
import com.littlebridge.enrollplus.feature.branding.presentation.BrandingThemeManager
import com.littlebridge.enrollplus.feature.branding.presentation.BrandingViewModel
import com.littlebridge.enrollplus.platform.rememberMediaPicker
import com.littlebridge.enrollplus.ui.v2.components.*
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

internal val BRANDING_PRESET_COLORS = listOf(
    "#2563EB", "#7C3AED", "#059669", "#DC2626",
    "#EA580C", "#D97706", "#0891B2", "#4F46E5",
    "#0D9488", "#BE185D", "#1E40AF", "#9333EA",
    "#16A34A", "#E11D48", "#F59E0B", "#0EA5E9",
)

private val SUBDOMAIN_REGEX = Regex("^[a-z0-9][a-z0-9-]{2,30}[a-z0-9]$")

internal fun parseBrandingHexColor(hex: String): Color {
    return try {
        val value = hex.removePrefix("#")
        if (value.length == 3) {
            val r = value[0].toString().repeat(2).toInt(16)
            val g = value[1].toString().repeat(2).toInt(16)
            val b = value[2].toString().repeat(2).toInt(16)
            Color(r, g, b)
        } else {
            val rgb = value.toLong(16)
            Color((rgb shr 16 and 0xFF).toInt(), (rgb shr 8 and 0xFF).toInt(), (rgb and 0xFF).toInt())
        }
    } catch (_: Exception) {
        Color(0xFF2563EB)
    }
}

@Composable
fun BrandingSettingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BrandingViewModel = koinViewModel(),
    brandingThemeManager: BrandingThemeManager = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) {
        viewModel.loadBranding()
    }

    // When branding is updated or reset, refresh the global theme manager
    // so the app's accent colors update live. Skips initial load (handled by
    // NavGraphV2's LaunchedEffect(isAuthenticated)).
    var hasInitialLoad = remember { mutableStateOf(false) }
    LaunchedEffect(state.branding) {
        if (state.branding != null) {
            if (hasInitialLoad.value) {
                brandingThemeManager.loadBranding()
            } else {
                hasInitialLoad.value = true
            }
        }
    }

    LaunchedEffect(state.infoMessage) {
        if (state.infoMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearInfoMessage()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            kotlinx.coroutines.delay(5000)
            viewModel.clearError()
        }
    }

    BrandingSettingsContent(
        state = state,
        onBack = onBack,
        onUpdateBranding = { req -> viewModel.updateBranding(req) },
        onResetBranding = { viewModel.resetBranding() },
        onCheckSubdomain = { sub -> viewModel.checkSubdomain(sub) },
        onAssignSubdomain = { sub -> viewModel.updateSubdomain(sub) },
        onRemoveSubdomain = { viewModel.removeSubdomain() },
        onSubdomainInputChanged = { viewModel.clearSubdomainCheck() },
        onUploadAsset = { field, bytes, fileName, mimeType -> viewModel.uploadAsset(field, bytes, fileName, mimeType) },
        onDeleteAsset = { field -> viewModel.deleteAsset(field) },
        onRetry = { viewModel.loadBranding() },
        modifier = modifier,
    )
}

private fun isSubdomainValid(input: String): Boolean {
    return input.length >= 4 && input.length <= 32 && SUBDOMAIN_REGEX.matches(input)
}

@Composable
private fun BrandingSettingsContent(
    state: com.littlebridge.enrollplus.feature.branding.presentation.BrandingState,
    onBack: () -> Unit,
    onUpdateBranding: (UpdateBrandingRequest) -> Unit,
    onResetBranding: () -> Unit,
    onCheckSubdomain: (String) -> Unit,
    onAssignSubdomain: (String) -> Unit,
    onRemoveSubdomain: () -> Unit,
    onSubdomainInputChanged: () -> Unit,
    onUploadAsset: (String, ByteArray, String, String) -> Unit,
    onDeleteAsset: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
        val branding = state.branding

    var primaryColor by remember(branding?.primaryColor) {
        mutableStateOf(branding?.primaryColor ?: "#2563EB")
    }
    var secondaryColor by remember(branding?.secondaryColor) {
        mutableStateOf(branding?.secondaryColor ?: "#1E40AF")
    }
    var accentColor by remember(branding?.accentColor) {
        mutableStateOf(branding?.accentColor ?: "#3B82F6")
    }
    var subdomainInput by remember(branding?.customSubdomain) {
        mutableStateOf(branding?.customSubdomain ?: "")
    }
    var showResetConfirm by remember { mutableStateOf(false) }

    VConfirmDialog(
        visible = showResetConfirm,
        title = appString(StringKeys.BRAND_RESET_TITLE),
        message = appString(StringKeys.BRAND_RESET_MSG),
        confirmLabel = appString(StringKeys.BRAND_RESET_BTN),
        onConfirm = {
            showResetConfirm = false
            onResetBranding()
        },
        onDismiss = { showResetConfirm = false },
        icon = VIcons.AlertTriangle,
    )

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.ink.copy(alpha = 0.06f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.ArrowLeft, contentDescription = "Back", tint = VColors.ink, modifier = Modifier.size(20.dp))
            }
            Text(appString(StringKeys.BRAND_TITLE), style = VTypography.h2.copy(color = VColors.ink))
        }

        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = branding == null && !state.isLoading,
            onRetry = onRetry,
            skeleton = { SkeletonProfile() },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // ── Info / Error banner ────────────────────────────────────
                state.infoMessage?.let { msg ->
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(VIcons.Check, contentDescription = null, tint = VColors.success, modifier = Modifier.size(18.dp))
                            Text(msg, style = VTypography.body.copy(color = VColors.ink))
                        }
                    }
                }
                state.error?.let { err ->
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(VIcons.AlertTriangle, contentDescription = null, tint = VColors.error, modifier = Modifier.size(18.dp))
                            Text(err, style = VTypography.body.copy(color = VColors.error))
                        }
                    }
                }

                // ── Branding status badge ──────────────────────────────────
                branding?.let { b ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            b.schoolName,
                            style = VTypography.h3.copy(color = VColors.ink),
                        )
                        VBadge(
                            text = if (b.isCustomized) appString(StringKeys.BRAND_CUSTOMIZED) else appString(StringKeys.BRAND_DEFAULT),
                            tone = if (b.isCustomized) VBadgeTone.Success else VBadgeTone.Neutral,
                        )
                    }
                }

                // ── Live Preview Card ──────────────────────────────────────
                BrandingPreviewCard(
                    branding = branding,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    accentColor = accentColor,
                )

                // ── Color Pickers ──────────────────────────────────────────
                Text(appString(StringKeys.BRAND_COLORS), style = VTypography.h3.copy(color = VColors.ink))
                BrandingColorPickerSection(
                    label = appString(StringKeys.BRAND_PRIMARY_COLOR),
                    currentColor = primaryColor,
                    onColorSelected = { primaryColor = it },
                )
                BrandingColorPickerSection(
                    label = appString(StringKeys.BRAND_SECONDARY_COLOR),
                    currentColor = secondaryColor,
                    onColorSelected = { secondaryColor = it },
                )
                BrandingColorPickerSection(
                    label = appString(StringKeys.BRAND_ACCENT_COLOR),
                    currentColor = accentColor,
                    onColorSelected = { accentColor = it },
                )

                // ── Save Colors Button ─────────────────────────────────────
                VButton(
                    text = appString(StringKeys.BRAND_SAVE_COLORS),
                    onClick = {
                        onUpdateBranding(
                            UpdateBrandingRequest(
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                accentColor = accentColor,
                                isCustomized = true,
                            )
                        )
                    },
                    full = true,
                    loading = state.isLoading,
                )

                // ── Brand Assets Section ───────────────────────────────────
                Spacer(Modifier.height(8.dp))
                Text(appString(StringKeys.BRAND_ASSETS), style = VTypography.h3.copy(color = VColors.ink))
                Text(
                    appString(StringKeys.BRAND_ASSETS_DESC),
                    style = VTypography.caption.copy(color = VColors.ink3),
                )
                AssetUploadRow(
                    label = appString(StringKeys.BRAND_LOGO),
                    field = "logo",
                    url = branding?.logoUrl,
                    isUploading = state.uploadingField == "logo",
                    onUpload = onUploadAsset,
                    onDelete = onDeleteAsset,
                )
                AssetUploadRow(
                    label = appString(StringKeys.BRAND_DARK_LOGO),
                    field = "logo_dark",
                    url = branding?.logoDarkUrl,
                    isUploading = state.uploadingField == "logo_dark",
                    onUpload = onUploadAsset,
                    onDelete = onDeleteAsset,
                )
                AssetUploadRow(
                    label = appString(StringKeys.BRAND_FAVICON),
                    field = "favicon",
                    url = branding?.faviconUrl,
                    isUploading = state.uploadingField == "favicon",
                    onUpload = onUploadAsset,
                    onDelete = onDeleteAsset,
                )
                AssetUploadRow(
                    label = appString(StringKeys.BRAND_APP_ICON),
                    field = "app_icon",
                    url = branding?.appIconUrl,
                    isUploading = state.uploadingField == "app_icon",
                    onUpload = onUploadAsset,
                    onDelete = onDeleteAsset,
                )
                AssetUploadRow(
                    label = appString(StringKeys.BRAND_SPLASH),
                    field = "splash_screen",
                    url = branding?.splashScreenUrl,
                    isUploading = state.uploadingField == "splash_screen",
                    onUpload = onUploadAsset,
                    onDelete = onDeleteAsset,
                )
                AssetUploadRow(
                    label = appString(StringKeys.BRAND_LOGIN_BG),
                    field = "login_background",
                    url = branding?.loginBackgroundUrl,
                    isUploading = state.uploadingField == "login_background",
                    onUpload = onUploadAsset,
                    onDelete = onDeleteAsset,
                )

                // ── Subdomain Section ──────────────────────────────────────
                Spacer(Modifier.height(8.dp))
                Text(appString(StringKeys.BRAND_SUBDOMAIN), style = VTypography.h3.copy(color = VColors.ink))
                Text(
                    appString(StringKeys.BRAND_SUBDOMAIN_DESC),
                    style = VTypography.caption.copy(color = VColors.ink3),
                )

                branding?.customSubdomain?.let { existing ->
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(appString(StringKeys.BRAND_CURRENT_SUBDOMAIN), style = VTypography.caption.copy(color = VColors.ink3))
                                Text(
                                    "$existing.vidyaprayag.com",
                                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink),
                                )
                            }
                            VButton(
                                text = appString(StringKeys.BRAND_REMOVE),
                                onClick = { onRemoveSubdomain() },
                                variant = VButtonVariant.Ghost,
                                tone = VButtonTone.Rose,
                                size = VButtonSize.Sm,
                            )
                        }
                    }
                }

                if (branding?.customSubdomain == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        VInput(
                            value = subdomainInput,
                            onValueChange = {
                                subdomainInput = it.lowercase().filter { ch -> ch.isLetterOrDigit() || ch == '-' }
                                onSubdomainInputChanged()
                            },
                            modifier = Modifier.weight(1f),
                            label = appString(StringKeys.BRAND_SUBDOMAIN_LABEL),
                            placeholder = appString(StringKeys.BRAND_SUBDOMAIN_PLACE),
                            hint = appString(StringKeys.BRAND_SUBDOMAIN_HINT),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        VButton(
                            text = appString(StringKeys.BRAND_CHECK),
                            onClick = { onCheckSubdomain(subdomainInput) },
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Sm,
                            enabled = isSubdomainValid(subdomainInput),
                            loading = state.isLoading,
                        )
                        VButton(
                            text = appString(StringKeys.BRAND_ASSIGN),
                            onClick = { onAssignSubdomain(subdomainInput) },
                            variant = VButtonVariant.Primary,
                            size = VButtonSize.Sm,
                            enabled = isSubdomainValid(subdomainInput),
                            loading = state.isLoading,
                        )
                    }

                    state.subdomainAvailable?.let { available ->
                        VCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    if (available) VIcons.Check else VIcons.Close,
                                    contentDescription = null,
                                    tint = if (available) VColors.success else VColors.error,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    if (available) appString(StringKeys.BRAND_SUBDOMAIN_AVAIL) else appString(StringKeys.BRAND_SUBDOMAIN_TAKEN),
                                    style = VTypography.body.copy(color = if (available) VColors.success else VColors.error),
                                )
                            }
                        }
                    }
                }

                // ── Reset to defaults ──────────────────────────────────────
                Spacer(Modifier.height(16.dp))
                VButton(
                    text = appString(StringKeys.BRAND_RESET_DEFAULTS),
                    onClick = { showResetConfirm = true },
                    variant = VButtonVariant.Ghost,
                    tone = VButtonTone.Rose,
                    full = true,
                )
            }
        }
    }
}

@Composable
internal fun BrandingPreviewCard(
    branding: SchoolBranding?,
    primaryColor: String,
    secondaryColor: String,
    accentColor: String,
) {
        val primary = parseBrandingHexColor(primaryColor)
    val secondary = parseBrandingHexColor(secondaryColor)
    val accent = parseBrandingHexColor(accentColor)

    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(appString(StringKeys.BRAND_LIVE_PREVIEW), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))

            // Mock login header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    branding?.schoolName ?: appString(StringKeys.BRAND_YOUR_SCHOOL),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Mock button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(appString(StringKeys.BRAND_PRIMARY_BTN), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(secondary.copy(alpha = 0.15f))
                        .border(1.dp, secondary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(appString(StringKeys.BRAND_SECONDARY_BTN), color = secondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Color swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BrandingColorSwatch(appString(StringKeys.BRAND_SWATCH_PRIMARY), primary)
                BrandingColorSwatch(appString(StringKeys.BRAND_SWATCH_SECONDARY), secondary)
                BrandingColorSwatch(appString(StringKeys.BRAND_SWATCH_ACCENT), accent)
            }
        }
    }
}

@Composable
internal fun BrandingColorSwatch(label: String, color: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(0.33f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, Color.White, CircleShape),
        )
        Text(label, style = VTypography.caption.copy(color = VColors.ink3), fontSize = 10.sp)
    }
}

@Composable
internal fun BrandingColorPickerSection(
    label: String,
    currentColor: String,
    onColorSelected: (String) -> Unit,
) {
        var hexInput by remember(currentColor) { mutableStateOf(currentColor) }
    val parsedColor = parseBrandingHexColor(hexInput)
    var showSlider by remember { mutableStateOf(false) }

    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(parsedColor)
                        .border(2.dp, VColors.line, CircleShape)
                        .clickable { showSlider = !showSlider },
                )
            }

            // Expanded preset swatches — 2 rows of 8
            val chunked = BRANDING_PRESET_COLORS.chunked(8)
            chunked.forEach { rowColors ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowColors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(parseBrandingHexColor(hex))
                                .border(
                                    width = if (hex.equals(hexInput, ignoreCase = true)) 3.dp else 0.dp,
                                    color = VColors.ink,
                                    shape = CircleShape,
                                )
                                .clickable {
                                    hexInput = hex
                                    onColorSelected(hex)
                                    showSlider = false
                                },
                        )
                    }
                }
            }

            // Hue slider — tap the color circle to toggle
            if (showSlider) {
                HueSliderBar(
                    currentHex = hexInput,
                    onColorSelected = { hex ->
                        hexInput = hex
                        onColorSelected(hex)
                    },
                )
            }

            // Hex input
            VInput(
                value = hexInput,
                onValueChange = { value ->
                    val withoutHash = value.filter { it.isLetterOrDigit() }
                    hexInput = "#$withoutHash"
                    if (hexInput.length == 7 && hexInput.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                        onColorSelected(hexInput)
                    }
                },
                label = appString(StringKeys.BRAND_HEX_COLOR),
                placeholder = "#2563EB",
                singleLine = true,
            )
        }
    }
}

@Composable
private fun HueSliderBar(
    currentHex: String,
    onColorSelected: (String) -> Unit,
) {
    val hue = remember(currentHex) {
        val rgb = parseBrandingHexColor(currentHex)
        val max = maxOf(rgb.red, rgb.green, rgb.blue)
        val min = minOf(rgb.red, rgb.green, rgb.blue)
        val delta = max - min
        if (delta == 0f) 0f
        else {
            val h = when (max) {
                rgb.red -> 60f * (((rgb.green - rgb.blue) / delta) % 6f)
                rgb.green -> 60f * (((rgb.blue - rgb.red) / delta) + 2f)
                else -> 60f * (((rgb.red - rgb.green) / delta) + 4f)
            }
            if (h < 0f) h + 360f else h
        }
    }
    var sliderHue by remember { mutableStateOf(hue) }
    var sliderSat by remember { mutableStateOf(0.8f) }
    var sliderValue by remember { mutableStateOf(0.9f) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Hue slider with indicator
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFFFF0000),
                            0.167f to Color(0xFFFFFF00),
                            0.333f to Color(0xFF00FF00),
                            0.5f to Color(0xFF00FFFF),
                            0.667f to Color(0xFF0000FF),
                            0.833f to Color(0xFFFF00FF),
                            1f to Color(0xFFFF0000),
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val w = size.width.toFloat()
                            sliderHue = (offset.x / w).coerceIn(0f, 1f) * 360f
                            onColorSelected(hsvToHex(sliderHue, sliderSat, sliderValue))
                        },
                        onDrag = { change, _ ->
                            val w = size.width.toFloat()
                            sliderHue = (change.position.x / w).coerceIn(0f, 1f) * 360f
                            onColorSelected(hsvToHex(sliderHue, sliderSat, sliderValue))
                        },
                    )
                },
        ) {
            val indicatorOffset = maxWidth * (sliderHue / 360f) - 14.dp
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .align(Alignment.CenterStart)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.Black, CircleShape),
            )
        }

        // Saturation slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color.White,
                            1f to hsvToColor(sliderHue, 1f, sliderValue),
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val w = size.width.toFloat()
                            sliderSat = (offset.x / w).coerceIn(0f, 1f)
                            onColorSelected(hsvToHex(sliderHue, sliderSat, sliderValue))
                        },
                        onDrag = { change, _ ->
                            val w = size.width.toFloat()
                            sliderSat = (change.position.x / w).coerceIn(0f, 1f)
                            onColorSelected(hsvToHex(sliderHue, sliderSat, sliderValue))
                        },
                    )
                },
        )

        // Value (brightness) slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color.Black,
                            1f to hsvToColor(sliderHue, sliderSat, 1f),
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val w = size.width.toFloat()
                            sliderValue = (offset.x / w).coerceIn(0f, 1f)
                            onColorSelected(hsvToHex(sliderHue, sliderSat, sliderValue))
                        },
                        onDrag = { change, _ ->
                            val w = size.width.toFloat()
                            sliderValue = (change.position.x / w).coerceIn(0f, 1f)
                            onColorSelected(hsvToHex(sliderHue, sliderSat, sliderValue))
                        },
                    )
                },
        )
    }
}

private fun hsvToColor(h: Float, s: Float, v: Float): Color {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = v - c
    val (r, g, b) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m)
}

private fun hsvToHex(h: Float, s: Float, v: Float): String {
    val c = hsvToColor(h, s, v)
    val r = (c.red * 255).toInt()
    val g = (c.green * 255).toInt()
    val b = (c.blue * 255).toInt()
    return String.format("#%02X%02X%02X", r, g, b)
}

@Composable
private fun AssetUploadRow(
    label: String,
    field: String,
    url: String?,
    isUploading: Boolean,
    onUpload: (String, ByteArray, String, String) -> Unit,
    onDelete: (String) -> Unit,
) {
        val picker = rememberMediaPicker(
        onPicked = { bytes, mimeType, fileName ->
            onUpload(field, bytes, fileName, mimeType)
        },
        onUnsupported = { /* silently ignore — admin can try a different format */ },
    )

    VCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Preview thumbnail or placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VColors.ink.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = label,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                    )
                } else {
                    Icon(VIcons.Upload, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(20.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                Text(
                    if (url != null) appString(StringKeys.BRAND_UPLOADED) else appString(StringKeys.BRAND_NOT_SET),
                    style = VTypography.caption.copy(color = VColors.ink3),
                )
            }

            if (isUploading) {
                TeacherSpinner(20.dp, 2.dp)
            } else {
                if (url != null) {
                    VButton(
                        text = appString(StringKeys.BRAND_REMOVE),
                        onClick = { onDelete(field) },
                        variant = VButtonVariant.Ghost,
                        tone = VButtonTone.Rose,
                        size = VButtonSize.Sm,
                    )
                }
                VButton(
                    text = if (url != null) appString(StringKeys.BRAND_REPLACE) else appString(StringKeys.BRAND_UPLOAD),
                    onClick = { picker.launchImage() },
                    variant = VButtonVariant.Secondary,
                    size = VButtonSize.Sm,
                )
            }
        }
    }
}
