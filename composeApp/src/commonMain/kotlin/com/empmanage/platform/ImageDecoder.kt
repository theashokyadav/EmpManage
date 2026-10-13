package com.empmanage.platform

import androidx.compose.ui.graphics.ImageBitmap

/** Decodes a local image file path into a Compose [ImageBitmap], or null if unreadable. */
expect fun decodeImageFile(filePath: String): ImageBitmap?
