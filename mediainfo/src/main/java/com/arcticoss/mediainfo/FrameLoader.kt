package com.arcticoss.mediainfo

import android.graphics.Bitmap

class FrameLoader {

    fun loadFrame(filePath: String, bitmap: Bitmap): Boolean {
        return nativeLoadFrame(filePath, bitmap, 0)
    }

    fun loadFrame(filePath: String, bitmap: Bitmap, atDuration: Long): Boolean {
        return nativeLoadFrame(filePath, bitmap, atDuration)
    }

    private external fun nativeLoadFrame(filePath: String, bitmap: Bitmap, atDuration: Long): Boolean

    companion object {
        init {
            listOf("avutil", "avcodec", "avformat", "swscale", "mediainfo").forEach {
                System.loadLibrary(it)
            }
        }
    }
}