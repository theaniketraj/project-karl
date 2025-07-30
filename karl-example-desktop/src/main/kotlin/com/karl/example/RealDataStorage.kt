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
 * Real DataStorage implementation using SQLite JDBC
 * This replaces the InMemoryDataStorage stub with persistent storage
 */
class RealDataStorage(
    private val databasePath: String = "karl_database.db",
) : DataStorage {
    private lateinit var connection: Connection

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
