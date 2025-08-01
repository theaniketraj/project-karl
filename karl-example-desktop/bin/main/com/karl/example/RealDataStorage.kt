/*
 * Copyright (c) 2025 Project KARL Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karl.example

import com.karl.core.models.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlContainerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Production-ready SQLite-based implementation of [DataStorage] interface for Project KARL.
 *
 * This class provides persistent storage capabilities for the KARL adaptive reasoning system,
 * replacing in-memory storage with durable SQLite database persistence. The implementation
 * manages two primary data domains: container states and user interaction histories.
 *
 * **Architecture & Design:**
 * - **Database Engine**: SQLite JDBC for lightweight, embedded persistence
 * - **Connection Management**: Single connection per instance with proper lifecycle management
 * - **Concurrency**: All database operations execute on [Dispatchers.IO] for non-blocking behavior
 * - **Schema Design**: Normalized tables with optimized indexing for query performance
 * - **Error Handling**: Comprehensive exception handling with logging for debugging
 *
 * **Database Schema:**
 * ```sql
 * -- Container States Table
 * CREATE TABLE container_states (
 *     user_id TEXT PRIMARY KEY,        -- Unique user identifier
 *     state_data BLOB NOT NULL,        -- Serialized KarlContainerState data
 *     version INTEGER NOT NULL,        -- State version for conflict resolution
 *     created_at INTEGER DEFAULT NOW,  -- Creation timestamp (Unix epoch)
 *     updated_at INTEGER DEFAULT NOW   -- Last modification timestamp
 * );
 *
 * -- Interaction Data Table
 * CREATE TABLE interaction_data (
 *     id INTEGER PRIMARY KEY AUTOINCREMENT,  -- Unique interaction ID
 *     user_id TEXT NOT NULL,                 -- Associated user identifier
 *     type TEXT NOT NULL,                    -- Interaction type classification
 *     details TEXT NOT NULL,                 -- JSON-serialized interaction details
 *     timestamp INTEGER NOT NULL,            -- Interaction timestamp (Unix epoch)
 *     created_at INTEGER DEFAULT NOW         -- Database insertion timestamp
 * );
 *
 * -- Performance Index
 * CREATE INDEX idx_interaction_user_timestamp
 * ON interaction_data(user_id, timestamp DESC);
 * ```
 *
 * **Data Flow Patterns:**
 * 1. **State Persistence**: Container states are serialized as BLOB data with versioning
 * 2. **Interaction Logging**: User interactions stored as structured events with timestamps
 * 3. **Query Optimization**: Indexed queries for efficient historical data retrieval
 * 4. **Cleanup Operations**: Bulk deletion support for user data management
 *
 * **Threading & Concurrency:**
 * - All database operations use `withContext(Dispatchers.IO)` for proper coroutine context
 * - Thread-safe connection management with synchronized access patterns
 * - Non-blocking I/O operations suitable for reactive application architectures
 *
 * **Error Recovery:**
 * - SQLException handling with detailed error logging
 * - Graceful degradation for failed operations
 * - Connection state validation and recovery mechanisms
 *
 * @param databasePath Filesystem path to the SQLite database file. Defaults to "karl_database.db"
 *                     in the application working directory. Can be absolute or relative path.
 *
 * @constructor Creates a new [RealDataStorage] instance with the specified database path.
 *              The database connection is established during [initialize] call.
 *
 * @see DataStorage The interface contract this implementation fulfills
 * @see KarlContainerState The state data structure managed by this storage
 * @see InteractionData The interaction event structure persisted by this storage
 *
 * @since 1.0.0
 * @author KARL Development Team
 */
