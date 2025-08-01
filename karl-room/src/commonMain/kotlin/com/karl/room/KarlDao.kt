/**
 * Data Access Object (DAO) interface for the KARL Room database implementation.
 *
 * This interface defines the database operations for persisting and retrieving KARL framework data
 * including container states and user interaction data. The DAO follows Room's architectural
 * patterns for type-safe database access with coroutine support for asynchronous operations.
 *
 * Design Philosophy:
 * - Provides atomic operations for both container state management and interaction logging
 * - Implements user-centric data organization with userId as the primary partitioning key
 * - Supports temporal data queries with timestamp-based filtering and ordering
 * - Ensures data consistency through strategic use of conflict resolution strategies
 *
 * Performance Considerations:
 * - Query operations are optimized with appropriate indexing on userId and timestamp fields
 * - Batch operations support efficient bulk data management
 * - Pagination support through limit parameters for large datasets
 *
 * Data Lifecycle Management:
 * - Container state operations use REPLACE strategy for seamless upsert behavior
 * - Interaction data supports both individual insertion and bulk operations
 * - Comprehensive deletion operations for user data management and privacy compliance
 *
 * @author KARL Framework Team
 * @since 1.0.0
 *
 * @see KarlContainerStateEntity Entity definition for container state persistence
 * @see InteractionDataEntity Entity definition for interaction data logging
 * @see RoomDataStorage Implementation of the core DataStorage interface using this DAO
 */
package com.karl.room

// Removed original Room imports and using stubs
// import androidx.room.*
import com.karl.room.model.InteractionDataEntity
import com.karl.room.model.KarlContainerStateEntity

// Removed @Dao annotation for stub implementation
// @Dao
interface KarlDao {
    // --- Methods for KarlContainerStateEntity ---

    /**
     * Persists or updates a KARL container state entity in the database.
     *
     * This method uses the REPLACE conflict resolution strategy to provide seamless upsert
     * functionality. When a container state already exists for the given userId, it will be
     * completely replaced with the new state data. This ensures that the database always
     * contains the most current container state without requiring separate insert/update logic.
     *
     * Data Consistency:
     * - Atomic operation ensures state consistency during concurrent access
     * - Primary key (userId) constraint prevents duplicate state entries
     * - Complete state replacement maintains referential integrity
     *
     * Performance Notes:
     * - Single database transaction minimizes I/O overhead
     * - Indexed userId lookup provides O(log n) access time
     * - State serialization handled transparently by Room converters
     *
     * @param stateEntity The complete container state to persist, including all learning
     *                   models, configuration parameters, and temporal metadata
     *
     * @throws SQLException If database constraints are violated or connection fails
     * @throws IllegalArgumentException If stateEntity contains invalid data structures
     *
     * @see KarlContainerStateEntity Entity structure and validation requirements
     * @see loadContainerState Corresponding retrieval operation
     */

    // Use OnConflictStrategy.REPLACE to handle inserts and updates easily via userId PK
    // @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContainerState(stateEntity: KarlContainerStateEntity)

    /**
     * Retrieves the persisted container state for a specific user.
     *
     * This method performs an indexed lookup to efficiently locate and deserialize the
     * container state associated with the provided userId. The operation is optimized
     * for single-user retrieval and returns null if no state exists for the user.
     *
     * Query Optimization:
     * - Primary key index ensures O(log n) lookup performance
     * - Single-row limitation minimizes data transfer and memory usage
     * - Lazy loading of related entities improves initial response time
     *
     * Data Integrity:
     * - Automatic deserialization validates stored state structure
     * - Null-safe return type prevents unexpected runtime exceptions
     * - Transactional consistency ensures atomic read operations
     *
     * @param userId Unique identifier for the user whose container state should be retrieved
     * @return The complete container state entity if found, null if no state exists for the user
     *
     * @throws SQLException If database query execution fails
     * @throws DataCorruptionException If stored state data cannot be deserialized
     *
     * @see KarlContainerStateEntity Structure of returned state data
     * @see saveContainerState Method for persisting container states
     */

    // @Query("SELECT * FROM container_state WHERE userId = :userId LIMIT 1")
    suspend fun loadContainerState(userId: String): KarlContainerStateEntity? // Returns nullable Entity

