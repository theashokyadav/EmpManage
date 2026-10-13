package com.empmanage.platform

import androidx.compose.runtime.Composable

/** Metadata returned alongside a picked document's locally-copied file path. */
data class PickedDocument(
    val filePath: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val mimeType: String
)

/**
 * Returns a trigger function that launches the system camera. The result
 * is a local file path (never Base64) already written into app storage.
 * Android requests the CAMERA runtime permission first if needed, calling
 * [onPermissionDenied] if the user declines.
 */
@Composable
expect fun rememberCameraLauncher(
    onImageCaptured: (String) -> Unit,
    onPermissionDenied: () -> Unit = {}
): () -> Unit

/**
 * Returns a trigger function that launches the system photo picker. The
 * picked image is copied into app-private storage and returned as a local
 * file path. Android requests the photo-library runtime permission first
 * if needed, calling [onPermissionDenied] if the user declines.
 */
@Composable
expect fun rememberGalleryLauncher(
    onImageSelected: (String) -> Unit,
    onPermissionDenied: () -> Unit = {}
): () -> Unit

/**
 * Returns a trigger function that launches the system document picker,
 * scoped to PDF/DOC/DOCX. The picked document is copied into app-private
 * storage; [PickedDocument] carries its path plus display metadata.
 */
@Composable
expect fun rememberDocumentLauncher(onDocumentPicked: (PickedDocument) -> Unit): () -> Unit
