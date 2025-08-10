/*
 * Copyright (c) 2025 Project KARL Contributors
 * Licensed under the Apache License, Version 2.0
 *
 * This file defines the core data models and structures used throughout the KARL
 * (Kotlin Adaptive Reasoning Learner) system. These models represent the fundamental
 * data types for user interactions, system state, predictions, and user instructions
 * that flow between different components of the KARL architecture.
 *
 * Design principles:
 * - **Immutability**: All data classes are immutable to ensure thread safety
 * - **Serialization support**: Structures are designed for efficient serialization/deserialization
 * - **Version compatibility**: Include versioning for backward compatibility and migration
 * - **Type safety**: Use strong typing to prevent data corruption and runtime errors
 * - **Privacy by design**: No sensitive user data in core data structures
 *
 * Data flow patterns:
 * 1. InteractionData: Captures user behavior events from applications
 * 2. KarlContainerState: Persists learning model state across sessions
 * 3. Prediction: Delivers AI insights and suggestions to applications
 * 4. KarlInstruction: Customizes system behavior based on user preferences
 *
 * These models form the data contracts between different KARL components and
 * enable loose coupling between learning engines, storage systems, and applications.
 */
package com.karl.core.models

/**
 * Represents a single user interaction event captured from an application.
 *
 * InteractionData serves as the fundamental unit of information that flows from
 * applications into the KARL learning system. Each instance captures a discrete
 * user action, behavior, or context change that can be learned from to improve
 * future predictions and recommendations.
 *
 * Data structure principles:
 * - **Standardized format**: Consistent structure across different interaction types
 * - **Rich context**: Sufficient detail for meaningful pattern recognition
 * - **Privacy protection**: No personally identifiable information in raw data
 * - **Extensible design**: Details map allows for domain-specific information
 * - **Temporal tracking**: Precise timestamps enable time-series analysis
 *
 * Common interaction types and examples:
 *
 * **UI Interactions**:
 * - "button_click": User selects specific UI elements or controls
 * - "menu_selection": Navigation through application menus and options
 * - "form_submission": Completion of data entry or configuration forms
 * - "scroll_behavior": Reading patterns and content consumption habits
 *
 * **Content Interactions**:
 * - "document_view": Access to files, articles, or media content
 * - "search_query": Information seeking and discovery behaviors
 * - "filter_application": Data filtering and refinement preferences
 * - "content_creation": User-generated content and editing activities
 *
 * **Navigation Patterns**:
 * - "page_transition": Movement between different application sections
 * - "workflow_step": Progress through multi-step processes or tasks
 * - "session_start": Beginning of user interaction sessions
 * - "session_end": Conclusion of user activities with outcome tracking
 *
 * **Context Events**:
 * - "error_occurrence": Error conditions and user recovery actions
 * - "performance_metric": System responsiveness and user satisfaction indicators
 * - "preference_change": User customization and setting modifications
 * - "feature_discovery": First-time use of application features or capabilities
 *
 * Details map structure and content guidelines:
 *
 * **Contextual information**:
 * - "session_id": Unique identifier for grouping related interactions
 * - "screen_name": Current application screen or context
 * - "feature_area": Functional domain or module where interaction occurred
 * - "user_goal": Inferred or declared user objective for the interaction
 *
 * **Interaction specifics**:
 * - "element_id": Specific UI element or content identifier
 * - "action_result": Success, failure, or outcome of the interaction
 * - "duration": Time spent on the interaction or associated task
 * - "effort_level": Complexity or difficulty of the interaction
 *
 * **Environmental factors**:
 * - "device_type": Platform or device characteristics
 * - "network_status": Connectivity and performance conditions
 * - "time_context": Time of day, day of week, or seasonal factors
 * - "location_context": Geographic or spatial context when relevant
 *
 * **Performance and quality metrics**:
 * - "response_time": System responsiveness for the interaction
 * - "accuracy": Correctness or precision of user actions
 * - "completion_status": Whether the interaction achieved its intended goal
 * - "user_satisfaction": Explicit or implicit satisfaction indicators
 *
 * Privacy and security considerations:
 * - Never include passwords, personal identifiers, or sensitive content
 * - Use anonymized or hashed identifiers for external references
 * - Apply data minimization principles to include only learning-relevant information
 * - Implement automatic expiration for temporary or session-specific data
 *
 * Data quality and validation:
 * - Ensure consistent timestamp formats and timezone handling
 * - Validate interaction types against known vocabularies
 * - Implement schema validation for details map structure
 * - Monitor data volume and implement sampling for high-frequency events
 *
 * @property type A standardized string identifier that categorizes the interaction.
 *               Should follow a consistent naming convention and be drawn from a
 *               well-defined vocabulary to enable effective pattern recognition.
 *               Examples: "button_click", "document_view", "search_query"
 *
 * @property details A flexible map containing interaction-specific information and
 *                  context. Keys should be standardized within interaction types,
 *                  and values should be serializable primitive types or collections.
 *                  Use nested maps for complex structured data.
 *
 * @property timestamp Unix timestamp in milliseconds representing when the interaction
 *                    occurred. Should be as precise as possible and use UTC timezone
 *                    to ensure consistent temporal analysis across different locales.
 *
 * @property userId The unique identifier for the user who performed the interaction.
 *                 Must match the userId associated with the KarlContainer to ensure
 *                 proper data isolation and privacy protection.
 */
