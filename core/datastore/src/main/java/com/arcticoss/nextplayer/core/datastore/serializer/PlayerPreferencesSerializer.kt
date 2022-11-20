package com.arcticoss.nextplayer.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PlayerPreferencesSerializer: Serializer<PlayerPreferences> {
    override val defaultValue: PlayerPreferences
        get() = PlayerPreferences()

    override suspend fun readFrom(input: InputStream): PlayerPreferences {
        try {
            return Json.decodeFromString(
                deserializer = PlayerPreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: PlayerPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = PlayerPreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}