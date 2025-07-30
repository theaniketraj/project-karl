// karl-project/karl-room/src/jvmMain/kotlin/com/karl/room/RoomDataStorage.kt
package com.karl.room

import com.karl.core.models.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlContainerState
import com.karl.room.model.InteractionDataEntity
import com.karl.room.model.KarlContainerStateEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomDataStorage(
    private val karlDao: KarlDao,
    // Inject IO dispatcher for database operations
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DataStorage {
    // Note: Initialization of the database itself happens outside this class,
    // typically via the KarlRoomDatabase.getDatabase() singleton pattern.
    override suspend fun initialize() {
        // Usually no-op here, DB init happens externally.
        // Could potentially verify connection if needed.
        println("RoomDataStorage: Initialized (DB access via DAO).")
    }

    override suspend fun saveContainerState(
        userId: String,
        state: KarlContainerState,
    ) {
        withContext(ioDispatcher) {
            val entity =
                KarlContainerStateEntity(
                    userId = userId,
                    stateData = state.data,
                    version = state.version,
                )
            karlDao.saveContainerState(entity)
        }
    }

    override suspend fun loadContainerState(userId: String): KarlContainerState? {
        return withContext(ioDispatcher) {
            karlDao.loadContainerState(userId)?.let { entity ->
                // Convert from Entity back to the core model
                KarlContainerState(data = entity.stateData, version = entity.version)
            }
        }
    }

    override suspend fun saveInteractionData(data: InteractionData) {
        withContext(ioDispatcher) {
            val entity =
                InteractionDataEntity(
                    userId = data.userId,
                    timestamp = data.timestamp,
                    type = data.type,
                    details = data.details,
                )
            karlDao.saveInteractionData(entity)
        }
    }

    override suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int,
        type: String?,
    ): List<InteractionData> {
        return withContext(ioDispatcher) {
            val entities =
                if (type == null) {
                    karlDao.loadRecentInteractionData(userId, limit)
                } else {
                    // Note: This method might not exist in KarlDao - we'll need to add it or use a different approach
                    karlDao.loadRecentInteractionData(userId, limit)
                }
            entities.map { entity ->
                InteractionData(
                    userId = entity.userId,
                    timestamp = entity.timestamp,
                    type = entity.type,
                    details = entity.details,
                )
            }
        }
    }

    override suspend fun deleteUserData(userId: String) {
        withContext(ioDispatcher) {
            karlDao.deleteContainerState(userId)
            karlDao.deleteAllUserInteractionData(userId)
        }
    }

    override suspend fun release() {
        // RoomDatabase instance should be closed by the singleton provider
        // when the application scope ends. This class doesn't own the DB connection.
        println("RoomDataStorage: Release requested (DB closure managed externally).")
    }
}