data class InteractionData(
    val type: String,
    val details: Map<String, Any>,
    val timestamp: Long,
    val userId: String,
)

/**
 * Represents the complete serialized state of a KARL container for persistence and recovery.
 *
 * KarlContainerState encapsulates all information necessary to restore a KARL container
 * to its exact previous state across application sessions. This includes learned model
 * parameters, training history, user preferences, and system configuration that enables
 * seamless continuity of AI behavior and personalization.
 *
 * State composition and structure:
 *
 * **Learning model data**:
 * - Neural network weights and biases for all layers
 * - Hyperparameters and learning algorithm configuration
 * - Training progress metrics and convergence indicators
 * - Feature engineering parameters and normalization settings
 *
 * **Historical learning context**:
 * - Interaction processing statistics and learning milestones
 * - Pattern recognition accuracy and performance metrics
 * - Adaptation history for concept drift and behavior changes
 * - Error rates and model reliability indicators
 *
 * **User customization data**:
 * - Applied instructions and preference settings
 * - Privacy controls and data usage restrictions
 * - Personalization parameters and user-specific adaptations
 * - Custom vocabularies and domain-specific configurations
 *
 * **System metadata**:
 * - Container creation and last update timestamps
 * - Component version information and compatibility markers
 * - Data integrity checksums and validation information
 * - Performance optimization settings and resource allocations
 *
 * Serialization strategy and format:
 *
 * **Binary format considerations**:
 * - Compact binary encoding for efficient storage and network transfer
 * - Platform-independent serialization for cross-device compatibility
 * - Compression support to minimize storage requirements
 * - Encryption compatibility for privacy-sensitive deployments
 *
 * **Version management**:
 * - Forward compatibility: Newer versions can read older state formats
 * - Graceful degradation: Missing features handled transparently
 * - Migration support: Automatic upgrade of legacy state formats
 * - Validation: Integrity checks prevent corrupted state loading
 *
 * **Performance optimization**:
 * - Incremental serialization for large models to reduce save time
 * - Lazy loading support for partial state restoration scenarios
 * - Compression algorithms optimized for model parameter distributions
 * - Parallel serialization for multi-core performance improvements
 *
 * State lifecycle and management:
 *
 * **Creation and updates**:
 * - Generated automatically by learning engines during training
 * - Updated incrementally to capture learning progress
 * - Triggered by milestone events or periodic schedules
 * - Coordinated with user preference changes and instruction updates
 *
 * **Storage and retrieval**:
 * - Persisted by DataStorage implementations with appropriate security
 * - Retrieved during container initialization for state restoration
 * - Cached for performance during frequent save/load operations
 * - Backed up for disaster recovery and data protection scenarios
 *
 * **Validation and integrity**:
 * - Checksum validation to detect data corruption
 * - Version compatibility checking before state restoration
 * - Schema validation for structural integrity
 * - Error recovery procedures for partially corrupted state
 *
 * Thread safety and concurrency:
 * - Immutable structure ensures thread-safe access
 * - Atomic updates prevent partial state corruption
 * - Copy-on-write semantics for efficient cloning
 * - Coordinated access during concurrent save/load operations
 *
 * Privacy and security implications:
 * - Contains learned behavioral patterns but no raw user data
 * - Requires encryption for sensitive deployment scenarios
 * - Subject to data retention policies and automatic expiration
 * - Supports secure deletion for privacy compliance requirements
 *
 * Migration and compatibility strategy:
 * When version changes require state format updates, the system should:
 * 1. Attempt to migrate existing state to the new format
 * 2. Preserve as much learning progress as possible during migration
 * 3. Fall back to fresh initialization if migration fails
 * 4. Log migration events for debugging and user notification
 *
 * @property data The serialized container state as a binary array. Contains all
 *               learning model parameters, training history, user preferences,
 *               and metadata required for complete state restoration. Format
 *               is determined by the specific learning engine implementation.
 *
 * @property version The state format version number used for compatibility checking
 *                  and migration support. Higher version numbers represent newer
 *                  formats with potentially enhanced capabilities or different
 *                  serialization strategies.
 */
