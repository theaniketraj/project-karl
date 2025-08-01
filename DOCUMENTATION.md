# Documentation Generation

Project KARL includes comprehensive documentation generation using Dokka. The documentation covers all public APIs, includes code examples, and provides detailed technical explanations.

## Quick Start

### Generate Documentation

```bash
./gradlew generateDocs
```

### Open Documentation in Browser

```bash
./gradlew openDocs
```

### Clean Documentation

```bash
./gradlew cleanDocs
```

## Available Documentation Formats

- **HTML Multi-Module**: Complete documentation with cross-module linking
- **Individual Module Docs**: Per-module documentation available via `./gradlew :module-name:dokkaHtml`

## Documentation Structure

```pgsql
build/dokka/htmlMultiModule/
├── index.html              # Main documentation index
├── karl-core/              # Core framework documentation
├── karl-kldl/              # KotlinDL engine documentation  
├── karl-room/              # Room storage documentation
├── karl-compose-ui/        # Compose UI components documentation
└── navigation.html         # Documentation navigation
```

## Module Documentation

### KARL Core

Contains fundamental interfaces, data models, and APIs for the KARL framework.

- Core interfaces: `LearningEngine`, `KarlContainer`, `DataStorage`, `DataSource`
- Data models: `InteractionData`, `KarlContainerState`, `Prediction`, `KarlInstruction`
- API layer: `Karl` builder pattern and configuration utilities

### KARL KotlinDL Engine

Machine learning implementation using KotlinDL for neural network computation.

- `KLDLLearningEngine`: Main ML engine with incremental learning
- `SimpleMLPModel`: Multi-layer perceptron architecture
- Privacy-first on-device training and inference

### KARL Room Storage

Robust data persistence layer using Android's Room database library.

- `RoomDataStorage`: Main storage implementation
- `KarlDao`: Type-safe database operations
- Entity models for container state and interaction data

### KARL Compose UI

Modern UI components for visualizing KARL AI containers.

- `KarlContainerUI`: Main container visualization
- `KarlLearningProgressIndicator`: Progress visualization
- Material Design compliant with reactive state management

## Documentation Features

### Comprehensive Coverage

- **API Documentation**: All public classes, interfaces, and methods
- **Code Examples**: Usage patterns and integration examples
- **Architecture Guides**: Design principles and implementation details
- **Cross-References**: Links between related components

### Technical Depth

- **Performance Considerations**: Optimization notes and best practices
- **Error Handling**: Exception documentation and recovery strategies
- **Thread Safety**: Concurrency patterns and safety guarantees
- **Privacy & Security**: Data protection and compliance considerations

### Developer Experience

- **Source Links**: Direct links to GitHub source code
- **External Links**: Links to related documentation (Coroutines, Compose, Room)
- **Search Functionality**: Full-text search across all documentation
- **Mobile Responsive**: Documentation works on all devices

## Customization

### Module Configuration

Each module includes its own Dokka configuration in `build.gradle.kts`:

- Module name and version
- Source links to GitHub
- External documentation links
- Package-level documentation

### Adding Documentation

1. **KDoc Comments**: Use `/** */` format for all public APIs
2. **Module Descriptions**: Update `Module.md` files for module overviews
3. **Code Examples**: Include usage examples in KDoc
4. **Cross-References**: Use `@see` tags for related components

### External Links

The documentation includes links to:

- [Kotlinx Coroutines](https://kotlinlang.org/api/kotlinx.coroutines/)
- [KotlinDL](https://kotlin.github.io/kotlindl/)
- [Android Room](https://developer.android.com/reference/androidx/room/package-summary)
- [Jetpack Compose](https://developer.android.com/reference/kotlin/androidx/compose/package-summary)

## Build Integration

### Gradle Tasks

- `generateDocs`: Generate complete multi-module documentation
- `openDocs`: Generate and open documentation in browser
- `cleanDocs`: Clean generated documentation
- `dokkaHtmlMultiModule`: Core Dokka multi-module task
- `:module:dokkaHtml`: Generate documentation for specific module

### CI/CD Integration

Documentation can be automatically generated and deployed:

```yaml
- name: Generate Documentation
  run: ./gradlew generateDocs

- name: Deploy Documentation
  # Deploy build/dokka/htmlMultiModule/ to documentation hosting
```

## Quality Assurance

### Documentation Standards

- All public APIs must have KDoc comments
- Include parameter descriptions with `@param`
- Document return values with `@return`
- List exceptions with `@throws`
- Provide usage examples for complex APIs

### Validation

Documentation generation serves as validation:

- Ensures all KDoc is syntactically correct
- Verifies cross-references are valid
- Confirms external links are accessible
- Tests code examples for compilation

The comprehensive documentation ensures Project KARL is accessible to developers at all levels, from quick integration to deep customization.
