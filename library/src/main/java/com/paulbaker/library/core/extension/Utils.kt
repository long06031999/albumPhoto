package com.paulbaker.library.core.extension

import android.graphics.Bitmap
import android.util.Base64
import java.io.*
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile


class Utils {
    companion object {
        fun File.toBase64(): String? {
            val result: String?
            inputStream().use { inputStream ->
                val sourceBytes = inputStream.readBytes()
                result = Base64.encodeToString(sourceBytes, Base64.DEFAULT)
            }
            return result
        }

        fun decodeBase64ToBitMap(imageString: String?): Bitmap? {
            if (!imageString.isNotNull()) {
                if (!imageString!!.isValidValue()) return null
            }
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

        fun uriToFile(uri: Uri): File {
            return uri.toFile()
        }

    }
}