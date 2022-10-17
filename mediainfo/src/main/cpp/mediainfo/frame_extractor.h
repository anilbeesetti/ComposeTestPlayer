//
// Created by anil on 10/17/22.
//

#ifndef NEXTPLAYER_FRAME_EXTRACTOR_H
#define NEXTPLAYER_FRAME_EXTRACTOR_H

#include <jni.h>

bool frameExtractorLoadFrame(JNIEnv *env, const char *filePath, jobject jBitmap, jlong at_duration);

#endif //NEXTPLAYER_FRAME_EXTRACTOR_H
