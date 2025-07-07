package com.karl.core.models

/**
 * Represents the persistent storage for KarlContainer state and user metadata.
 * Implementations will wrap specific storage solutions (like SQLDelight or Room).
 * This interface resides in the data package as it's specific to data persistence.
 */
interface DataStorage {
    /**
     * Initializes the storage, potentially setting up databases/tables.
     */
    suspend fun initialize()

    /**
     * Saves the current state of a KarlContainer.
     * @param userId The ID of the user whose state is being saved.
     * @param state The KarlContainerState to save.
     */
    suspend fun saveContainerState(
        userId: String,
        state: KarlContainerState,
    )

    /**
     * Loads the state of a KarlContainer for a user.
     * @param userId The ID of the user whose state to load.
     * @return The loaded KarlContainerState, or null if no state exists.
     */
    suspend fun loadContainerState(userId: String): KarlContainerState?

    /**
     * Saves user interaction data.
     * @param data The InteractionData to save.
     */
    suspend fun saveInteractionData(data: InteractionData)

    /**
     * Loads recent interaction data for a user, optionally filtered by type.
     * Useful for providing context to the LearningEngine for prediction.
     * @param userId The ID of the user.
     * @param limit The maximum number of interactions to load.
     * @param type Optional type to filter by.
     * @return A list of recent InteractionData.
     */
    suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int,
        type: String? = null,
    ): List<InteractionData>

    /**
     * Deletes all stored data for a specific user.
     * @param userId The ID of the user.
     */
    suspend fun deleteUserData(userId: String)

    /**
     * Releases any resources held by the storage.
     */
    suspend fun release()
}
