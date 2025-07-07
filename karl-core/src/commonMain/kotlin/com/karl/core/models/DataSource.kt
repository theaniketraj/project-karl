package com.karl.core.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Represents a source of user interaction data from the application.
 * The application implements this to feed data to the KarlContainer.
 * This interface resides in the data package as it's specific to providing data.
 */
interface DataSource {
    /**
     * Should be implemented by the application to provide a stream of relevant
     * InteractionData events to the KarlContainer.
     * This function might start observation and return a Job that can be cancelled.
     * The implementation should call the provided [onNewData] callback whenever
     * a new interaction occurs that KARL should potentially learn from.
     * @param onNewData A callback function provided by KarlContainer to receive new InteractionData.
     * @param coroutineScope A CoroutineScope for launching observation tasks.
     * @return A Job representing the running observation task. Cancelling this job stops the data stream.
     */
    fun observeInteractionData(
        onNewData: suspend (InteractionData) -> Unit,
        coroutineScope: CoroutineScope,
    ): Job
}
