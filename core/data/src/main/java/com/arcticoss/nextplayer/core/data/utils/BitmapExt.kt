package com.arcticoss.nextplayer.core.data.utils

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

fun Bitmap.saveThumbnail(storagePath: String, quality: Int = 100): String {
    var thumbPath = ""
    try {
        var file: File
        do {
            thumbPath = storagePath + "/" + Random.nextInt(1, Int.MAX_VALUE) + ".jpg"
            file = File(thumbPath)
        } while (file.exists())
        val fos = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG, quality, fos)
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return if (File(thumbPath).exists()) thumbPath else ""
}