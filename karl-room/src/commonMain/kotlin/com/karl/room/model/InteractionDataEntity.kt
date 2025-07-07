// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/model/InteractionDataEntity.kt
package com.karl.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.karl.room.MapConverter // Assuming MapConverter is in com.karl.room package

/**
 * Room Entity representing a piece of user interaction data. This class is specific to the Room
 * persistence layer.
 *
 * It mirrors the structure of `com.karl.core.models.InteractionData` but is annotated for Room and
 * includes a database primary key.
 */
@Entity(tableName = "interaction_data")
@TypeConverters(MapConverter::class) // Apply the converter for the 'details' Map
data class InteractionDataEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0, // Auto-generated primary key for the database table
        val type: String,
        val details: Map<String, Any>, // Will be converted by MapConverter
        val timestamp: Long,
        val userId: String, // Important for querying user-specific data
)