data class KarlContainerState(
    val data: ByteArray,
    val version: Int = 1,
) {
    /**
     * Implements proper equality comparison for ByteArray content.
     *
     * The default ByteArray.equals() method compares object references rather than
     * content, which would cause identical state data to be considered different.
     * This implementation ensures that two KarlContainerState instances with
     * identical data and version are considered equal.
     *
     * @param other The object to compare with this KarlContainerState
     * @return true if the other object is a KarlContainerState with identical
     *         data content and version number
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as KarlContainerState
        if (!data.contentEquals(other.data)) return false
        if (version != other.version) return false
        return true
    }

    /**
     * Implements proper hash code calculation for ByteArray content.
     *
     * This implementation ensures that two KarlContainerState instances with
     * identical content produce the same hash code, which is required for
     * proper behavior in hash-based collections and equality comparisons.
     *
     * @return A hash code based on the content of the data array and version number
     */
    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + version
        return result
    }
}

/**
 * Represents an AI-generated prediction, suggestion, or recommendation from the KARL system.
 *
 * Prediction objects are the primary output of the KARL learning system, delivering
 * intelligent insights and suggestions based on learned user behavior patterns.
 * They provide structured, actionable information that applications can use to
 * enhance user experience through personalized recommendations and proactive assistance.
 *
 * Prediction structure and components:
 *
 * **Core prediction content**:
 * - Primary suggestion or recommendation text
 * - Confidence level indicating prediction reliability
 * - Type classification for appropriate handling and presentation
 * - Supporting metadata for context and explanation
 * - Alternative options for user choice and diversity
 *
 * **Confidence scoring and interpretation**:
 * - 0.0 - 0.3: Low confidence, experimental or exploratory suggestions
 * - 0.3 - 0.7: Moderate confidence, plausible suggestions with some uncertainty
 * - 0.7 - 0.9: High confidence, well-supported suggestions based on clear patterns
 * - 0.9 - 1.0: Very high confidence, highly likely suggestions with strong evidence
 *
 * **Prediction types and usage patterns**:
 *
 * **Action suggestions** ("action"):
 * - Next likely user actions based on current context
 * - Workflow optimization and shortcut recommendations
 * - Error prevention and recovery suggestions
 * - Efficiency improvements and automation opportunities
 *
 * **Content recommendations** ("content"):
 * - Relevant documents, articles, or media based on interests
 * - Related items and discovery suggestions
 * - Personalized content filtering and prioritization
 * - Cross-reference and citation recommendations
 *
 * **Navigation assistance** ("navigation"):
 * - Optimal paths through complex interfaces or workflows
 * - Frequently accessed features and shortcuts
 * - Context-aware menu and option highlighting
 * - Progressive disclosure based on user expertise level
 *
 * **Contextual insights** ("insight"):
 * - Pattern recognition and behavior analysis feedback
 * - Performance metrics and improvement opportunities
 * - Learning progress and skill development indicators
 * - Comparative analysis and benchmarking information
 *
 * **Adaptive customization** ("customization"):
 * - Interface layout and feature arrangement suggestions
 * - Preference and setting optimization recommendations
 * - Accessibility and usability improvements
 * - Personalization based on usage patterns and preferences
 *
 * Metadata structure and information:
 *
 * **Explanation and reasoning**:
 * - "rationale": Human-readable explanation of why this prediction was generated
 * - "evidence": Supporting data points or patterns that inform the prediction
 * - "pattern_strength": Statistical strength of the underlying behavioral pattern
 * - "learning_maturity": How much training data supports this type of prediction
 *
 * **Context and relevance**:
 * - "context_match": How well current context matches prediction training scenarios
 * - "temporal_relevance": Time-based factors affecting prediction accuracy
 * - "user_segment": User behavior classification that informs the prediction
 * - "domain_specificity": How specialized or general the prediction domain is
 *
 * **Presentation and interaction**:
 * - "urgency": Time sensitivity of the prediction or recommendation
 * - "presentation_style": Suggested UI treatment (notification, inline, modal, etc.)
 * - "interaction_mode": How user should engage with the prediction
 * - "dismissible": Whether the prediction can be ignored or permanently dismissed
 *
 * **Quality and validation**:
 * - "source_interactions": Number of interactions supporting the prediction
 * - "validation_score": Cross-validation or holdout testing performance
 * - "novelty": Whether this represents a new pattern or established behavior
 * - "risk_assessment": Potential negative consequences of following the prediction
 *
 * Alternative suggestions and diversity:
 *
 * **Diversity mechanisms**:
 * - Provide multiple options to avoid prediction tunnel vision
 * - Include contrarian or minority pattern suggestions
 * - Balance between exploitation of known patterns and exploration of new behaviors
 * - Ensure alternatives span different categories and approaches
 *
 * **Ranking and prioritization**:
 * - Order alternatives by confidence, relevance, or user preference
 * - Include confidence scores for each alternative when available
 * - Consider user's historical acceptance patterns for similar suggestions
 * - Balance familiar options with novel exploration opportunities
 *
 * Quality assurance and validation:
 *
 * **Prediction filtering**:
 * - Minimum confidence thresholds based on user preferences
 * - Content appropriateness and safety filtering
 * - Relevance scoring based on current context and user goals
 * - Duplicate detection and consolidation
 *
 * **Continuous improvement**:
 * - Track prediction acceptance and rejection rates
 * - Monitor long-term outcomes of followed predictions
 * - Adapt confidence calibration based on real-world performance
 * - Learn from user feedback and correction signals
 *
 * Error handling and edge cases:
 * - Graceful handling of predictions that become invalid due to context changes
 * - Clear communication when predictions are uncertain or experimental
 * - Fallback suggestions when primary predictions are not applicable
 * - Error recovery assistance when predictions lead to undesired outcomes
 *
 * @property content The primary recommendation or suggestion text that should be
 *                  presented to the user. Should be clear, actionable, and
 *                  appropriate for the current context. May include structured
 *                  data or markup for rich presentation.
 *
 * @property confidence A floating-point value between 0.0 and 1.0 indicating the
 *                     system's confidence in the prediction accuracy. Higher values
 *                     represent greater certainty based on stronger behavioral patterns
 *                     and more comprehensive training data.
 *
 * @property type A categorical identifier that describes the nature and purpose of
 *               the prediction. Common types include "action", "content", "navigation",
 *               "insight", and "customization". Used for appropriate handling and
 *               presentation by consuming applications.
 *
 * @property metadata Optional map containing additional context, explanation, and
 *                   presentation information for the prediction. Keys should follow
 *                   established conventions and values should be serializable types.
 *                   Used for rich presentation and debugging purposes.
 *
 * @property alternatives Optional list of alternative suggestions that provide user
 *                       choice and prevent prediction tunnel vision. Should be ordered
 *                       by relevance or confidence and represent diverse approaches
 *                       to achieving the user's inferred goals.
 */
