package com.arcticoss.nextplayer.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.arcticoss.model.InterfacePreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object InterfacePreferencesSerializer: Serializer<InterfacePreferences> {
    override val defaultValue: InterfacePreferences
        get() = InterfacePreferences()

    override suspend fun readFrom(input: InputStream): InterfacePreferences {
        try {
            return Json.decodeFromString(
                deserializer = InterfacePreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: InterfacePreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = InterfacePreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}