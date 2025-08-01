# Module KARL Compose UI

The **KARL Compose UI** module provides modern, Material Design-compliant user interface components for visualizing and interacting with KARL AI containers. Built with Jetpack Compose, it offers reactive, performant UI components that integrate seamlessly with KARL's learning system.

## Key Features

### Component Architecture

- **Reactive UI**: Built on Compose StateFlows for real-time updates
- **Material Design**: Follows Material Design 3 guidelines and theming
- **Accessibility**: Implements proper semantics and navigation support
- **Performance**: Optimized for smooth animations and minimal recomposition

### Integration Patterns

- **MVVM/MVI Ready**: Connects to KARL containers through StateFlow observables
- **Dependency Injection**: Supports DI frameworks and testability
- **Compose Native**: Seamless integration with existing Compose applications
- **Customizable Theming**: Full Material Theme support with customization

## Core Components

### Primary Components

- **`KarlContainerUI`** - Main container visualization component
- **`KarlLearningProgressIndicator`** - Learning progress visualization
- Additional UI utilities and helper components

### Preview System

- **`KarlUIPreviews`** - Comprehensive preview definitions for development
- **Desktop Previews**: IDE-integrated visual development tools
- **State Simulation**: Mock data for testing different component states

## Design Philosophy

### User Experience

- **Transparency**: Clear visualization of AI learning progress
- **Trust Building**: Users can see how the AI system is learning
- **Non-Intrusive**: Subtle integration that enhances rather than disrupts workflow
- **Real-Time Feedback**: Immediate updates as learning progresses

### Developer Experience

- **Easy Integration**: Simple API for adding KARL visualization to apps
- **Customizable**: Extensive theming and layout customization options
- **Well Documented**: Comprehensive KDoc with usage examples
- **Preview Support**: Rich preview system for rapid development

## Advanced Features

- **Adaptive Layouts**: Responsive design for different screen sizes
- **Animation Support**: Smooth transitions and state changes
- **Error States**: Graceful handling of error conditions
- **Loading States**: Proper loading indicators and skeleton screens

## Dependencies

- KARL Core module
- Jetpack Compose (Runtime, Foundation, Material, UI)
- Kotlinx Coroutines
- Compose UI Tooling

## Usage

```kotlin
@Composable
fun MyApp() {
    KarlContainerUI(
        containerState = karlContainer.state.collectAsState(),
        predictions = karlContainer.predictions.collectAsState(),
        onInteraction = { /* handle user interaction */ }
    )
}
```

This module brings beautiful, functional AI visualization to KARL applications with modern Compose UI.