data class Prediction(
    val content: String,
    val confidence: Float,
    val type: String,
    val metadata: Map<String, Any>? = emptyMap(),
    val alternatives: List<String>? = null,
)

/**
 * Represents user-defined instructions that customize KARL's learning and prediction behavior.
 *
 * KarlInstruction provides a mechanism for users and applications to influence how the KARL
 * system processes data, generates predictions, and adapts its behavior. Instructions act as
 * high-level constraints and preferences that guide the AI without requiring deep knowledge
 * of machine learning algorithms or implementation details.
 *
 * Instruction architecture and design:
 *
 * **Sealed class hierarchy**:
 * - Provides type safety and compile-time validation for instruction types
 * - Enables exhaustive pattern matching for instruction processing
 * - Allows for easy extension with new instruction types
 * - Ensures consistent instruction handling across different components
 *
 * **Instruction categories and purposes**:
 *
 * **Data processing instructions**:
 * - Control which types of interactions are included in learning
 * - Specify data preprocessing and filtering preferences
 * - Define privacy constraints and data retention policies
 * - Configure feature extraction and pattern recognition parameters
 *
 * **Learning behavior instructions**:
 * - Adjust learning rates and adaptation speed
 * - Specify focus areas and pattern priorities
 * - Control model complexity and resource usage
 * - Define convergence criteria and stopping conditions
 *
 * **Prediction customization instructions**:
 * - Set confidence thresholds for suggestion generation
 * - Specify prediction types and content filters
 * - Configure presentation styles and interaction modes
 * - Define fallback behaviors for uncertain predictions
 *
 * **Privacy and security instructions**:
 * - Control data collection scope and granularity
 * - Specify encryption and anonymization requirements
 * - Define data sharing and export restrictions
 * - Configure audit trails and compliance reporting
 *
 * **User experience instructions**:
 * - Customize notification frequency and timing
 * - Specify interaction modes and presentation preferences
 * - Configure accessibility and usability adaptations
 * - Define personalization boundaries and constraints
 *
 * Instruction processing and application:
 *
 * **Validation and consistency**:
 * - Instructions are validated for syntax and semantic correctness
 * - Conflicting instructions are resolved using priority rules
 * - Invalid instructions are rejected with descriptive error messages
 * - Instruction changes are applied atomically to prevent inconsistent state
 *
 * **Component coordination**:
 * - Instructions are distributed to all relevant container components
 * - Each component interprets and applies instructions within its domain
 * - Instruction updates trigger reconfiguration of affected systems
 * - Cross-component instruction effects are coordinated and synchronized
 *
 * **Dynamic updates and persistence**:
 * - Instructions can be updated at runtime without container restart
 * - Instruction changes take effect immediately for new operations
 * - Instructions are persisted with container state for session continuity
 * - Instruction history may be maintained for debugging and analysis
 *
 * **Performance and optimization**:
 * - Instructions are preprocessed and compiled for efficient runtime application
 * - Frequently used instructions are cached for fast access
 * - Instruction evaluation is optimized for minimal performance impact
 * - Complex instructions may be simplified or approximated for efficiency
 *
 * Extensibility and custom instructions:
 *
 * The sealed class design allows for easy extension with new instruction types:
 *
 * ```kotlin
 * sealed class KarlInstruction {
 *     // Existing instructions...
 *
 *     // Custom instruction examples:
 *     data class LearningFocus(val domain: String, val priority: Float) : KarlInstruction()
 *     data class PrivacyLevel(val level: String, val restrictions: List<String>) : KarlInstruction()
 *     data class ResponseStyle(val style: String, val verbosity: Int) : KarlInstruction()
 *     data class TimeConstraint(val maxResponseTime: Long, val degradeGracefully: Boolean) : KarlInstruction()
 * }
 * ```
 *
 * Best practices for instruction design:
 * - Keep instructions simple and focused on single concerns
 * - Use clear, descriptive names and parameter types
 * - Provide reasonable default values for optional parameters
 * - Include validation logic for parameter ranges and constraints
 * - Document expected behavior and interaction effects
 * - Consider performance implications of complex instructions
 *
 * Thread safety and concurrency:
 * - Instructions are immutable and safe for concurrent access
 * - Instruction updates are atomic and consistent across all components
 * - No shared mutable state in instruction implementations
 * - Thread-safe instruction processing and validation
 */
