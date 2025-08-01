# Module KARL Core

The **KARL Core** module contains the fundamental interfaces, data models, and APIs that define the Project KARL (Kotlin Adaptive Reasoning Learner) framework. This module provides the foundation for building privacy-first, locally adaptive AI systems that learn from user interactions without compromising data privacy.

## Key Components

### Core Interfaces

- **`LearningEngine`** - Defines the contract for AI/ML model implementations
- **`KarlContainer`** - Orchestrates the complete KARL system lifecycle
- **`DataStorage`** - Abstraction for persistent state and interaction data
- **`DataSource`** - Interface for application event integration

### Data Models

- **`InteractionData`** - Represents user interaction events
- **`KarlContainerState`** - Encapsulates serializable learning state
- **`Prediction`** - AI-generated suggestions and insights
- **`KarlInstruction`** - User-defined learning preferences and rules

### API Layer

- **`Karl`** - Main entry point with fluent builder pattern
- **`KarlAPI`** - Container creation and configuration utilities

## Architecture Principles

- **Privacy First**: All processing remains on-device
- **Composable Design**: Modular components with clear interfaces
- **Multiplatform Ready**: Kotlin Multiplatform for cross-platform compatibility
- **Coroutine Native**: Asynchronous operations with structured concurrency

## Usage

```kotlin
val container = Karl.forUser("user_123")
    .withLearningEngine(learningEngine)
    .withDataStorage(dataStorage)
    .withDataSource(dataSource)
    .withCoroutineScope(scope)
    .build()
```

## Dependencies

- Kotlin Standard Library
- Kotlinx Coroutines Core

This module is the foundation upon which all KARL implementations are built.