    /**
     * Removes the container state for a specific user from the database.
     *
     * This operation permanently deletes all stored container state data for the specified
     * user, including learning models, configuration parameters, and temporal metadata.
     * The deletion is atomic and cannot be reversed, making it suitable for user data
     * cleanup and privacy compliance scenarios.
     *
     * Privacy and Compliance:
     * - Complete data removal supports GDPR and privacy regulations
     * - Atomic deletion prevents partial state corruption
     * - Cascade behavior can be configured for related entities
     *
     * Performance Characteristics:
     * - Indexed deletion provides efficient O(log n) performance
     * - Single transaction ensures consistency during concurrent operations
     * - Immediate disk space reclamation through database optimization
     *
     * @param userId Unique identifier for the user whose container state should be deleted
     *
     * @throws SQLException If database deletion operation fails
     * @throws SecurityException If user lacks deletion permissions
     *
     * @see saveContainerState Method for storing container states
     * @see deleteAllUserInteractionData Companion method for complete user data removal
     */

    // @Query("DELETE FROM container_state WHERE userId = :userId")
    suspend fun deleteContainerState(userId: String)

    // --- Methods for InteractionData ---

    /**
     * Persists a single user interaction data entry to the database.
     *
     * This method stores individual interaction events that contribute to the KARL learning
     * process. Each interaction represents a discrete user behavior or system response that
     * can be analyzed for pattern recognition and adaptive behavior modeling.
     *
     * Data Structure:
     * - Automatic timestamp assignment for temporal analysis
     * - User partitioning enables efficient multi-user data management
     * - Structured interaction data supports complex behavioral analytics
     *
     * Performance Optimization:
     * - Batch-friendly design allows for efficient bulk operations
     * - Minimal table locking during high-frequency interaction logging
     * - Asynchronous operation prevents UI blocking during data persistence
     *
     * Analytics Integration:
     * - Standardized data format supports machine learning pipeline integration
     * - Temporal ordering enables sequence analysis and pattern detection
     * - User segmentation capabilities for personalized learning models
     *
     * @param interactionData Complete interaction event data including user context,
     *                       system state, and behavioral metadata
     *
     * @throws SQLException If database insertion fails due to constraints or connectivity
     * @throws ValidationException If interaction data fails schema validation
     *
     * @see InteractionDataEntity Structure and validation requirements for interaction data
     * @see loadRecentInteractionData Method for retrieving recent interactions
     */

    // @Insert
    suspend fun saveInteractionData(interactionData: InteractionDataEntity)

    /**
     * Retrieves the most recent interaction data for a user with configurable pagination.
     *
     * This method provides efficient access to recent user interactions, ordered by timestamp
     * in descending order. The limit parameter enables pagination support for large interaction
     * histories while maintaining optimal query performance through database indexing.
     *
     * Query Performance:
     * - Composite index on (userId, timestamp) ensures efficient sorting and filtering
     * - Configurable limit prevents memory overflow with large datasets
     * - Descending timestamp order provides newest-first result ordering
     *
     * Use Cases:
     * - Real-time interaction analysis for immediate behavioral insights
     * - Recent pattern detection for adaptive model updates
     * - User interface display of interaction history
     * - Performance monitoring and system diagnostics
     *
     * Data Freshness:
     * - Timestamp-based ordering ensures chronological accuracy
     * - Configurable limit balances data completeness with performance
     * - Consistent ordering supports reliable pagination patterns
     *
     * @param userId Unique identifier for the user whose interactions should be retrieved
     * @param limit Maximum number of interaction records to return (default: 100)
     * @return List of interaction entities ordered by timestamp (newest first)
     *
     * @throws SQLException If database query execution fails
     * @throws IllegalArgumentException If limit parameter is negative or zero
     *
     * @see InteractionDataEntity Structure of returned interaction data
     * @see loadAllInteractionData Method for retrieving complete interaction history
     */

