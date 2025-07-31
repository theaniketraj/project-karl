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
        println("RoomDataStorage: saveContainerState called for userId=$userId")
        println("RoomDataStorage: State data size=${state.data.size}, version=${state.version}")
        withContext(ioDispatcher) {
            val entity =
                KarlContainerStateEntity(
                    userId = userId,
                    stateData = state.data,
                    version = state.version,
                )
            println("RoomDataStorage: Created entity, about to call karlDao.saveContainerState()...")
            karlDao.saveContainerState(entity)
            println("RoomDataStorage: Successfully saved container state to database via DAO")
        }
    }

    override suspend fun loadContainerState(userId: String): KarlContainerState? {
        println("RoomDataStorage: loadContainerState called for userId=$userId")
        return withContext(ioDispatcher) {
            println("RoomDataStorage: About to call karlDao.loadContainerState()...")
            val entity = karlDao.loadContainerState(userId)
            if (entity != null) {
                println("RoomDataStorage: Found existing state in database")
                println("RoomDataStorage: State data size=${entity.stateData.size} bytes, version=${entity.version}")
                val state = KarlContainerState(data = entity.stateData, version = entity.version)
                println("RoomDataStorage: Successfully loaded container state from database")
                return@withContext state
            } else {
                println("RoomDataStorage: No existing state found in database for userId=$userId")
                return@withContext null
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
