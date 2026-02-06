package com.rentitem.core.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver

        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null

        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)

        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}