sealed class KarlInstruction {
    /**
     * Instructs the system to ignore interactions of a specified type during learning.
     *
     * This instruction provides fine-grained control over which user interactions
     * contribute to the learning process. It can be used to exclude noise, irrelevant
     * events, or privacy-sensitive interactions from being incorporated into the
     * learned behavioral patterns.
     *
     * Use cases and applications:
     * - **Privacy protection**: Exclude interactions involving sensitive data or actions
     * - **Noise reduction**: Filter out accidental clicks, system-generated events, or error conditions
     * - **Domain focus**: Concentrate learning on specific types of user behavior
     * - **Performance optimization**: Reduce processing overhead by excluding high-frequency, low-value events
     * - **Compliance requirements**: Ensure certain types of data are not used for learning purposes
     *
     * Implementation considerations:
     * - Instruction is applied during data preprocessing before learning engine processing
     * - Existing learned patterns from the specified type are not automatically removed
     * - The instruction affects only future interactions, not historical data analysis
     * - Multiple IgnoreDataType instructions can be used to exclude multiple interaction types
     *
     * Example usage:
     * ```kotlin
     * val instructions = listOf(
     *     KarlInstruction.IgnoreDataType("mouse_movement"),  // Exclude noise
     *     KarlInstruction.IgnoreDataType("password_field"),   // Privacy protection
     *     KarlInstruction.IgnoreDataType("system_error")      // Focus on user actions
     * )
     * ```
     *
     * @property type The string identifier of the interaction type to ignore. Must match
     *               the type field used in InteractionData objects. Case-sensitive string
     *               matching is used, so ensure exact correspondence with data source naming.
     */
    data class IgnoreDataType(val type: String) : KarlInstruction()

