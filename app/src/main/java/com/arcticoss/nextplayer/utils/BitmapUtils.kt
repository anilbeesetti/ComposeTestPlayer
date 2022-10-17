package com.arcticoss.nextplayer.utils

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

fun Bitmap.saveThumbnail(path: String): String {
    var thumbPath = ""
    try {
        val quality = 100
        path.let {
            var file: File
            do {
                thumbPath = it + "/" + Random.nextInt(1, Int.MAX_VALUE) + ".jpg"
                file = File(thumbPath)
            } while (file.exists())
            val fos = FileOutputStream(file)
            this.compress(Bitmap.CompressFormat.JPEG, quality, fos)
            fos.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return if (File(thumbPath).exists()) thumbPath else ""
}