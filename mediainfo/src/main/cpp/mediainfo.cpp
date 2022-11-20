#include <jni.h>
#include <string>
#include "mediainfo/frame_extractor.h"
#include "mediainfo/mediainfo_builder.h"

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_arcticoss_nextplayer_mediainfo_FrameLoader_nativeLoadFrame(JNIEnv *env,
                                                         jobject thiz,
                                                         jstring jFilePath,
                                                         jobject bitmap,
                                                         jlong at_duration) {
    const char *cFilePath = env->GetStringUTFChars(jFilePath, nullptr);
    bool result = frameExtractorLoadFrame(env, cFilePath, bitmap, at_duration);
    env->ReleaseStringUTFChars(jFilePath, cFilePath);
    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_arcticoss_nextplayer_mediainfo_MediaInfoBuilder_nativeCreateMediaInfo(JNIEnv *env,
                                                                    jobject thiz,
                                                                    jstring jPath) {
    const char *cPath = env->GetStringUTFChars(jPath, nullptr);
    mediaInfoBuilder(thiz, cPath);
    env->ReleaseStringUTFChars(jPath, cPath);
}