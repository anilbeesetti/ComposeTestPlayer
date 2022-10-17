package com.arcticoss.mediainfo

class NativeLib {

    /**
     * A native method that is implemented by the 'mediainfo' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'mediainfo' library on application startup.
        init {
            System.loadLibrary("mediainfo")
        }
    }
}