package com.arcticoss.nextplayer.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.arcticoss.model.PlayerUiPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PlayerUiPreferencesSerializer: Serializer<PlayerUiPreferences> {
    override val defaultValue: PlayerUiPreferences
        get() = PlayerUiPreferences()

    override suspend fun readFrom(input: InputStream): PlayerUiPreferences {
        try {
            return Json.decodeFromString(
                deserializer = PlayerUiPreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: PlayerUiPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = PlayerUiPreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}