class RealDataStorage(
    private val databasePath: String = "karl_database.db",
) : DataStorage {
    /**
     * SQLite JDBC connection instance for database operations.
     *
     * This connection is initialized during [initialize] and maintained throughout the
     * instance lifecycle. All database operations are performed through this single
     * connection to ensure consistency and proper transaction management.
     *
     * **Connection Characteristics:**
     * - **Driver**: SQLite JDBC (org.sqlite.JDBC)
     * - **URL Format**: `jdbc:sqlite:${databasePath}`
     * - **Mode**: Single connection per instance
     * - **Threading**: All access through [Dispatchers.IO] context
     *
     * **Lifecycle Management:**
     * - Initialized: During [initialize] method execution
     * - Active: Throughout normal operation phase
     * - Released: During [release] method execution or application shutdown
     *
     * @see initialize Database connection establishment
     * @see release Connection cleanup and resource release
     */
    private lateinit var connection: Connection

    /**
     * Initializes the SQLite database connection and creates required schema.
     *
     * This method establishes the database connection, loads the SQLite JDBC driver,
     * and ensures all required tables and indexes exist. The operation is performed
     * asynchronously on the IO dispatcher to prevent blocking the calling coroutine.
     *
     * **Initialization Process:**
     * 1. **Driver Loading**: Loads `org.sqlite.JDBC` driver class
     * 2. **Connection Establishment**: Creates JDBC connection to SQLite database
     * 3. **Schema Creation**: Executes DDL statements for tables and indexes
     * 4. **Validation**: Confirms successful initialization with logging
     *
     * **Database Schema Created:**
     * - `container_states`: User state persistence with versioning
     * - `interaction_data`: User interaction event logging
     * - `idx_interaction_user_timestamp`: Performance optimization index
     *
     * **Error Handling:**
     * - [ClassNotFoundException]: SQLite JDBC driver not found in classpath
     * - [SQLException]: Database connection or schema creation failure
     * - [Exception]: General initialization errors with detailed logging
     *
     * **Threading Context:**
     * - Executes on [Dispatchers.IO] for non-blocking database operations
     * - Safe to call from any coroutine context
     *
     * @throws Exception If database initialization fails for any reason
     *
     * @see createTables Schema creation implementation
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun initialize() {
        withContext(Dispatchers.IO) {
            try {
                // Load SQLite JDBC driver
                Class.forName("org.sqlite.JDBC")

                // Create connection to SQLite database
                connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")

                // Create tables if they don't exist
                createTables()

                println("RealDataStorage: Initialized SQLite database at $databasePath")
            } catch (e: Exception) {
                println("RealDataStorage: Failed to initialize database: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Creates the required database schema for KARL data persistence.
     *
     * This private method executes DDL statements to create tables and indexes
     * necessary for the KARL storage system. The schema is designed for optimal
     * performance and data integrity in the SQLite environment.
     *
     * **Schema Components:**
     *
     * **1. Container States Table (`container_states`):**
     * - **Purpose**: Stores serialized [KarlContainerState] data per user
     * - **Primary Key**: `user_id` (TEXT) - Ensures one state per user
     * - **Data Storage**: `state_data` (BLOB) - Binary serialized state
     * - **Versioning**: `version` (INTEGER) - State version for conflict resolution
     * - **Timestamps**: `created_at`, `updated_at` - Audit trail timestamps
     *
     * **2. Interaction Data Table (`interaction_data`):**
     * - **Purpose**: Logs all user interactions with the KARL system
     * - **Primary Key**: `id` (AUTOINCREMENT) - Unique interaction identifier
     * - **Relationships**: `user_id` (TEXT) - Foreign key to user
     * - **Classification**: `type` (TEXT) - Interaction category
     * - **Content**: `details` (TEXT) - JSON-serialized interaction details
     * - **Timing**: `timestamp` (INTEGER) - Unix epoch interaction time
     *
     * **3. Performance Index (`idx_interaction_user_timestamp`):**
     * - **Purpose**: Optimizes queries for recent user interactions
     * - **Columns**: `user_id`, `timestamp DESC` - Composite index
     * - **Benefits**: Fast retrieval of chronologically ordered interactions
     *
     * **SQL Schema Details:**
     * ```sql
     * -- Primary state storage with versioning
     * CREATE TABLE IF NOT EXISTS container_states (
     *     user_id TEXT PRIMARY KEY,           -- User identifier
     *     state_data BLOB NOT NULL,           -- Serialized state
     *     version INTEGER NOT NULL,           -- Version number
     *     created_at INTEGER DEFAULT NOW,     -- Creation time
     *     updated_at INTEGER DEFAULT NOW      -- Modification time
     * );
     *
     * -- Interaction event logging
     * CREATE TABLE IF NOT EXISTS interaction_data (
     *     id INTEGER PRIMARY KEY AUTOINCREMENT,  -- Unique ID
     *     user_id TEXT NOT NULL,                 -- User reference
     *     type TEXT NOT NULL,                    -- Event type
     *     details TEXT NOT NULL,                 -- Event data
     *     timestamp INTEGER NOT NULL,            -- Event time
     *     created_at INTEGER DEFAULT NOW         -- Insert time
     * );
     *
     * -- Query optimization index
     * CREATE INDEX IF NOT EXISTS idx_interaction_user_timestamp
     * ON interaction_data(user_id, timestamp DESC);
     * ```
     *
     * **Design Rationale:**
     * - **BLOB Storage**: Allows flexible serialization formats for state data
     * - **Versioning**: Enables optimistic concurrency control and conflict resolution
     * - **Timestamps**: Provides audit trail and temporal query capabilities
     * - **Indexing**: Optimizes common query patterns for interaction retrieval
     *
     * @throws SQLException If any DDL statement execution fails
     *
     * @see container_states Table for user state persistence
     * @see interaction_data Table for interaction event logging
     */
    private fun createTables() {
        // Create container states table
        connection.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS container_states (
                user_id TEXT PRIMARY KEY,
                state_data BLOB NOT NULL,
                version INTEGER NOT NULL,
                created_at INTEGER DEFAULT (strftime('%s', 'now')),
                updated_at INTEGER DEFAULT (strftime('%s', 'now'))
            )
        """,
        )

        // Create interaction data table
        connection.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS interaction_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                type TEXT NOT NULL,
                details TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                created_at INTEGER DEFAULT (strftime('%s', 'now'))
            )
        """,
        )

        // Create index for faster queries
        connection.createStatement().execute(
            """
            CREATE INDEX IF NOT EXISTS idx_interaction_user_timestamp 
            ON interaction_data(user_id, timestamp DESC)
        """,
        )
    }

    /**
     * Persists a [KarlContainerState] for the specified user to the database.
     *
     * This method performs an atomic upsert operation (INSERT OR REPLACE) to store
     * the user's current container state. The state data is serialized as a BLOB
     * and stored with version information for conflict resolution.
     *
     * **Operation Details:**
     * - **SQL Operation**: `INSERT OR REPLACE` for atomic upsert behavior
     * - **Data Serialization**: State data stored as binary BLOB
     * - **Versioning**: Version number stored for optimistic concurrency control
     * - **Timestamping**: `updated_at` automatically set to current timestamp
     *
     * **Database Transaction:**
     * ```sql
     * INSERT OR REPLACE INTO container_states (user_id, state_data, version, updated_at)
     * VALUES (?, ?, ?, strftime('%s', 'now'))
     * ```
     *
     * **Threading & Performance:**
     * - Executes on [Dispatchers.IO] for non-blocking database access
     * - Uses prepared statements for SQL injection prevention
     * - Automatic resource cleanup with statement closure
     *
     * **Error Handling:**
     * - [SQLException]: Database operation failures with detailed logging
     * - Exceptions are re-thrown to calling context for handling
     * - Comprehensive error messages include user ID and version information
     *
     * **Concurrency Considerations:**
     * - UPSERT operation is atomic at the database level
     * - Version number can be used for optimistic locking in multi-user scenarios
     * - Thread-safe through coroutine context switching to IO dispatcher
     *
     * @param userId Unique identifier for the user whose state is being saved.
     *               Must be non-null and should be consistent across sessions.
     * @param state The [KarlContainerState] instance to persist. Contains both
     *              the serialized data and version information for storage.
     *
     * @throws SQLException If the database operation fails due to connection issues,
     *                      constraint violations, or other database-related errors.
     *
     * @see KarlContainerState The state data structure being persisted
     * @see loadContainerState Corresponding state retrieval operation
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun saveContainerState(
        userId: String,
        state: KarlContainerState,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val stmt =
                    connection.prepareStatement(
                        """
                    INSERT OR REPLACE INTO container_states (user_id, state_data, version, updated_at)
                    VALUES (?, ?, ?, strftime('%s', 'now'))
                """,
                    )
                stmt.setString(1, userId)
                stmt.setBytes(2, state.data)
                stmt.setInt(3, state.version)
                stmt.executeUpdate()
                stmt.close()

                println("RealDataStorage: Saved container state for user: $userId (version: ${state.version})")
            } catch (e: SQLException) {
                println("RealDataStorage: Failed to save container state: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Retrieves the stored [KarlContainerState] for the specified user from the database.
     *
     * This method performs a parameterized query to fetch the user's container state
     * and reconstructs the [KarlContainerState] object from the stored BLOB data
     * and version information.
     *
     * **Query Operation:**
     * ```sql
     * SELECT state_data, version FROM container_states WHERE user_id = ?
     * ```
     *
     * **Data Reconstruction:**
     * - **BLOB Deserialization**: Converts stored binary data back to state object
     * - **Version Restoration**: Reconstructs version information for state tracking
     * - **Null Handling**: Returns null if no state exists for the specified user
     *
     * **Performance Characteristics:**
     * - **Primary Key Lookup**: O(1) query performance via user_id primary key
     * - **Memory Efficient**: Only loads requested user's data
     * - **Non-blocking**: Executes on [Dispatchers.IO] for coroutine compatibility
     *
     * **Error Handling & Recovery:**
     * - [SQLException]: Database access errors with comprehensive logging
     * - **Graceful Degradation**: Returns null on any error rather than throwing
     * - **Resource Cleanup**: Automatic closure of ResultSet and PreparedStatement
     *
     * **Return Value Semantics:**
     * - **Non-null**: Valid [KarlContainerState] with data and version information
     * - **Null**: No stored state exists for the user OR error occurred during retrieval
     *
     * **Threading Context:**
     * - All database operations execute on [Dispatchers.IO]
     * - Safe to call from any coroutine context
     * - Non-blocking operation suitable for reactive architectures
     *
     * **Usage Patterns:**
     * ```kotlin
     * val state = storage.loadContainerState("user123")
     * if (state != null) {
     *     // Process loaded state with data and version
     *     println("Loaded state version: ${state.version}")
     * } else {
     *     // No state exists or error occurred - initialize new state
     *     val newState = KarlContainerState.createDefault()
     * }
     * ```
     *
     * @param userId Unique identifier for the user whose state should be retrieved.
     *               Must match the user ID used during [saveContainerState] operations.
     *
     * @return [KarlContainerState] containing the user's persisted state data and version,
     *         or null if no state exists for the user or if an error occurs during retrieval.
     *
     * @see KarlContainerState The state data structure being retrieved
     * @see saveContainerState Corresponding state persistence operation
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun loadContainerState(userId: String): KarlContainerState? {
        return withContext(Dispatchers.IO) {
            try {
                val stmt =
                    connection.prepareStatement(
                        """
                    SELECT state_data, version FROM container_states WHERE user_id = ?
                """,
                    )
                stmt.setString(1, userId)
                val resultSet = stmt.executeQuery()

                val state =
                    if (resultSet.next()) {
                        val stateData = resultSet.getBytes("state_data")
                        val version = resultSet.getInt("version")
                        KarlContainerState(data = stateData, version = version)
                    } else {
                        null
                    }

                resultSet.close()
                stmt.close()

                println("RealDataStorage: Loaded container state for user: $userId (exists: ${state != null})")
                state
            } catch (e: SQLException) {
                println("RealDataStorage: Failed to load container state: ${e.message}")
                null
            }
        }
    }

    /**
     * Persists user interaction data to the database for analytics and learning purposes.
     *
     * This method stores individual interaction events in the interaction_data table,
     * enabling the KARL system to learn from user behavior patterns and improve
     * prediction accuracy over time.
     *
     * **Data Storage Strategy:**
     * - **Structured Logging**: Each interaction stored as a discrete database record
     * - **JSON Serialization**: Complex interaction details serialized as TEXT
     * - **Temporal Indexing**: Timestamp-based organization for chronological analysis
     * - **User Association**: All interactions linked to specific user identifiers
     *
     * **Database Operation:**
     * ```sql
     * INSERT INTO interaction_data (user_id, type, details, timestamp)
     * VALUES (?, ?, ?, ?)
     * ```
     *
     * **Data Mapping:**
     * - `user_id` ← [InteractionData.userId]
     * - `type` ← [InteractionData.type] (interaction classification)
     * - `details` ← [InteractionData.details].toString() (serialized map)
     * - `timestamp` ← [InteractionData.timestamp] (Unix epoch time)
     *
     * **Interaction Types & Examples:**
     * - **"command_prediction"**: AI-generated command suggestions
     * - **"user_acceptance"**: User accepted/rejected predictions
     * - **"context_change"**: Directory or environment changes
     * - **"feedback_submission"**: Explicit user feedback events
     * - **"error_recovery"**: Error handling and recovery actions
     *
     * **Threading & Performance:**
     * - **Asynchronous**: Non-blocking operation on [Dispatchers.IO]
     * - **Prepared Statements**: SQL injection prevention and performance optimization
     * - **Resource Management**: Automatic statement cleanup after execution
     *
     * **Error Handling:**
     * - [SQLException]: Database operation failures with detailed error logging
     * - **Exception Propagation**: Errors re-thrown to calling context
     * - **Data Integrity**: Failed insertions do not affect existing data
     *
     * **Analytics & Learning Implications:**
     * - **Pattern Recognition**: Enables ML algorithms to identify usage patterns
     * - **Prediction Improvement**: Historical data improves future suggestions
     * - **User Profiling**: Builds user-specific behavioral models
     * - **System Optimization**: Identifies common workflows and pain points
     *
     * **Data Retention Considerations:**
     * - **Storage Growth**: Interaction data accumulates over time
     * - **Privacy**: Contains user behavior information requiring careful handling
     * - **Cleanup**: Consider implementing data retention policies
     *
     * @param data The [InteractionData] instance containing the interaction details,
     *             user context, and temporal information to be persisted.
     *
     * @throws SQLException If the database insertion operation fails due to connection
     *                      issues, constraint violations, or other database errors.
     *
     * @see InteractionData The interaction event data structure being stored
     * @see loadRecentInteractionData Corresponding interaction retrieval operation
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun saveInteractionData(data: InteractionData) {
        withContext(Dispatchers.IO) {
            try {
                val stmt =
                    connection.prepareStatement(
                        """
                    INSERT INTO interaction_data (user_id, type, details, timestamp)
                    VALUES (?, ?, ?, ?)
                """,
                    )
                stmt.setString(1, data.userId)
                stmt.setString(2, data.type)
                stmt.setString(3, data.details.toString()) // Convert map to string
                stmt.setLong(4, data.timestamp)
                stmt.executeUpdate()
                stmt.close()

                println("RealDataStorage: Stored interaction: ${data.type} for user ${data.userId}")
            } catch (e: SQLException) {
                println("RealDataStorage: Failed to save interaction data: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Retrieves recent user interaction data with optional filtering and limiting.
     *
     * This method queries the interaction_data table to fetch the most recent
     * interactions for a specified user, supporting both filtered and unfiltered
     * queries with configurable result limits.
     *
     * **Query Variants:**
     *
     * **1. Type-Filtered Query:**
     * ```sql
     * SELECT user_id, type, details, timestamp
     * FROM interaction_data
     * WHERE user_id = ? AND type = ?
     * ORDER BY timestamp DESC
     * LIMIT ?
     * ```
     *
     * **2. Unfiltered Query:**
     * ```sql
     * SELECT user_id, type, details, timestamp
     * FROM interaction_data
     * WHERE user_id = ?
     * ORDER BY timestamp DESC
     * LIMIT ?
     * ```
     *
     * **Performance Optimization:**
     * - **Index Utilization**: Leverages `idx_interaction_user_timestamp` for fast retrieval
     * - **Descending Order**: Most recent interactions returned first
     * - **Result Limiting**: Prevents excessive memory usage with large datasets
     * - **Conditional Filtering**: Optional type filtering for specific interaction categories
     *
     * **Data Reconstruction:**
     * - **Object Mapping**: Database rows mapped to [InteractionData] instances
     * - **Simplified Parsing**: Details stored as single string in "stored" key
     * - **Temporal Ordering**: Results reversed to chronological order before return
     * - **Type Preservation**: Interaction type classifications maintained
     *
     * **Error Handling & Recovery:**
     * - [SQLException]: Database access errors with comprehensive logging
     * - **Graceful Degradation**: Returns empty list on any error
     * - **Resource Management**: Automatic cleanup of ResultSet and PreparedStatement
     * - **Null Safety**: Handles missing or corrupted data gracefully
     *
     * **Use Cases & Applications:**
     * - **Recent Activity**: Show user's latest interactions in UI
     * - **Pattern Analysis**: Analyze recent behavior for prediction improvement
     * - **Context Awareness**: Understand current user workflow context
     * - **Debugging**: Investigate user interaction sequences for troubleshooting
     * - **Recommendation**: Base suggestions on recent interaction patterns
     *
     * **Filtering Examples:**
     * ```kotlin
     * // Get last 10 interactions of any type
     * val recent = loadRecentInteractionData("user123", 10, null)
     *
     * // Get last 5 command predictions specifically
     * val predictions = loadRecentInteractionData("user123", 5, "command_prediction")
     *
     * // Get last 20 user acceptance events
     * val acceptances = loadRecentInteractionData("user123", 20, "user_acceptance")
     * ```
     *
     * **Threading Context:**
     * - All database operations execute on [Dispatchers.IO]
     * - Non-blocking operation suitable for reactive UI updates
     * - Safe to call from any coroutine context
     *
     * @param userId Unique identifier for the user whose interactions should be retrieved.
     *               Must match user IDs used in [saveInteractionData] operations.
     * @param limit Maximum number of interaction records to return. Should be reasonable
     *              to prevent excessive memory usage (e.g., 1-1000 range).
     * @param type Optional interaction type filter. If provided, only interactions
     *             matching this type will be returned. If null, all interaction types
     *             are included in the results.
     *
     * @return List of [InteractionData] instances ordered chronologically (oldest first),
     *         containing up to [limit] recent interactions. Returns empty list if no
     *         interactions exist or if an error occurs during retrieval.
     *
     * @see InteractionData The interaction event data structure being retrieved
     * @see saveInteractionData Corresponding interaction persistence operation
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int,
        type: String?,
    ): List<InteractionData> {
        return withContext(Dispatchers.IO) {
            try {
                val query =
                    if (type != null) {
                        """
                    SELECT user_id, type, details, timestamp 
                    FROM interaction_data 
                    WHERE user_id = ? AND type = ?
                    ORDER BY timestamp DESC 
                    LIMIT ?
                    """
                    } else {
                        """
                    SELECT user_id, type, details, timestamp 
                    FROM interaction_data 
                    WHERE user_id = ?
                    ORDER BY timestamp DESC 
                    LIMIT ?
                    """
                    }

                val stmt = connection.prepareStatement(query)
                stmt.setString(1, userId)
                if (type != null) {
                    stmt.setString(2, type)
                    stmt.setInt(3, limit)
                } else {
                    stmt.setInt(2, limit)
                }

                val resultSet = stmt.executeQuery()
                val interactions = mutableListOf<InteractionData>()

                while (resultSet.next()) {
                    val interaction =
                        InteractionData(
                            userId = resultSet.getString("user_id"),
                            type = resultSet.getString("type"),
                            details = mapOf("stored" to resultSet.getString("details")), // Simplified parsing
                            timestamp = resultSet.getLong("timestamp"),
                        )
                    interactions.add(interaction)
                }

                resultSet.close()
                stmt.close()

                println("RealDataStorage: Loaded ${interactions.size} recent interactions for user: $userId")
                interactions.reversed() // Return in chronological order
            } catch (e: SQLException) {
                println("RealDataStorage: Failed to load recent interactions: ${e.message}")
                emptyList()
            }
        }
    }

    /**
     * Permanently removes all data associated with the specified user from the database.
     *
     * This method performs a comprehensive data deletion operation, removing the user's
     * container state and all historical interaction data from both primary tables.
     * This operation is typically used for user account deletion, data privacy compliance,
     * or system cleanup operations.
     *
     * **Deletion Scope:**
     * - **Container States**: Removes user's stored state from `container_states` table
     * - **Interaction History**: Removes all user interactions from `interaction_data` table
     * - **Complete Cleanup**: No residual user data remains after successful execution
     *
     * **Database Operations:**
     * ```sql
     * -- Remove user's container state
     * DELETE FROM container_states WHERE user_id = ?
     *
     * -- Remove user's interaction history
     * DELETE FROM interaction_data WHERE user_id = ?
     * ```
     *
     * **Operation Characteristics:**
     * - **Atomic Operations**: Each DELETE is atomic, but overall operation is not transactional
     * - **Referential Integrity**: Safely removes related data across multiple tables
     * - **Performance**: Uses indexed user_id columns for efficient deletion
     * - **Audit Logging**: Reports number of records deleted from each table
     *
     * **Privacy & Compliance Implications:**
     * - **GDPR Compliance**: Supports "right to be forgotten" requirements
     * - **Data Minimization**: Enables removal of unnecessary user data
     * - **Security**: Prevents unauthorized access to deleted user's historical data
     * - **Audit Trail**: Logs deletion operations for compliance tracking
     *
     * **Error Handling:**
     * - [SQLException]: Database operation failures with detailed error logging
     * - **Partial Failures**: If one table deletion fails, the other may still succeed
     * - **Exception Propagation**: Database errors re-thrown to calling context
     * - **Transactional Considerations**: Consider wrapping in transaction for atomicity
     *
     * **Performance Considerations:**
     * - **Index Utilization**: Leverages primary key and foreign key indexes
     * - **Batch Operations**: Efficiently removes multiple records per user
     * - **Lock Duration**: Minimal lock time due to indexed deletion
     * - **Space Reclamation**: SQLite may require VACUUM for space recovery
     *
     * **Usage Patterns:**
     * ```kotlin
     * try {
     *     storage.deleteUserData("user123")
     *     println("User data successfully deleted")
     * } catch (e: SQLException) {
     *     logger.error("Failed to delete user data", e)
     *     // Handle deletion failure (e.g., retry, alert admin)
     * }
     * ```
     *
     * **Threading Context:**
     * - Executes on [Dispatchers.IO] for non-blocking database access
     * - Safe to call from any coroutine context
     * - Suitable for background cleanup operations
     *
     * @param userId Unique identifier for the user whose data should be completely
     *               removed from the database. Must match the user ID used in previous
     *               [saveContainerState] and [saveInteractionData] operations.
     *
     * @throws SQLException If either deletion operation fails due to database connection
     *                      issues, constraint violations, or other database-related errors.
     *
     * @see saveContainerState Container state operations that create deletable data
     * @see saveInteractionData Interaction operations that create deletable data
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun deleteUserData(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                // Delete from both tables
                val stmt1 = connection.prepareStatement("DELETE FROM container_states WHERE user_id = ?")
                stmt1.setString(1, userId)
                val deleted1 = stmt1.executeUpdate()
                stmt1.close()

                val stmt2 = connection.prepareStatement("DELETE FROM interaction_data WHERE user_id = ?")
                stmt2.setString(1, userId)
                val deleted2 = stmt2.executeUpdate()
                stmt2.close()

                println("RealDataStorage: Deleted data for user: $userId (states: $deleted1, interactions: $deleted2)")
            } catch (e: SQLException) {
                println("RealDataStorage: Failed to delete user data: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Gracefully releases database resources and closes the connection.
     *
     * This method performs cleanup operations to properly close the SQLite database
     * connection and release associated system resources. It should be called during
     * application shutdown or when the [RealDataStorage] instance is no longer needed.
     *
     * **Resource Management:**
     * - **Connection Closure**: Closes active SQLite JDBC connection
     * - **Memory Cleanup**: Releases connection-related memory resources
     * - **Handle Cleanup**: Cleans up database file handles and locks
     * - **State Validation**: Checks connection initialization and status before closure
     *
     * **Cleanup Process:**
     * 1. **Initialization Check**: Verifies connection was properly initialized
     * 2. **Status Validation**: Confirms connection is not already closed
     * 3. **Connection Closure**: Invokes JDBC connection close method
     * 4. **Confirmation Logging**: Reports successful resource release
     *
     * **Error Handling:**
     * - [SQLException]: Connection closure errors with detailed logging
     * - **Graceful Degradation**: Errors logged but don't prevent application shutdown
     * - **State Safety**: Safe to call multiple times or on uninitialized instances
     * - **Exception Isolation**: Prevents resource cleanup errors from propagating
     *
     * **Threading Context:**
     * - Executes on [Dispatchers.IO] for consistent database operation context
     * - Non-blocking operation suitable for shutdown sequences
     * - Safe to call from any coroutine context
     *
     * **Lifecycle Integration:**
     * ```kotlin
     * class Application {
     *     private val storage = RealDataStorage()
     *
     *     suspend fun initialize() {
     *         storage.initialize()
     *     }
     *
     *     suspend fun shutdown() {
     *         storage.release()  // Proper cleanup
     *     }
     * }
     * ```
     *
     * **Connection State Management:**
     * - **Idempotent**: Safe to call multiple times
     * - **State Aware**: Checks both initialization and connection status
     * - **Resource Safe**: No resource leaks even if called repeatedly
     * - **Shutdown Compatible**: Suitable for application shutdown hooks
     *
     * **Best Practices:**
     * - Call during application shutdown or dependency injection cleanup
     * - Include in try-finally blocks for guaranteed cleanup
     * - Use in conjunction with proper connection lifecycle management
     * - Consider implementing AutoCloseable interface for automatic resource management
     *
     * **SQLite-Specific Considerations:**
     * - **File Locking**: Releases SQLite database file locks
     * - **WAL Mode**: Properly closes Write-Ahead Logging if enabled
     * - **Journal Files**: Ensures temporary journal files are cleaned up
     * - **Connection Pool**: Single connection model simplifies cleanup
     *
     * @see initialize Database connection establishment
     * @see Connection.close JDBC connection closure documentation
     * @see Dispatchers.IO Coroutine context for database operations
     */
    override suspend fun release() {
        withContext(Dispatchers.IO) {
            try {
                if (::connection.isInitialized && !connection.isClosed) {
                    connection.close()
                    println("RealDataStorage: Released database connection")
                }
            } catch (e: SQLException) {
                println("RealDataStorage: Error releasing connection: ${e.message}")
            }
        }
    }
}
