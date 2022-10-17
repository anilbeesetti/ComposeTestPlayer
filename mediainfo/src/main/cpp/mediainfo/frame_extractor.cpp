//
// Created by anil on 10/17/22.
// Source: https://github.com/Javernaut/MediaFile/blob/master/media-file/src/main/cpp/frame_extractor.cpp
//

#include <android/bitmap.h>
#include "frame_extractor.h"
#include "log.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libavutil/bprint.h>
#include <libavutil/channel_layout.h>
#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

static bool frameExtractorLoadFrameInto(JNIEnv *env, AVFormatContext *avFormatContext, int videoStreamIndex, jobject bitmap, jlong at_duration) {
    AVStream *avVideoStream = avFormatContext->streams[videoStreamIndex];
    AVCodecParameters *avCodecParameters = avVideoStream->codecpar;
    auto *avVideoCodec = avcodec_find_decoder(avCodecParameters->codec_id);
    AndroidBitmapInfo bitmapMetricInfo;
    AndroidBitmap_getInfo(env, bitmap, &bitmapMetricInfo);
    auto pixelFormat = static_cast<AVPixelFormat>(avCodecParameters->format);
    if (pixelFormat == AV_PIX_FMT_NONE) {
        LOGE("ERROR Could not find pixel format");
        return false;
    }

    uint dstW = bitmapMetricInfo.width == 0 ? avCodecParameters->width : bitmapMetricInfo.width;
    uint dstH = bitmapMetricInfo.height == 0 ? avCodecParameters->height : bitmapMetricInfo.height;

    SwsContext *scalingContext =
            sws_getContext(
                    // srcW
                    avCodecParameters->width,
                    // srcH
                    avCodecParameters->height,
                    // srcFormat
                    pixelFormat,
                    // dstW
                    dstW,
                    // dstH
                    dstH,
                    // dstFormat
                    AV_PIX_FMT_RGBA,
                    SWS_BICUBIC, nullptr, nullptr, nullptr);

    int64_t videoDuration = avVideoStream->duration;

    // In some cases the duration is of a video stream is set to Long.MIN_VALUE and we need compute it in another way
    if (videoDuration == LONG_LONG_MIN && avVideoStream->time_base.den != 0) {
        videoDuration = avFormatContext->duration / avVideoStream->time_base.den;
    }


    // We extract frames right from the middle of a region, so the offset equals to a half of a region
    int64_t offset = videoDuration / 4;

    AVPacket *packet = av_packet_alloc();
    AVFrame *frame = av_frame_alloc();


    int64_t seekPosition = at_duration != 0 ? at_duration : (videoDuration < 10000000 ? 0 : offset);
    av_seek_frame(avFormatContext,
                  videoStreamIndex,
                  seekPosition,
                  0
    );

    AVCodecContext *videoCodecContext = avcodec_alloc_context3(avVideoCodec);
    avcodec_parameters_to_context(videoCodecContext, avCodecParameters);
    avcodec_open2(videoCodecContext, avVideoCodec, nullptr);
    bool resultValue = true;

    while (true) {
        if (av_read_frame(avFormatContext, packet) < 0) {
            resultValue = false;
            break;
        }

        if (packet->stream_index == videoStreamIndex) {
            avcodec_send_packet(videoCodecContext, packet);
            int response = avcodec_receive_frame(videoCodecContext, frame);
            if (response == AVERROR(EAGAIN)) {
                // A frame can be split across several packets, so continue reading in this case
                continue;
            }

            if (response >= 0) {
                AVFrame *frameForDrawing = av_frame_alloc();
                void *bitmapBuffer;
                AndroidBitmap_lockPixels(env, bitmap, &bitmapBuffer);

                // prepare a FFmpeg's Frame to use android Bitmap's buffer
                av_image_fill_arrays(
                        frameForDrawing->data,
                        frameForDrawing->linesize,
                        static_cast<const uint8_t *>(bitmapBuffer),
                        AV_PIX_FMT_RGBA,
                        dstW,
                        dstH,
                        1);

                // Scale
                sws_scale(
                        scalingContext,
                        frame->data,
                        frame->linesize,
                        0,
                        avCodecParameters->height,
                        frameForDrawing->data,
                        frameForDrawing->linesize);

                av_frame_free(&frameForDrawing);
                AndroidBitmap_unlockPixels(env, bitmap);
                break;
            }
        }
        av_packet_unref(packet);
    }


    av_packet_free(&packet);
    av_frame_free(&frame);
    avcodec_free_context(&videoCodecContext);

    sws_freeContext(scalingContext);

    return resultValue;
}

bool frameExtractorLoadFrame(JNIEnv *env, const char *filePath, jobject jBitmap, jlong at_duration) {
    AVFormatContext *avFormatContext = nullptr;
    if (avformat_open_input(&avFormatContext, filePath, nullptr, nullptr) != 0) {
        LOGE("ERROR Could not open file");
        return false;
    }
    if (avformat_find_stream_info(avFormatContext, nullptr) < 0) {
        LOGE("ERROR Could not get the stream info");
        return false;
    }

    int videoStreamIndex = -1;

    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        AVCodecParameters *avCodecParameters = avFormatContext->streams[i]->codecpar;

        if (avCodecParameters->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStreamIndex = i;
        }
    }

    return frameExtractorLoadFrameInto(env, avFormatContext, videoStreamIndex, jBitmap, at_duration);
}

