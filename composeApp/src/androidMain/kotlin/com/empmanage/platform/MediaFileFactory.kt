package com.empmanage.platform

import android.content.Context
import java.io.File

internal object MediaFileFactory {
    fun newImageFile(context: Context): File {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        return File(dir, "profile_${currentTimeMillis()}.jpg")
    }

    fun newDocumentFile(context: Context, originalName: String): File {
        val dir = File(context.filesDir, "documents").apply { mkdirs() }
        val safeName = originalName.ifBlank { "resume_${currentTimeMillis()}" }
        return File(dir, "${currentTimeMillis()}_$safeName")
    }

    fun fileProviderUri(context: Context, file: File) =
        androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
