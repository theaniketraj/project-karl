# Module KARL Room Storage

The **KARL Room Storage** module provides a robust, production-ready data persistence layer using Android's Room database library. It implements the complete DataStorage contract with support for user data isolation, efficient querying, and transactional safety guarantees.

## Key Features

### Database Architecture

- **Entity-Based Design**: Uses Room entities to map domain models to SQL tables
- **DAO Pattern**: Leverages Data Access Objects for type-safe database operations
- **Transaction Support**: Ensures atomic operations for data consistency
- **Migration Support**: Built-in schema versioning for database evolution

### Performance Characteristics

- **Thread Safety**: All database operations dispatched to IO thread pool
- **Connection Pooling**: Leverages Room's built-in connection management
- **Query Optimization**: Uses indexed queries for efficient data retrieval
- **Memory Efficiency**: Minimal object allocation through entity reuse

### Data Isolation

User data is strictly partitioned by userId, ensuring complete privacy isolation between different users of the same application. This supports multi-user scenarios and privacy compliance requirements.

## Core Components

- **`RoomDataStorage`** - Main DataStorage implementation
- **`KarlDao`** - Data Access Object with comprehensive query methods
- **`KarlRoomDatabase`** - Database configuration and entity definitions
- **`KarlRoomDatabaseFactory`** - Platform-specific database creation

### Entity Models

- **`KarlContainerStateEntity`** - Container state persistence
- **`InteractionDataEntity`** - User interaction data logging
- **`MapConverter`** - Type converters for complex data structures

## Advanced Features

- **Temporal Queries**: Time-based filtering and analysis capabilities
- **Bulk Operations**: Efficient batch data management
- **Pagination Support**: Handle large datasets with limit parameters
- **User Data Management**: Comprehensive deletion and cleanup operations

## Coroutine Integration

All operations are implemented as suspending functions that work seamlessly with Kotlin coroutines. Database operations are automatically dispatched to the IO dispatcher to prevent blocking the main thread.

## Dependencies

- KARL Core module
- AndroidX Room (Common, Runtime, Compiler)
- Kotlinx Serialization JSON
- Kotlinx Coroutines

## Usage

```kotlin
val storage = RoomDataStorage(karlDao)
storage.initialize()
storage.saveContainerState(userId, state)
```

This module provides enterprise-grade data persistence for KARL applications.
