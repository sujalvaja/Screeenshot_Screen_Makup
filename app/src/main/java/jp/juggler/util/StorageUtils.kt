package jp.juggler.util

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import jp.juggler.screenshotbutton.API_STORAGE_VOLUME
import jp.juggler.screenshotbutton.App1

@Suppress("unused")
private val log = LogCategory("${App1.tagPrefix}/StorageUtils")

private fun Cursor.getStringOrNull(idx: Int) = when {
    isNull(idx) -> null
    else -> getString(idx)
}

class FindMediaResult(val uri: Uri, val mimeType: String?)

fun findMedia(context: Context, uri: Uri): FindMediaResult? {
    return try {
        context.contentResolver.query(
            uri,
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.MIME_TYPE
            ),
            null,
            null,
            null
        )
            ?.use { cursor ->
                if (cursor.moveToNext()) {
                    val idxId = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                    val idxMimeType = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                    FindMediaResult(
                        ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            cursor.getLong(idxId)
                        ),
                        cursor.getStringOrNull(idxMimeType)
                    )
                } else {
                    log.eToast(context, false, "can't find content uri.")
                    null
                }
            }
    } catch (ex: Throwable) {
        log.eToast(context, ex, "findMedia() failed. $uri")
        null
    }
}

fun isExternalStorageDocument(uri: Uri) =
    "com.android.externalstorage.documents" == uri.authority

private const val PATH_TREE = "tree"
private const val PATH_DOCUMENT = "document"


private fun getDocumentId(documentUri: Uri): String {
    val paths = documentUri.pathSegments
    if (paths.size >= 2 && PATH_DOCUMENT == paths[0]) {
        return paths[1]
    }

    if (paths.size >= 4 && PATH_TREE == paths[0]
        && PATH_DOCUMENT == paths[2]
    ) {
        return paths[3]
    }

    if (paths.size >= 2 && PATH_TREE == paths[0]) {
        return paths[1]
    }
    error("getDocumentId() can'f find ID from $documentUri")
}

private val reAndroidDataFolder = """/Android/data/.*""".toRegex()

@TargetApi(30)
private fun getVolumePathApi30(context: Context, uuid: String): String {

    val list = ContextCompat.getExternalFilesDirs(context, null)
        .map { it.canonicalPath.replace(reAndroidDataFolder, "") }
    val path = if (uuid == "primary") {
        list.firstOrNull()
    } else {
        list.find { it.contains(uuid, ignoreCase = true) }
    }

    return path ?: error("can't find volume for uuid $uuid")
}

@TargetApi(24)
private fun getVolumePathApi24(storageManager: StorageManager, uuid: String): String =
    when (uuid) {
        "primary" -> storageManager.primaryStorageVolume
        else -> storageManager.storageVolumes.find { it.uuid == uuid }
    }?.let {
        StorageVolume::class.java.getMethod("getPath").invoke(it).cast()
    } ?: error("can't find volume for uuid $uuid")

@TargetApi(21)
private fun getVolumePathApi21(storageManager: StorageManager, uuid: String): String =
    when (uuid) {

        "primary" -> {
            @Suppress("DEPRECATION")
            Environment.getExternalStorageDirectory().toString()
        }

        else -> {

            val volumes = storageManager.javaClass.getMethod("getVolumeList")
                .invoke(storageManager)
                .cast<Array<*>>()
                ?: error("storageManager.getVolumeList() failed.")

            if (volumes.isEmpty())
                error("storageManager.getVolumeList() is empty.")

            val volumeClass = volumes.first()?.javaClass
                ?: error("volumes[0].javaClass is null.")

            val getUuid = volumeClass.getMethod("getUuid")

            val volume = volumes.find {
                it ?: return@find false
                uuid == getUuid.invoke(it)
            } ?: error("missing volume for uuid $uuid")

            val state = volumeClass.getMethod("getState")
                .invoke(volume)
                .cast<String>()

            if (state != "mounted")
                error("uuid is not mounted. $state")

            volumeClass.getMethod("getPath")
                .invoke(volume)
                .cast<String>()
                .notEmpty()
                ?: error("volume.getPath() failed.")
        }
    }

fun pathFromDocumentUriOrThrow(context: Context, uri: Uri) = when {

    uri.authority == "file" -> uri.path ?: "/"

    isExternalStorageDocument(uri) -> {
        val split = getDocumentId(uri).split(":").dropLastWhile { it.isEmpty() }
        if (split.size < 2) error("document id has no semicolon.")

        val storageManager = systemService<StorageManager>(context)!!

        val volumePath = when {
            Build.VERSION.SDK_INT >= 30 ->
                getVolumePathApi30(context, split[0])

            Build.VERSION.SDK_INT >= API_STORAGE_VOLUME ->
                getVolumePathApi24(storageManager, split[0])

            else ->
                getVolumePathApi21(storageManager, split[0])
        }

        "$volumePath/${split[1]}"
    }

    else ->
        error("Please specify the regular folder inside the storage top. This app does not support meta folders such as Downloads and Recents.")
}


fun pathFromDocumentUri(context: Context, uri: Uri): String? = try {
    pathFromDocumentUriOrThrow(context, uri)
} catch (ex: Throwable) {
    log.e(ex, "pathFromDocumentUri failed. $uri")
    null
}

fun generateDocument(
    context: Context,
    treeUriArg: Uri,
    baseName: String,
    mimeType: String
): Uri {
    val treeUri = DocumentsContract.buildDocumentUriUsingTree(
        treeUriArg,
        if (DocumentsContract.isDocumentUri(context, treeUriArg)) {
            DocumentsContract.getDocumentId(treeUriArg)
                ?: error("getTreeDocumentId returns null.")
        } else {
            DocumentsContract.getTreeDocumentId(treeUriArg)
                ?: error("getTreeDocumentId returns null.")
        }
    ) ?: error("buildDocumentUriUsingTree returns null.")

    return DocumentsContract.createDocument(context.contentResolver, treeUri, mimeType, baseName)
        ?: error("createDocument returns null.")
}

fun deleteDocument(
    context: Context,
    itemUri: Uri
): Boolean {
    if (DocumentsContract.deleteDocument(context.contentResolver, itemUri))
        return true
    context.contentResolver.query(
        itemUri,
        arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID),
        null,
        null,
        null
    )?.use {
        if (it.count == 0) return true
    }
    log.w("deleteDocument: can't delete, but exist… $itemUri")
    return false
}
