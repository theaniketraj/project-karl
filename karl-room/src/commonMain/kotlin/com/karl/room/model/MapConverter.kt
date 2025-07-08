// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/MapConverter.kt
package com.karl.room

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

object MapConverter {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @TypeConverter
    fun fromString(value: String?): Map<String, Any>? {
        if (value == null) return null
        return try {
            // Deserialize the JSON string back into a Map<String, JsonElement>
            // then convert JsonElement values to basic Kotlin types.
            val jsonMap = json.decodeFromString<Map<String, JsonElement>>(value)
            jsonMap.mapValues { (_, jsonElement) ->
                val content = jsonElement.jsonPrimitive.content
                when {
                    jsonElement.jsonPrimitive.isString -> content
                    content == "true" || content == "false" -> {
                        content.toBoolean()
                    }
                    content.toIntOrNull() != null -> {
                        content.toInt()
                    }
                    content.toDoubleOrNull() != null -> {
                        content.toDouble()
                    }
                    else -> content
                }
            }
        } catch (e: Exception) {
            // Log error or return null/emptyMap if deserialization fails
            println("MapConverter Error deserializing: ${e.message}")
            null
        }
    }

    @TypeConverter
    fun toString(map: Map<String, Any>?): String? {
        if (map == null) return null
        return try {
            // For Map<String, Any>, we need to handle serialization of 'Any' carefully.
            // This example serializes all values as strings for simplicity with JsonPrimitives.
            // For more complex 'Any' types, you'd need a more sophisticated (polymorphic) serialization strategy.
            val stringifiedMap = map.mapValues { (_, value) -> value.toString() }
            json.encodeToString(
                MapSerializer(String.serializer(), String.serializer()),
                stringifiedMap,
            )
        } catch (e: Exception) {
            println("MapConverter Error serializing: ${e.message}")
            null
        }
    }
}
