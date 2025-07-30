// Stub implementation for Room KMP until dependencies are resolved
package com.karl.room

// Stub Room classes
abstract class RoomDatabase {
    abstract fun clearAllTables()
}

object Room {
    fun databaseBuilder(
        klass: kotlin.reflect.KClass<out RoomDatabase>,
        name: String,
    ): DatabaseBuilder {
        return DatabaseBuilder()
    }
}

class DatabaseBuilder {
    fun fallbackToDestructiveMigration(): DatabaseBuilder = this

    fun build(): KarlRoomDatabase {
        return object : KarlRoomDatabase() {
            override fun karlDao(): KarlDao =
                object : KarlDao {
                    override suspend fun saveContainerState(stateEntity: com.karl.room.model.KarlContainerStateEntity) {
                        println("Stub: saveContainerState")
                    }

                    override suspend fun loadContainerState(userId: String): com.karl.room.model.KarlContainerStateEntity? {
                        println("Stub: loadContainerState")
                        return null
                    }

                    override suspend fun deleteContainerState(userId: String) {
                        println("Stub: deleteContainerState")
                    }

                    override suspend fun saveInteractionData(interactionData: com.karl.room.model.InteractionDataEntity) {
                        println("Stub: saveInteractionData")
                    }

                    override suspend fun loadRecentInteractionData(
                        userId: String,
                        limit: Int,
                    ): List<com.karl.room.model.InteractionDataEntity> {
                        println("Stub: loadRecentInteractionData")
                        return emptyList()
                    }

                    override suspend fun loadAllInteractionData(userId: String): List<com.karl.room.model.InteractionDataEntity> {
                        println("Stub: loadAllInteractionData")
                        return emptyList()
                    }

                    override suspend fun deleteAllUserInteractionData(userId: String) {
                        println("Stub: deleteAllUserInteractionData")
                    }

                    override suspend fun loadInteractionDataInRange(
                        userId: String,
                        startTime: Long,
                        endTime: Long,
                    ): List<com.karl.room.model.InteractionDataEntity> {
                        println("Stub: loadInteractionDataInRange")
                        return emptyList()
                    }
                }

            override fun clearAllTables() {
                println("Stub: clearAllTables")
            }
        }
    }
}
