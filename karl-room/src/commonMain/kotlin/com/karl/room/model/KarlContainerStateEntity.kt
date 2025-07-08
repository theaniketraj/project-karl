// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/model/KarlContainerStateEntity.kt
package com.karl.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing the persisted state of a KarlContainer for a specific user.
 * This class is specific to the Room persistence layer.
 */
@Entity(tableName = "container_state")
data class KarlContainerStateEntity(
    // The userId uniquely identifies the state for a container
    @PrimaryKey
    val userId: String,
    // Explicitly tell Room this is a Blob/ByteArray
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val stateData: ByteArray, // The serialized AI model state
    val version: Int, // Version of the state data structure
) {
    // It's good practice to provide equals() and hashCode() when dealing with ByteArrays
    // if you intend to compare instances of this entity directly (e.g., in tests or collections).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false // Using javaClass for KMP compatibility if needed

        other as KarlContainerStateEntity

        if (userId != other.userId) return false
        if (!stateData.contentEquals(other.stateData)) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + stateData.contentHashCode()
        result = 31 * result + version
        return result
    }
}
