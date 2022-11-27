//
// Created by anil on 10/17/22.
//

#include "log.h"
#include "utils.h"
#include "mediainfo_builder.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libavutil/bprint.h>
#include <libavutil/channel_layout.h>
#include <libavcodec/avcodec.h>
}


static jstring toJString(const char *cString) {
    jstring result = nullptr;
    if (cString != nullptr) {
        result = utils_get_env()->NewStringUTF(cString);
    }
    return result;
}

static jstring get_string(AVDictionary *metadata, const char *key) {
    jstring result = nullptr;
    AVDictionaryEntry *tag = av_dict_get(metadata, key, nullptr, 0);
    if (tag != nullptr) {
        result = utils_get_env()->NewStringUTF(tag->value);
    }
    return result;
}

static jstring get_title(AVDictionary *metadata) {
    return get_string(metadata, "title");
}

static jstring get_language(AVDictionary *metadata) {
    return get_string(metadata, "language");
}

static void onMediaFileFound(jobject jMediaMetadataBuilder, AVFormatContext *avFormatContext) {
    const char *fileFormatName = avFormatContext->iformat->long_name;

    jstring jFileFormatName = utils_get_env()->NewStringUTF(fileFormatName);

    utils_call_instance_method_void(jMediaMetadataBuilder,
                                    fields.MediaMetadataBuilder.onMediaFileFoundID,
                                    jFileFormatName,
                                    avFormatContext->duration / 1000);
}

static void onVideoStreamFound(jobject jMediaMetadataBuilder,
                               AVFormatContext *avFormatContext,
                               int index) {
    AVStream *stream = avFormatContext->streams[index];
    AVCodecParameters *parameters = stream->codecpar;
    auto codecDescriptor = avcodec_descriptor_get(parameters->codec_id);
    jstring jCodecName = utils_get_env()->NewStringUTF(codecDescriptor->long_name);
    AVRational guessedFrameRate = av_guess_frame_rate(
            avFormatContext,
            stream,
            nullptr
    );

    jdouble resultFrameRate = guessedFrameRate.num / (double) guessedFrameRate.den;

    utils_call_instance_method_void(jMediaMetadataBuilder,
                                    fields.MediaMetadataBuilder.onVideoStreamFoundID,
                                    index,
                                    get_title(stream->metadata),
                                    jCodecName,
                                    get_language(stream->metadata),
                                    parameters->bit_rate,
                                    resultFrameRate,
                                    parameters->width,
                                    parameters->height);
}

static void onAudioStreamFound(jobject jMediaMetadataBuilder,
                               AVFormatContext *avFormatContext,
                               int index) {
    AVStream *stream = avFormatContext->streams[index];
    AVCodecParameters *parameters = stream->codecpar;
    auto codecDescriptor = avcodec_descriptor_get(parameters->codec_id);
    jstring jCodecName = utils_get_env()->NewStringUTF(codecDescriptor->long_name);

    utils_call_instance_method_void(jMediaMetadataBuilder,
                                    fields.MediaMetadataBuilder.onAudioStreamFoundID,
                                    index,
                                    get_title(stream->metadata),
                                    jCodecName,
                                    get_language(stream->metadata),
                                    parameters->bit_rate,
                                    parameters->sample_rate,
                                    parameters->channels,
                                    nullptr);
}

static void onSubtitleStreamFound(jobject jMediaMetadataBuilder,
                                  AVFormatContext *avFormatContext,
                                  int index) {
    AVStream *stream = avFormatContext->streams[index];
    AVCodecParameters *parameters = stream->codecpar;
    auto codecDescriptor = avcodec_descriptor_get(parameters->codec_id);
    const char *codec = codecDescriptor->long_name;
    LOGD("%s", codec);
    jstring jCodecName = utils_get_env()->NewStringUTF(codec);

    utils_call_instance_method_void(jMediaMetadataBuilder,
                                    fields.MediaMetadataBuilder.onSubtitleStreamFoundID,
                                    index,
                                    get_title(stream->metadata),
                                    jCodecName,
                                    get_language(stream->metadata));

}

int mediaInfoBuilder(jobject jMediaMetadataBuilder, const char *path) {
    AVFormatContext *avFormatContext = nullptr;
    if (avformat_open_input(&avFormatContext, path, nullptr, nullptr) != 0) {
        LOGE("ERROR Could not open file");
        return -1;
    }
    if (avformat_find_stream_info(avFormatContext, nullptr) < 0) {
        LOGE("ERROR Could not get the stream info");
        return -1;
    }

    onMediaFileFound(jMediaMetadataBuilder, avFormatContext);

    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        AVCodecParameters *avCodecParameters = avFormatContext->streams[i]->codecpar;

        switch (avCodecParameters->codec_type) {
            case AVMEDIA_TYPE_VIDEO:
                onVideoStreamFound(jMediaMetadataBuilder, avFormatContext, i);
                break;
            case AVMEDIA_TYPE_AUDIO:
                onAudioStreamFound(jMediaMetadataBuilder, avFormatContext, i);
                break;
            case AVMEDIA_TYPE_SUBTITLE:
                onSubtitleStreamFound(jMediaMetadataBuilder, avFormatContext, i);
                break;
        }
    }
    avformat_free_context(avFormatContext);
    return 0;
}
