// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/model/MapConverter.kt
package com.karl.room.model

/**
 * Stub MapConverter for build purposes until Room KMP is properly configured
 */
object MapConverter {
    // Removed Room annotation for stub implementation
    // @TypeConverter
    fun fromString(value: String?): Map<String, Any>? {
        if (value == null) return null
        println("Stub MapConverter: fromString called with: $value")
        // Return empty map for stub
        return emptyMap()
    }

    // Removed Room annotation for stub implementation
    // @TypeConverter
    fun toString(map: Map<String, Any>?): String? {
        if (map == null) return null
        println("Stub MapConverter: toString called with: $map")
        // Return empty JSON object for stub
        return "{}"
    }
}
