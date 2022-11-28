package com.arcticoss.nextplayer.mediainfo

import android.net.Uri
import com.arcticoss.nextplayer.mediainfo.models.AudioStream
import com.arcticoss.nextplayer.mediainfo.models.MediaInfo
import com.arcticoss.nextplayer.mediainfo.models.SubtitleStream
import com.arcticoss.nextplayer.mediainfo.models.VideoStream
import java.io.File

class MediaInfoBuilder {

    private var fileFormatName: String = ""
    private var duration: Long = 0
    private var videoStreams = mutableListOf<VideoStream>()
    private var audioStreams = mutableListOf<AudioStream>()
    private var subtitleStreams = mutableListOf<SubtitleStream>()

    private var fromFile: File? = null
    private var fromUri: Uri? = null


    fun from(uri: Uri) = apply {
        fromUri = uri
        nativeCreateMediaInfo(uri.toString())
    }

    fun from(file: File) = apply {
        fromFile = file
        nativeCreateMediaInfo(file.path)
    }

    fun build(): MediaInfo {
        if (fromFile == null && fromUri == null) {
            throw NoFileProvidedException()
        }
        return MediaInfo(
            title = fromFile?.name ?: "",
            filePath = fromFile?.path ?: "",
            size = fromFile?.length() ?: 0,
            lastModified = fromFile?.lastModified() ?: 0,
            width = videoStreams.firstOrNull()?.frameWidth ?: 0,
            height = videoStreams.firstOrNull()?.frameHeight ?: 0,
            frameRate = videoStreams.firstOrNull()?.frameRate ?: 0.0,
            fileFormatName = fileFormatName,
            duration = duration,
            videoStreams = videoStreams,
            audioStreams = audioStreams,
            subtitleStreams = subtitleStreams
        )
    }

    /**
     * JNI FUNCTIONS: functions to use in jni to build [MediaInfo] object.
     */


    private fun onMediaMetadataFound(
        fileFormatName: String,
        duration: Long
    ) {
        this.fileFormatName = fileFormatName
        this.duration = duration
    }

    private fun onVideoStreamFound(
        index: Int,
        title: String,
        codecName: String,
        language: String?,
        bitRate: Long,
        frameRate: Double,
        frameWidth: Int,
        frameHeight: Int,
    ) {
        this.videoStreams.add(VideoStream(index, title, codecName, language, bitRate, frameRate, frameWidth, frameHeight))
    }

    private fun onAudioStreamFound(
        index: Int,
        title: String,
        codecName: String,
        language: String?,
        bitRate: Long,
        sampleRate: Int,
        channels: Int,
        channelLayout: String?
    ) {
        this.audioStreams.add(AudioStream(index, title, codecName, language, sampleRate, bitRate, channels, channelLayout))
    }

    private fun onSubtitleStreamFound(
        index: Int,
        title: String?,
        codecName: String,
        language: String?
    ) {
        this.subtitleStreams.add(SubtitleStream(index, title, codecName, language))
    }

    private external fun nativeCreateMediaInfo(path: String)

    companion object {
        // Used to load the 'mediainfo' library and its dependencies on application startup.
        init {
            listOf("avutil", "avcodec", "avformat", "swscale", "mediainfo").forEach {
                System.loadLibrary(it)
            }
        }
    }
}

class NoFileProvidedException:Throwable()