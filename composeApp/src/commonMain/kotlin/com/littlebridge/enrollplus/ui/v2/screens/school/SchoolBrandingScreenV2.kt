package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.PickedMedia
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingPhotosState
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingPhotosViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.GalleryPhoto
import com.littlebridge.enrollplus.feature.branding.domain.model.UpdateBrandingRequest
import com.littlebridge.enrollplus.feature.branding.presentation.BrandingThemeManager
import com.littlebridge.enrollplus.feature.branding.presentation.BrandingViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressRing
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.v2.util.rememberImagePicker
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolBrandingScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrandingPhotosViewModel = koinViewModel(),
    brandingVm: BrandingViewModel = koinViewModel(),
    brandingThemeManager: BrandingThemeManager = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val brandingState by brandingVm.state.collectAsStateV2()
    var photoToDelete by remember { mutableStateOf<GalleryPhoto?>(null) }

    LaunchedEffect(Unit) {
        brandingVm.loadBranding()
    }

    var hasInitialLoad = remember { mutableStateOf(false) }
    LaunchedEffect(brandingState.branding) {
        if (brandingState.branding != null) {
            if (hasInitialLoad.value) {
                brandingThemeManager.loadBranding()
            } else {
                hasInitialLoad.value = true
            }
        }
    }

    LaunchedEffect(brandingState.infoMessage) {
        if (brandingState.infoMessage != null) {
            kotlinx.coroutines.delay(2000)
            brandingVm.clearInfoMessage()
        }
    }

    LaunchedEffect(brandingState.error) {
        if (brandingState.error != null) {
            kotlinx.coroutines.delay(5000)
            brandingVm.clearError()
        }
    }

    VConfirmDialog(
        visible = photoToDelete != null,
        title = "Delete photo",
        message = "Remove this campus photo from your gallery?",
        confirmLabel = "Delete",
        onConfirm = {
            photoToDelete?.let { viewModel.deleteGalleryPhoto(it.url) }
            photoToDelete = null
        },
        onDismiss = { photoToDelete = null },
        icon = VIcons.AlertTriangle,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        VBackHeader(
            title = "Branding & Photos",
            onBack = onBack,
            pinRouteId = "overlay_branding",
        )

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = false,
            onRetry = viewModel::load,
            skeleton = { BrandingPhotosSkeleton() },
            modifier = Modifier.fillMaxSize()
        ) {
            SchoolBrandingContent(
                state = state,
                brandingState = brandingState,
                onUpdateBranding = { req -> brandingVm.updateBranding(req) },
                onUploadAdmin = viewModel::uploadAdminPhoto,
                onUploadLogo = viewModel::uploadLogo,
                onUploadCover = viewModel::uploadCover,
                onUploadGallery = viewModel::uploadGalleryPhoto,
                onDeleteGallery = { photoToDelete = it },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SchoolBrandingContent(
    state: BrandingPhotosState,
    brandingState: com.littlebridge.enrollplus.feature.branding.presentation.BrandingState,
    onUpdateBranding: (UpdateBrandingRequest) -> Unit,
    onUploadAdmin: (PickedMedia) -> Unit,
    onUploadLogo: (PickedMedia) -> Unit,
    onUploadCover: (PickedMedia) -> Unit,
    onUploadGallery: (PickedMedia) -> Unit,
    onDeleteGallery: (GalleryPhoto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val branding = brandingState.branding
    var primaryColor by remember(branding?.primaryColor) {
        mutableStateOf(branding?.primaryColor ?: "#2563EB")
    }
    var secondaryColor by remember(branding?.secondaryColor) {
        mutableStateOf(branding?.secondaryColor ?: "#1E40AF")
    }
    var accentColor by remember(branding?.accentColor) {
        mutableStateOf(branding?.accentColor ?: "#3B82F6")
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 120.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // ── Custom App Colors ───────────────────────────────────────────
        SectionHeader("App colors", "Customize your school's accent colors")
        BrandingPreviewCard(
            branding = branding,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            accentColor = accentColor,
        )
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
            loading = brandingState.isLoading,
        )

        brandingState.infoMessage?.let { msg ->
            Text(
                msg,
                style = VTypography.body.copy(color = VColors.success),
                modifier = Modifier.fillMaxWidth()
            )
        }
        brandingState.error?.let { err ->
            Text(
                err,
                style = VTypography.body.copy(color = VColors.error),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ── Photos ───────────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))
        SectionHeader("Admin profile", "Your photo across the console")
        AdminProfileCard(
            name = state.adminName,
            photoUrl = state.adminProfilePicUrl,
            uploading = state.uploadingSlot == "admin",
            onPick = onUploadAdmin,
            modifier = Modifier.staggeredItemEntrance(0, true),
        )

        SectionHeader("School branding", "Logo seen by parents & staff")
        SchoolLogoCard(
            logoUrl = state.schoolLogoUrl,
            uploading = state.uploadingSlot == "logo",
            onPick = onUploadLogo,
            modifier = Modifier.staggeredItemEntrance(1, true),
        )

        SectionHeader("Campus cover", "Hero photo for your public profile")
        CoverPhotoCard(
            coverUrl = state.coverImageUrl,
            uploading = state.uploadingSlot == "cover",
            onPick = onUploadCover,
            modifier = Modifier.staggeredItemEntrance(2, true),
        )

        SectionHeader("Campus gallery", "Showcase your school (${state.galleryPhotos.size})")
        GalleryCard(
            photos = state.galleryPhotos,
            uploading = state.uploadingSlot == "gallery",
            onPick = onUploadGallery,
            onDelete = onDeleteGallery,
            modifier = Modifier.staggeredItemEntrance(3, true),
        )

        state.infoMessage?.let {
            Text(
                it,
                style = VTypography.body.copy(color = VColors.success),
                modifier = Modifier.fillMaxWidth()
            )
        }
        state.errorMessage?.let {
            Text(
                it,
                style = VTypography.body.copy(color = VColors.error),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(title, style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
        Text(subtitle, style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
    }
}

@Composable
private fun AdminProfileCard(
    name: String,
    photoUrl: String,
    uploading: Boolean,
    onPick: (PickedMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberImagePicker(onResult = { it?.let(onPick) })

    VCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                VAvatar(name = name, src = photoUrl, size = 72.dp)
                if (uploading) {
                    Box(
                        Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(VColors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VProgressRing(value = 0f, size = 32.dp, strokeWidth = 3.dp)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text(name, style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Text("Profile picture", style = VTypography.caption, color = VColors.ink3)
            }
            VButton(
                text = if (photoUrl.isBlank()) "Add" else "Change",
                onClick = launcher,
                variant = com.littlebridge.enrollplus.ui.v2.components.VButtonVariant.Secondary,
                size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
                enabled = !uploading,
            )
        }
    }
}

@Composable
private fun SchoolLogoCard(
    logoUrl: String,
    uploading: Boolean,
    onPick: (PickedMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberImagePicker(onResult = { it?.let(onPick) })

    VCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                if (logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = "School logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        VIcons.School,
                        contentDescription = null,
                        tint = VColors.ink3,
                        modifier = Modifier.size(28.dp),
                    )
                }
                if (uploading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(VColors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VProgressRing(value = 0f, size = 28.dp, strokeWidth = 3.dp)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text("School logo", style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Text("Recommended: 1:1 ratio", style = VTypography.caption, color = VColors.ink3)
            }
            VButton(
                text = if (logoUrl.isBlank()) "Add" else "Change",
                onClick = launcher,
                variant = com.littlebridge.enrollplus.ui.v2.components.VButtonVariant.Secondary,
                size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
                enabled = !uploading,
            )
        }
    }
}

@Composable
private fun CoverPhotoCard(
    coverUrl: String,
    uploading: Boolean,
    onPick: (PickedMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberImagePicker(onResult = { it?.let(onPick) })

    VCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                if (coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = "Campus cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        VIcons.Upload,
                        contentDescription = null,
                        tint = VColors.ink3,
                        modifier = Modifier.size(36.dp),
                    )
                }
                if (uploading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(VColors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VProgressRing(value = 0f, size = 36.dp, strokeWidth = 4.dp)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Campus cover",
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                    modifier = Modifier.weight(1f),
                )
                VButton(
                    text = if (coverUrl.isBlank()) "Add cover" else "Change cover",
                    onClick = launcher,
                    variant = com.littlebridge.enrollplus.ui.v2.components.VButtonVariant.Secondary,
                    size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
                    enabled = !uploading,
                )
            }
        }
    }
}

@Composable
private fun GalleryCard(
    photos: List<GalleryPhoto>,
    uploading: Boolean,
    onPick: (PickedMedia) -> Unit,
    onDelete: (GalleryPhoto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberImagePicker(onResult = { it?.let(onPick) })

    VCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            photos.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEach { photo ->
                        GalleryThumbnail(
                            photo = photo,
                            onDelete = onDelete,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.cream)
                    .clickable(enabled = !uploading) { launcher() },
                contentAlignment = Alignment.Center,
            ) {
                if (uploading) {
                    VProgressRing(value = 0f, size = 32.dp, strokeWidth = 3.dp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(VIcons.Plus, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(4.dp))
                        Text("Add photo", style = VTypography.caption, color = VColors.violet)
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryThumbnail(
    photo: GalleryPhoto,
    onDelete: (GalleryPhoto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(VColors.cream),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = photo.url,
            contentDescription = "Campus photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(26.dp)
                .clip(CircleShape)
                .background(VColors.error.copy(alpha = 0.9f))
                .clickable { onDelete(photo) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Close, contentDescription = "Delete", tint = VColors.surface, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun BrandingPhotosSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VColors.cream),
            )
        }
    }
}
