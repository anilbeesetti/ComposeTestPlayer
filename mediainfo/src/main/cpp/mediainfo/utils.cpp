//
// Created by anil on 10/17/22.
//

#include "utils.h"
#include "log.h"

struct fields fields;
static JavaVM *javaVM;

JNIEnv *utils_get_env() {
    JNIEnv *env;
    if (javaVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return nullptr;
    }
    return env;
}

int utils_fields_init(JavaVM *vm) {
    javaVM = vm;

    JNIEnv *env = utils_get_env();
    if (env == nullptr) {
        return -1;
    }

    #define GET_CLASS(clazz, str, b_global) do { \
        (clazz) = env->FindClass((str)); \
        if (!(clazz)) { \
            LOGE("FindClass(%s) failed", (str)); \
            return -1; \
        } \
        if (b_global) { \
            (clazz) = (jclass) env->NewGlobalRef((clazz)); \
            if (!(clazz)) { \
                LOGE("NewGlobalRef(%s) failed", (str)); \
                return -1; \
            } \
        } \
    } while (0)

    #define GET_ID(get, id, clazz, str, args) do { \
        (id) = env->get((clazz), (str), (args)); \
        if (!(id)) { \
            LOGE(#get"(%s) failed", (str)); \
            return -1; \
        } \
    } while (0)

    GET_CLASS(fields.MediaMetadataBuilder.clazz, "com/arcticoss/nextplayer/mediainfo/MediaInfoBuilder", true);

    GET_ID(GetMethodID,
           fields.MediaMetadataBuilder.onMediaFileFoundID,
           fields.MediaMetadataBuilder.clazz,
           "onMediaMetadataFound", "(Ljava/lang/String;J)V");

    GET_ID(GetMethodID,
           fields.MediaMetadataBuilder.onVideoStreamFoundID,
           fields.MediaMetadataBuilder.clazz,
           "onVideoStreamFound", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;JDII)V");

    GET_ID(GetMethodID,
           fields.MediaMetadataBuilder.onAudioStreamFoundID,
           fields.MediaMetadataBuilder.clazz,
           "onAudioStreamFound", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;JIILjava/lang/String;)V");

    GET_ID(GetMethodID,
           fields.MediaMetadataBuilder.onSubtitleStreamFoundID,
           fields.MediaMetadataBuilder.clazz,
           "onSubtitleStreamFound", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

    return 0;
}

void utils_fields_free(JavaVM *vm) {
    JNIEnv *env = utils_get_env();
    if (vm == nullptr) {
        return;
    }

    env->DeleteGlobalRef(fields.MediaMetadataBuilder.clazz);

    javaVM = nullptr;
}

void utils_call_instance_method_void(jobject instance, jmethodID methodID, ...) {
    va_list args;
    va_start(args, methodID);
    utils_get_env()->CallVoidMethodV(instance, methodID, args);
    va_end(args);
}

jobject utils_call_instance_method_result(jobject instance, jmethodID methodID, ...) {
    va_list args;
    va_start(args, methodID);
    jobject result = utils_get_env()->CallObjectMethodV(instance, methodID, args);
    va_end(args);
    return result;
}
