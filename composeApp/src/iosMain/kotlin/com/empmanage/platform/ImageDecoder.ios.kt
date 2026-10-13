package com.empmanage.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
actual fun decodeImageFile(filePath: String): ImageBitmap? {
    val data: NSData = NSData.dataWithContentsOfFile(filePath) ?: return null
    val bytes = ByteArray(data.length.toInt())
    bytes.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
    }
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}
