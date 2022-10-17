//
// Created by anil on 10/17/22.
//

#ifndef NEXTPLAYER_MEDIA_METADATA_BUILDER_H
#define NEXTPLAYER_MEDIA_METADATA_BUILDER_H

#include <jni.h>

/**
* Open an file and read the header. Extracts Info about Media and creates MediaInfo jObject.
*
* @param jMediaMetadataBuilder jObject of the java class [MediaMetadataBuilder]
* @param filePath filePath of the file to open and decode.
*
* @return 0 on success, a negative number on failure.
*/
int mediaInfoBuilder(jobject jMediaMetadataBuilder, const char *uri);

#endif //NEXTPLAYER_MEDIA_METADATA_BUILDER_H
