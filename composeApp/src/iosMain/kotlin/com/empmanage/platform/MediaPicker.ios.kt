package com.empmanage.platform

/**
 * iOS implementation of the media-picker contracts from commonMain.
 * Written against the UIKit APIs but, like the rest of iosMain, not
 * compiled or run here — there's no Xcode toolchain in this dev
 * environment. Verification is deferred to Phase 7 when Mac/Xcode
 * access is available. iOS doesn't need an explicit runtime permission
 * request the way Android does for camera/photo-library access — the
 * system prompts automatically on first use, driven by the
 * `NSCameraUsageDescription`/`NSPhotoLibraryUsageDescription` keys that
 * must be added to Info.plist when the Xcode project is created.
 */

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.lastPathComponent
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
private fun topViewController(): UIViewController? =
    UIApplication.sharedApplication.keyWindow?.rootViewController

@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectory(): String =
    NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String

@OptIn(ExperimentalForeignApi::class)
private fun saveImageToAppStorage(image: UIImage): String? {
    val data = UIImageJPEGRepresentation(image, 0.9) ?: return null
    val dirPath = documentsDirectory() + "/images"
    NSFileManager.defaultManager.createDirectoryAtPath(dirPath, true, null, null)
    val filePath = "$dirPath/profile_${currentTimeMillis()}.jpg"
    data.writeToFile(filePath, true)
    return filePath
}

private class ImagePickerDelegate(
    private val onPicked: (String?) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        onPicked(image?.let(::saveImageToAppStorage))
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        onPicked(null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun launchImagePicker(sourceType: UIImagePickerControllerSourceType, onPicked: (String?) -> Unit) {
    val picker = UIImagePickerController()
    picker.sourceType = sourceType
    val delegate = ImagePickerDelegate(onPicked)
    picker.delegate = delegate
    topViewController()?.presentViewController(picker, animated = true, completion = null)
}

@Composable
actual fun rememberCameraLauncher(
    onImageCaptured: (String) -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {
    return {
        launchImagePicker(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera) { path ->
            if (path != null) onImageCaptured(path)
        }
    }
}

@Composable
actual fun rememberGalleryLauncher(
    onImageSelected: (String) -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {
    return {
        launchImagePicker(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary) { path ->
            if (path != null) onImageSelected(path)
        }
    }
}

private class DocumentPickerDelegate(
    private val onPicked: (PickedDocument?) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url == null) {
            onPicked(null)
            return
        }
        val fileName = url.lastPathComponent ?: "resume"
        val dirPath = documentsDirectory() + "/documents"
        NSFileManager.defaultManager.createDirectoryAtPath(dirPath, true, null, null)
        val destinationPath = "$dirPath/${currentTimeMillis()}_$fileName"
        NSFileManager.defaultManager.copyItemAtPath(url.path ?: "", destinationPath, null)

        val attributes = NSFileManager.defaultManager.attributesOfItemAtPath(destinationPath, null)
        val sizeNumber = attributes?.get("NSFileSize")
        val fileSize = (sizeNumber as? platform.Foundation.NSNumber)?.longLongValue ?: 0L

        onPicked(
            PickedDocument(
                filePath = destinationPath,
                fileName = fileName,
                fileSizeBytes = fileSize,
                mimeType = mimeTypeForExtension(fileName)
            )
        )
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onPicked(null)
    }
}

private fun mimeTypeForExtension(fileName: String): String = when {
    fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
    fileName.endsWith(".docx", ignoreCase = true) ->
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    fileName.endsWith(".doc", ignoreCase = true) -> "application/msword"
    else -> "application/octet-stream"
}

@Composable
actual fun rememberDocumentLauncher(onDocumentPicked: (PickedDocument) -> Unit): () -> Unit {
    return {
        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("com.adobe.pdf", "com.microsoft.word.doc", "org.openxmlformats.wordprocessing-document"),
            inMode = platform.UIKit.UIDocumentPickerMode.UIDocumentPickerModeImport
        )
        val delegate = DocumentPickerDelegate { document -> document?.let(onDocumentPicked) }
        picker.delegate = delegate
        topViewController()?.presentViewController(picker, animated = true, completion = null)
    }
}
