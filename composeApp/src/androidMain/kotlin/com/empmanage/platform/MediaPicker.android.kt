package com.empmanage.platform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File

@Composable
actual fun rememberCameraLauncher(
    onImageCaptured: (String) -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    var pendingFile by remember { mutableStateOf<File?>(null) }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val file = pendingFile
        pendingFile = null
        if (success && file != null) {
            onImageCaptured(file.absolutePath)
        }
    }

    fun launchCamera() {
        val file = MediaFileFactory.newImageFile(context)
        pendingFile = file
        takePicture.launch(MediaFileFactory.fileProviderUri(context, file))
    }

    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera() else onPermissionDenied()
    }

    return {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) launchCamera() else requestPermission.launch(Manifest.permission.CAMERA)
    }
}

@Composable
actual fun rememberGalleryLauncher(
    onImageSelected: (String) -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val destination = MediaFileFactory.newImageFile(context)
            context.contentResolver.openInputStream(uri)?.use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
            onImageSelected(destination.absolutePath)
        }
    }

    fun launchPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // The Android Photo Picker (PickVisualMedia) is a privacy-friendly system UI that
    // doesn't strictly require READ_MEDIA_IMAGES/READ_EXTERNAL_STORAGE, but the spec
    // calls for explicit runtime permission handling for the photo library, so it's
    // requested here regardless.
    val photoLibraryPermission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchPicker() else onPermissionDenied()
    }

    return {
        val granted = ContextCompat.checkSelfPermission(context, photoLibraryPermission) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) launchPicker() else requestPermission.launch(photoLibraryPermission)
    }
}

@Composable
actual fun rememberDocumentLauncher(onDocumentPicked: (PickedDocument) -> Unit): () -> Unit {
    val context = LocalContext.current

    val openDocument = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val resolver = context.contentResolver
            var displayName = "resume"
            var reportedSize = 0L
            resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex >= 0) displayName = cursor.getString(nameIndex) ?: displayName
                    if (sizeIndex >= 0) reportedSize = cursor.getLong(sizeIndex)
                }
            }
            val mimeType = resolver.getType(uri) ?: "application/octet-stream"
            val destination = MediaFileFactory.newDocumentFile(context, displayName)
            resolver.openInputStream(uri)?.use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
            onDocumentPicked(
                PickedDocument(
                    filePath = destination.absolutePath,
                    fileName = displayName,
                    fileSizeBytes = if (reportedSize > 0) reportedSize else destination.length(),
                    mimeType = mimeType
                )
            )
        }
    }

    return {
        openDocument.launch(
            arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
        )
    }
}
