package com.empmanage.platform

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeImageFile(filePath: String): ImageBitmap? =
    BitmapFactory.decodeFile(filePath)?.asImageBitmap()
