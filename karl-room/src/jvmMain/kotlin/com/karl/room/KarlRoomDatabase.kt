// karl-project/karl-room/src/jvmMain/kotlin/com/karl/room/KarlRoomDatabase.kt
package com.karl.room

import androidx.room.*
import com.karl.room.model.InteractionDataEntity
import com.karl.room.model.KarlContainerStateEntity
import com.karl.room.model.MapConverter

@Database(
    entities = [
        InteractionDataEntity::class,
        KarlContainerStateEntity::class,
    ],
    version = 1, // Start with version 1. Increment when schema changes.
    exportSchema = true, // Recommended: Exports schema to specified location for migrations
)
@TypeConverters(MapConverter::class) // Register type converters for the DB
abstract class KarlRoomDatabase : RoomDatabase() {
    // Abstract method to get the DAO instance
    abstract fun karlDao(): KarlDao

    // Companion object for singleton instance creation (optional but common)
    companion object {
        @Volatile private var instance: KarlRoomDatabase? = null

        fun getDatabase(
            // Context/Driver setup will differ for KMP Desktop vs Android
            // For Desktop, you typically provide the DB file path or driver
            dbPath: String, // e.g., "path/to/karl_data.db"
            // Potentially pass driver factory if needed by Room KMP Desktop setup
        ): KarlRoomDatabase {
            return instance ?: synchronized(this) {
                val newInstance = buildDatabaseInstance(dbPath) // Create instance via builder
                instance = newInstance
                newInstance
            }
        }

        private fun buildDatabaseInstance(dbPath: String): KarlRoomDatabase {
            // Room KMP Builder syntax might differ slightly. Consult documentation.
            // This is a conceptual JVM example.
            return Room.databaseBuilder(
                name = dbPath, // For JVM, 'name' is often the file path
                factory = { KarlRoomDatabase::class.instantiateImpl() }, // Factory for Room implementation
                // Add specific KMP/JVM backend configuration if needed (e.g., driver)
            )
                // REQUIRED for schema changes:
                // .addMigrations(MIGRATION_1_2, MIGRATION_2_3...)
                // Recommended for development ONLY:
                .fallbackToDestructiveMigration() // Deletes DB on version mismatch - AVOID IN PROD
                .build()
        }
        // TODO: Define your Migration objects (MIGRATION_1_2, etc.)
    }
}

// Hack for KSP/Room KMP limitation if constructor isn't public (Check Room KMP docs)
private fun KClass<KarlRoomDatabase>.instantiateImpl(): KarlRoomDatabase {
    val method = Class.forName("${qualifiedName}_Impl").getDeclaredConstructor()
    method.isAccessible = true
    return method.newInstance() as KarlRoomDatabase
}