    /**
     * Sets the minimum confidence threshold for prediction generation and presentation.
     *
     * This instruction controls the quality bar for predictions by specifying the minimum
     * confidence level required before a prediction is generated or presented to the user.
     * It helps balance between providing helpful suggestions and avoiding noise from
     * uncertain or unreliable predictions.
     *
     * Confidence threshold effects and implications:
     *
     * **Quality vs. quantity trade-offs**:
     * - Higher thresholds: Fewer but more reliable predictions
     * - Lower thresholds: More predictions but potentially less accurate
     * - Optimal thresholds: Balanced based on user tolerance for incorrect suggestions
     *
     * **Dynamic threshold considerations**:
     * - Learning maturity: Lower thresholds acceptable for new systems building confidence
     * - Domain criticality: Higher thresholds for high-stakes decisions or actions
     * - User expertise: Experienced users may prefer lower thresholds for more options
     * - Context sensitivity: Different thresholds for different prediction types or scenarios
     *
     * **Threshold range interpretation**:
     * - 0.0 - 0.3: Very permissive, includes experimental and exploratory predictions
     * - 0.3 - 0.5: Moderate filtering, balances exploration with reliability
     * - 0.5 - 0.7: Conservative approach, focuses on well-supported predictions
     * - 0.7 - 1.0: Very conservative, only high-confidence predictions are shown
     *
     * Implementation behavior:
     * - Predictions below the threshold are suppressed and not returned to applications
     * - The instruction affects all prediction types unless overridden by type-specific instructions
     * - Confidence calibration may adjust actual thresholds based on historical accuracy
     * - Alternative suggestions may still be provided even if primary prediction is suppressed
     *
     * Adaptive threshold strategies:
     * - **Learning-based**: Automatically adjust thresholds based on prediction accuracy feedback
     * - **Context-aware**: Use different thresholds for different application scenarios
     * - **User-adaptive**: Learn individual user tolerance for prediction accuracy
     * - **Time-varying**: Adjust thresholds based on system maturity and data availability
     *
     * Example usage:
     * ```kotlin
     * val instructions = listOf(
     *     KarlInstruction.MinConfidence(0.6f),  // Only show predictions with 60%+ confidence
     *     // Other customization instructions...
     * )
     * ```
     *
     * @property threshold A floating-point value between 0.0 and 1.0 representing the minimum
     *                    confidence level required for prediction generation. Values closer to
     *                    1.0 result in fewer but more reliable predictions, while values closer
     *                    to 0.0 result in more predictions with potentially lower accuracy.
     *
     * @throws IllegalArgumentException if threshold is not between 0.0 and 1.0 inclusive
     */
    data class MinConfidence(val threshold: Float) : KarlInstruction() {
        init {
            require(threshold in 0.0f..1.0f) {
                "Confidence threshold must be between 0.0 and 1.0, got $threshold"
            }
        }
    }
}
