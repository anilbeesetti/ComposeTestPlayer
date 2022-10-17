package com.arcticoss.mediainfo

import android.net.Uri
import com.arcticoss.mediainfo.models.AudioStream
import com.arcticoss.mediainfo.models.MediaInfo
import com.arcticoss.mediainfo.models.SubtitleStream
import com.arcticoss.mediainfo.models.VideoStream

class MediaInfoBuilder {

    private var fileFormatName: String = ""
    private var duration: Long = 0
    private var videoStreams = mutableListOf<VideoStream>()
    private var audioStreams = mutableListOf<AudioStream>()
    private var subtitleStreams = mutableListOf<SubtitleStream>()


    fun from(uri: Uri) = apply {
        nativeCreateMediaInfo(uri.toString())
    }

    fun fromFile(path: String) = apply {
        nativeCreateMediaInfo(path)
    }

    fun build(): MediaInfo {
        return MediaInfo(
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