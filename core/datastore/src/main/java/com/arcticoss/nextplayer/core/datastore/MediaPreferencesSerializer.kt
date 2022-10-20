package com.arcticoss.nextplayer.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.arcticoss.model.MediaPreferences
import com.arcticoss.model.PlayerUiPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object MediaPreferencesSerializer: Serializer<MediaPreferences> {
    override val defaultValue: MediaPreferences
        get() = MediaPreferences()

    override suspend fun readFrom(input: InputStream): MediaPreferences {
        try {
            return Json.decodeFromString(
                deserializer = MediaPreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: MediaPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = MediaPreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}