// karl-project/karl-room/src/jvmMain/kotlin/com/karl/room/KarlDao.kt
package com.karl.room // Or your chosen package for room implementation

import androidx.room.*
import com.karl.room.model.InteractionDataEntity
import com.karl.room.model.KarlContainerStateEntity

@Dao
interface KarlDao {
    // --- Methods for KarlContainerStateEntity ---

    // Use OnConflictStrategy.REPLACE to handle inserts and updates easily via userId PK
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContainerState(stateEntity: KarlContainerStateEntity)

    @Query("SELECT * FROM container_state WHERE userId = :userId LIMIT 1")
    suspend fun loadContainerState(userId: String): KarlContainerStateEntity? // Returns nullable Entity

    @Query("DELETE FROM container_state WHERE userId = :userId")
    suspend fun deleteContainerState(userId: String)

    // --- Methods for InteractionData ---

    @Insert
    suspend fun saveInteractionData(interactionData: InteractionDataEntity)

    // Query recent interactions for a user, ordered by timestamp
    @Query("SELECT * FROM interaction_data WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int,
    ): List<InteractionDataEntity>

    // Optional: Query filtered by type
    @Query("SELECT * FROM interaction_data WHERE userId = :userId AND type = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun loadRecentInteractionDataByType(
        userId: String,
        limit: Int,
        type: String,
    ): List<InteractionDataEntity>

    @Query("DELETE FROM interaction_data WHERE userId = :userId")
    suspend fun deleteInteractionData(userId: String)
}
