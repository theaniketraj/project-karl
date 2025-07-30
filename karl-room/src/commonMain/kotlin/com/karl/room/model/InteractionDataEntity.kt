// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/model/InteractionDataEntity.kt
package com.karl.room.model

// Removed Room imports for stub implementation
// import androidx.room.Entity
// import androidx.room.PrimaryKey
// import androidx.room.TypeConverters
// import com.karl.room.MapConverter // Assuming MapConverter is in com.karl.room package

/**
 * Room Entity representing a piece of user interaction data. This class is specific to the Room
 * persistence layer.
 *
 * It mirrors the structure of `com.karl.core.models.InteractionData` but is annotated for Room and
 * includes a database primary key.
 *
 * Room annotations removed for stub implementation:
 * @Entity(tableName = "interaction_data")
 * @TypeConverters(MapConverter::class) Apply the converter for the 'details' Map
 */
data class InteractionDataEntity(
    // @PrimaryKey(autoGenerate = true)
    // Auto-generated primary key for the database table
    val id: Long = 0,
    val type: String,
    // Will be converted by MapConverter
    val details: Map<String, Any>,
    val timestamp: Long,
    // Important for querying user-specific data
    val userId: String,
)
