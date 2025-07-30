// karl-project/karl-room/src/commonMain/kotlin/com/karl/room/KarlRoomDatabase.kt
package com.karl.room

// Removed original Room imports and using local stubs
// import androidx.room.Database
// import androidx.room.RoomDatabase
// import androidx.room.TypeConverters

// Using stub annotations instead of real Room annotations until Room KMP is properly configured
// @Database(
//     entities = [
//         InteractionDataEntity::class,
//         KarlContainerStateEntity::class,
//     ],
//     version = 1, // Start with version 1. Increment when schema changes.
//     exportSchema = true, // Recommended: Exports schema to specified location for migrations
// )
// @TypeConverters(MapConverter::class) // Register type converters for the DB
abstract class KarlRoomDatabase : RoomDatabase() {
    // Abstract method to get the DAO instance
    abstract fun karlDao(): KarlDao
}
