package com.arcticoss.nextplayer.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.arcticoss.nextplayer.core.model.UiPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UiPreferencesSerializer : Serializer<UiPreferences> {
    override val defaultValue: UiPreferences
        get() = UiPreferences()

    override suspend fun readFrom(input: InputStream): UiPreferences {
        try {
            return Json.decodeFromString(
                deserializer = UiPreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: UiPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = UiPreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}