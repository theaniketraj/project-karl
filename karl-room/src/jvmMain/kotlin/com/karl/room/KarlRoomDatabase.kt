// karl-project/karl-room/src/jvmMain/kotlin/com/karl/room/KarlRoomDatabase.kt
package com.karl.room

import androidx.room.*
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlContainerStateEntity

// --- Type Converters (Needed for complex types like Map) ---
// Define these in a separate file or here if simple
object MapConverter {
    @TypeConverter
    fun fromString(value: String?): Map<String, Any>? {
        // Implement conversion FROM String (e.g., JSON) TO Map
        // Example using a simple format (NOT robust): key1=value1;key2=value2
        // You SHOULD use a proper serialization library like kotlinx.serialization or Gson
        if (value == null) return null
        return try {
            value.split(';').associate {
                val (k, v) = it.split('=', limit = 2)
                k to v // Store all values as String for this simple example
            }
        } catch (e: Exception) { null } // Handle parsing errors
    }

    @TypeConverter
    fun toString(map: Map<String, Any>?): String? {
        // Implement conversion FROM Map TO String (e.g., JSON)
        // Example using a simple format (NOT robust): key1=value1;key2=value2
        // You SHOULD use a proper serialization library like kotlinx.serialization or Gson
        return map?.map { "${it.key}=${it.value}" }?.joinToString(";")
    }
}
// --- End Type Converters ---


@Database(
    entities = [
        InteractionData::class,
        KarlContainerStateEntity::class
    ],
    version = 1, // Start with version 1. Increment when schema changes.
    exportSchema = true // Recommended: Exports schema to specified location for migrations
)
@TypeConverters(MapConverter::class) // Register type converters for the DB
abstract class KarlRoomDatabase : RoomDatabase() {

    // Abstract method to get the DAO instance
    abstract fun karlDao(): KarlDao

    // Companion object for singleton instance creation (optional but common)
    companion object {
        @Volatile private var INSTANCE: KarlRoomDatabase? = null

        fun getDatabase(
            // Context/Driver setup will differ for KMP Desktop vs Android
            // For Desktop, you typically provide the DB file path or driver
            dbPath: String // e.g., "path/to/karl_data.db"
            // Potentially pass driver factory if needed by Room KMP Desktop setup
        ): KarlRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabaseInstance(dbPath) // Create instance via builder
                INSTANCE = instance
                instance
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