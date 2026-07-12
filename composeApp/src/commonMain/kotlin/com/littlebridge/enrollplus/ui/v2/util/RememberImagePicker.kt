package com.littlebridge.enrollplus.ui.v2.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.littlebridge.enrollplus.feature.admin.domain.model.PickedMedia
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch

@Composable
fun rememberImagePicker(
    onResult: (PickedMedia?) -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Single,
        title = "Choose a photo",
    ) { platformFile ->
        if (platformFile == null) {
            onResult(null)
            return@rememberFilePickerLauncher
        }
        scope.launch {
            val bytes = platformFile.readBytes()
            val name = platformFile.name
            val mime = guessImageMimeType(name)
            onResult(
                PickedMedia(
                    bytes = bytes,
                    fileName = name,
                    mimeType = mime
                )
            )
        }
    }

    return remember(launcher) {
        { launcher.launch() }
    }
}

private fun guessImageMimeType(fileName: String): String {
    return when (fileName.substringAfterLast('.', "").lowercase()) {
        "png" -> "image/png"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        "bmp" -> "image/bmp"
        else -> "image/jpeg"
    }
}
