// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/KarlDao.kt
package com.karl.room

// Removed original Room imports and using stubs
// import androidx.room.*
import com.karl.room.model.InteractionDataEntity
import com.karl.room.model.KarlContainerStateEntity

// Removed @Dao annotation for stub implementation
// @Dao
interface KarlDao {
    // --- Methods for KarlContainerStateEntity ---

    // Use OnConflictStrategy.REPLACE to handle inserts and updates easily via userId PK
    // @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContainerState(stateEntity: KarlContainerStateEntity)

    // @Query("SELECT * FROM container_state WHERE userId = :userId LIMIT 1")
    suspend fun loadContainerState(userId: String): KarlContainerStateEntity? // Returns nullable Entity

    // @Query("DELETE FROM container_state WHERE userId = :userId")
    suspend fun deleteContainerState(userId: String)

    // --- Methods for InteractionData ---

    // @Insert
    suspend fun saveInteractionData(interactionData: InteractionDataEntity)

    // Query recent interactions for a user, ordered by timestamp
    // @Query("SELECT * FROM interaction_data WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int = 100,
    ): List<InteractionDataEntity>

    // Query all interactions for a user
    // @Query("SELECT * FROM interaction_data WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun loadAllInteractionData(userId: String): List<InteractionDataEntity>

    // Delete all interactions for a user (useful for clearing user data)
    // @Query("DELETE FROM interaction_data WHERE userId = :userId")
    suspend fun deleteAllUserInteractionData(userId: String)

    // Optional: Query interactions within a timestamp range
    // @Query(
    //     "SELECT * FROM interaction_data WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC",
    // )
    suspend fun loadInteractionDataInRange(
        userId: String,
        startTime: Long,
        endTime: Long,
    ): List<InteractionDataEntity>
}