    // Query recent interactions for a user, ordered by timestamp
    // @Query("SELECT * FROM interaction_data WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int = 100,
    ): List<InteractionDataEntity>

    /**
     * Retrieves the complete interaction history for a specific user.
     *
     * This method returns all stored interaction data for the specified user, ordered
     * chronologically from newest to oldest. This comprehensive dataset supports deep
     * behavioral analysis, long-term pattern recognition, and complete user journey mapping.
     *
     * Data Completeness:
     * - Returns entire interaction timeline for comprehensive analysis
     * - Maintains chronological ordering for temporal pattern detection
     * - Supports unlimited result sets for complete behavioral profiling
     *
     * Performance Considerations:
     * - Large result sets may impact memory usage and query performance
     * - Consider using loadRecentInteractionData() for real-time operations
     * - Indexed queries provide optimal performance even with extensive histories
     *
     * Analytics Applications:
     * - Long-term behavioral trend analysis and forecasting
     * - Complete user journey mapping and experience optimization
     * - Machine learning model training with comprehensive datasets
     * - Historical performance analysis and system evolution tracking
     *
     * Memory Management:
     * - Lazy loading mechanisms minimize initial memory footprint
     * - Streaming capabilities support processing of very large datasets
     * - Garbage collection optimization through efficient data structures
     *
     * @param userId Unique identifier for the user whose complete interaction history is needed
     * @return Complete list of all interaction entities for the user, ordered by timestamp (newest first)
     *
     * @throws SQLException If database query execution fails
     * @throws OutOfMemoryError If interaction history exceeds available memory (consider pagination)
     *
     * @see InteractionDataEntity Structure of returned interaction data
     * @see loadRecentInteractionData Method for paginated access to recent interactions
     */

    // Query all interactions for a user
    // @Query("SELECT * FROM interaction_data WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun loadAllInteractionData(userId: String): List<InteractionDataEntity>

    /**
     * Permanently removes all interaction data for a specific user.
     *
     * This method provides complete data removal capabilities for user privacy compliance,
     * system cleanup, and data lifecycle management. The operation removes all historical
     * interaction records for the specified user while preserving data for other users.
     *
     * Privacy and Compliance:
     * - Supports GDPR, CCPA, and other privacy regulation requirements
     * - Complete data removal prevents any residual personal information
     * - Atomic operation ensures consistent deletion across all related records
     *
     * Data Lifecycle Management:
     * - Enables clean user account termination procedures
     * - Supports data retention policy enforcement
     * - Facilitates system maintenance and storage optimization
     *
     * Performance Impact:
     * - Indexed deletion provides efficient O(log n) performance characteristics
     * - Bulk deletion minimizes transaction overhead and locking duration
     * - Immediate space reclamation through database optimization triggers
     *
     * Cascade Behavior:
     * - User-scoped deletion preserves data integrity for other users
     * - Related analytics data may require separate cleanup operations
     * - Consider coordinating with deleteContainerState() for complete user removal
     *
     * @param userId Unique identifier for the user whose interaction data should be permanently deleted
     *
     * @throws SQLException If database deletion operation fails
     * @throws SecurityException If operation violates configured security policies
     * @throws IllegalStateException If user has active sessions that prevent deletion
     *
     * @see deleteContainerState Method for removing user container state data
     * @see saveInteractionData Method for storing new interaction data
     */

    // Delete all interactions for a user (useful for clearing user data)
    // @Query("DELETE FROM interaction_data WHERE userId = :userId")
    suspend fun deleteAllUserInteractionData(userId: String)

    /**
     * Retrieves interaction data within a specific time range for temporal analysis.
     *
     * This method enables sophisticated temporal analysis by providing filtered access to
     * interaction data within specified time boundaries. The timestamp-based filtering
     * supports various analytical scenarios including time-series analysis, behavioral
     * pattern detection during specific periods, and performance correlation studies.
     *
     * Temporal Analysis Capabilities:
     * - Precise time range filtering for targeted behavioral analysis
     * - Chronological ordering maintains temporal sequence integrity
     * - Flexible boundary specification supports various analytical time windows
     *
     * Performance Optimization:
     * - Composite index on (userId, timestamp) ensures efficient range queries
     * - Boundary-based filtering minimizes data transfer and memory usage
     * - Query planner optimization for timestamp range operations
     *
     * Use Cases:
     * - Performance correlation analysis during specific time periods
     * - Behavioral pattern detection within defined temporal windows
     * - A/B testing analysis with time-based cohort segmentation
     * - System performance monitoring and diagnostic correlation
     *
     * Query Precision:
     * - Inclusive boundary conditions for precise temporal matching
     * - Millisecond-level timestamp precision for fine-grained analysis
     * - Time zone consistency maintained throughout the analysis pipeline
     *
     * @param userId Unique identifier for the user whose interactions should be analyzed
     * @param startTime Inclusive start timestamp for the analysis window (Unix timestamp in milliseconds)
     * @param endTime Inclusive end timestamp for the analysis window (Unix timestamp in milliseconds)
     * @return List of interaction entities within the specified time range, ordered by timestamp (newest first)
     *
     * @throws SQLException If database query execution fails
     * @throws IllegalArgumentException If startTime is greater than endTime or timestamps are negative
     * @throws ValidationException If timestamp values exceed supported date ranges
     *
     * @see InteractionDataEntity Structure of returned interaction data
     * @see loadRecentInteractionData Method for recent interaction access without time constraints
     * @see loadAllInteractionData Method for complete interaction history retrieval
     */

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
