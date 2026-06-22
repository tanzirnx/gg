package com.nitha.files

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * File Manager for NITHA
 */
class FileManager(private val context: Context) {

    private val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    /**
     * Create a new folder
     */
    suspend fun createFolder(name: String, parentUri: Uri? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            if (parentUri != null) {
                val parent = DocumentFile.fromTreeUri(context, parentUri)
                parent?.createDirectory(name) != null
            } else {
                val file = File(downloadsDir, name)
                file.mkdirs()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create a new file
     */
    suspend fun createFile(name: String, content: String = "", parentUri: Uri? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            if (parentUri != null) {
                val parent = DocumentFile.fromTreeUri(context, parentUri)
                val mimeType = getMimeType(name)
                val file = parent?.createFile(mimeType, name)
                file?.let {
                    context.contentResolver.openOutputStream(it.uri)?.use { os ->
                        os.write(content.toByteArray())
                    }
                }
                file != null
            } else {
                val file = File(downloadsDir, name)
                file.writeText(content)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * List files in directory
     */
    suspend fun listFiles(directoryUri: Uri? = null): List<FileItem> = withContext(Dispatchers.IO) {
        try {
            val files = if (directoryUri != null) {
                DocumentFile.fromTreeUri(context, directoryUri)?.listFiles()?.map {
                    FileItem(
                        name = it.name ?: "Unknown",
                        uri = it.uri,
                        isDirectory = it.isDirectory,
                        size = it.length(),
                        lastModified = it.lastModified()
                    )
                } ?: emptyList()
            } else {
                downloadsDir?.listFiles()?.map {
                    FileItem(
                        name = it.name,
                        uri = Uri.fromFile(it),
                        isDirectory = it.isDirectory,
                        size = it.length(),
                        lastModified = it.lastModified()
                    )
                } ?: emptyList()
            }
            files.sortedBy { !it.isDirectory }.sortedBy { it.name }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Delete file or folder
     */
    suspend fun deleteFile(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            DocumentFile.fromSingleUri(context, uri)?.delete() == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Rename file
     */
    suspend fun renameFile(uri: Uri, newName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            DocumentFile.fromSingleUri(context, uri)?.renameTo(newName) == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Search files
     */
    suspend fun searchFiles(query: String, directoryUri: Uri? = null): List<FileItem> = withContext(Dispatchers.IO) {
        try {
            val allFiles = listFiles(directoryUri)
            allFiles.filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Create ZIP archive
     */
    suspend fun createZip(files: List<Uri>, outputName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val zipFile = File(downloadsDir, outputName)
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                files.forEach { uri ->
                    val file = DocumentFile.fromSingleUri(context, uri)
                    file?.let {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            val entry = ZipEntry(it.name ?: "file")
                            zos.putNextEntry(entry)
                            input.copyTo(zos)
                            zos.closeEntry()
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Extract ZIP
     */
    suspend fun extractZip(zipUri: Uri, destinationUri: Uri? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            val destDir = if (destinationUri != null) {
                DocumentFile.fromTreeUri(context, destinationUri)
            } else {
                null
            }

            context.contentResolver.openInputStream(zipUri)?.use { input ->
                ZipInputStream(input).use { zis ->
                    var entry: ZipEntry?
                    while (zis.nextEntry.also { entry = it } != null) {
                        entry?.let { zipEntry ->
                            if (destDir != null) {
                                destDir.createFile("application/octet-stream", zipEntry.name)?.let { newFile ->
                                    context.contentResolver.openOutputStream(newFile.uri)?.use { os ->
                                        zis.copyTo(os)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".txt") -> "text/plain"
            fileName.endsWith(".pdf") -> "application/pdf"
            fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> "image/jpeg"
            fileName.endsWith(".png") -> "image/png"
            fileName.endsWith(".mp3") -> "audio/mpeg"
            fileName.endsWith(".mp4") -> "video/mp4"
            fileName.endsWith(".zip") -> "application/zip"
            else -> "application/octet-stream"
        }
    }
}

data class FileItem(
    val name: String,
    val uri: Uri,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
)
