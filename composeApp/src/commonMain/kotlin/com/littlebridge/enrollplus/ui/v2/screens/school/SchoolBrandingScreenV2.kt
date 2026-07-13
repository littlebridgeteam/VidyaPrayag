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
import com.littlebridge.enrollplus.feature.admin.domain.model.PickedMedia
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingPhotosState
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingPhotosViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.GalleryPhoto
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressRing
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.v2.util.rememberImagePicker
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

@Composable
fun SchoolBrandingScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrandingPhotosViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var photoToDelete by remember { mutableStateOf<GalleryPhoto?>(null) }

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
    onUploadAdmin: (PickedMedia) -> Unit,
    onUploadLogo: (PickedMedia) -> Unit,
    onUploadCover: (PickedMedia) -> Unit,
    onUploadGallery: (PickedMedia) -> Unit,
    onDeleteGallery: (GalleryPhoto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 120.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
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
                style = VTheme.type.body.copy(color = VTheme.colors.success),
                modifier = Modifier.fillMaxWidth()
            )
        }
        state.errorMessage?.let {
            Text(
                it,
                style = VTheme.type.body.copy(color = VTheme.colors.error),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(title, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.ink)
        Text(subtitle, style = VTheme.type.caption.copy(fontSize = 11.sp), color = VTheme.colors.ink3)
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
                            .background(VTheme.colors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VProgressRing(value = 0f, size = 32.dp, strokeWidth = 3.dp)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text(name, style = VTheme.type.body.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.ink)
                Text("Profile picture", style = VTheme.type.caption, color = VTheme.colors.ink3)
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
                    .background(VTheme.colors.cream),
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
                        contentDescription = "",
                        tint = VTheme.colors.ink3,
                        modifier = Modifier.size(28.dp),
                    )
                }
                if (uploading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(VTheme.colors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VProgressRing(value = 0f, size = 28.dp, strokeWidth = 3.dp)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text("School logo", style = VTheme.type.body.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.ink)
                Text("Recommended: 1:1 ratio", style = VTheme.type.caption, color = VTheme.colors.ink3)
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
                    .background(VTheme.colors.cream),
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
                        contentDescription = "",
                        tint = VTheme.colors.ink3,
                        modifier = Modifier.size(36.dp),
                    )
                }
                if (uploading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(VTheme.colors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VProgressRing(value = 0f, size = 36.dp, strokeWidth = 4.dp)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Campus cover",
                    style = VTheme.type.body.copy(fontWeight = FontWeight.Bold),
                    color = VTheme.colors.ink,
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
                    .background(VTheme.colors.cream)
                    .clickable(enabled = !uploading) { launcher() },
                contentAlignment = Alignment.Center,
            ) {
                if (uploading) {
                    VProgressRing(value = 0f, size = 32.dp, strokeWidth = 3.dp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(VIcons.Plus, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(4.dp))
                        Text("Add photo", style = VTheme.type.caption, color = VTheme.colors.violet)
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
            .background(VTheme.colors.cream),
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
                .background(VTheme.colors.error.copy(alpha = 0.9f))
                .clickable { onDelete(photo) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Close, contentDescription = "Delete", tint = VTheme.colors.surface, modifier = Modifier.size(14.dp))
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
                    .background(VTheme.colors.cream),
            )
        }
    }
}
