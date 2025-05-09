# Project Structure 📂

```pgsql
project-karl/
├── .gitignore                  // Git ignore settings
├── build.gradle.kts            // Root project build script (plugins, versions, repositories)
├── CODE_OF_CONDUCT.md          // Community code of conduct
├── CONTRIBUTING.md             // Guidelines for contributors
├── gradle/
│   └── wrapper/                // Gradle wrapper files
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── LICENSE                     // Project license (Apache 2.0)
├── README.md                   // This documentation file
├── settings.gradle.kts         // Project settings & module inclusion
│
├── karl-core/                  // KARL Core Module (Platform-agnostic interfaces & logic)
│   ├── build.gradle.kts        // Core module build script (Multiplatform)
│   ├── README.md               // Core module documentation
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/
│       │       └── com/karl/core/
│       │           ├── api/            // Public API interfaces (KarlContainer, LearningEngine)
│       │           │   └── KarlAPI.kt
│       │           ├── container/      // Container implementation logic
│       │           │   └── KarlContainerImpl.kt
│       │           ├── data/           // Data handling interfaces & models
│       │           │   ├── DataStorage.kt
│       │           │   ├── DataSource.kt
│       │           │   └── models/
│       │           │       └── DataModels.kt // InteractionData, KarlContainerState, etc.
│       │           └── instructions/   // Instruction parsing and models
│       │               ├── InstructionParser.kt
│       │               └── KarlInstruction.kt // (Defined in models/DataModels.kt)
│       └── jvmMain/                // JVM-specific source set (if needed for core)
│           └── kotlin/             // JVM specific Kotlin code for core (currently likely empty)
│
├── karl-kldl/                  // KARL KotlinDL Implementation Module
│   ├── build.gradle.kts        // KLDL module build script (JVM)
│   ├── README.md               // KLDL module documentation
│   └── src/
│       └── jvmMain/
│           └── kotlin/
│               └── com/karl/kldl/  // KotlinDL specific implementation
│                   └── KLDLLearningEngine.kt // Implementation of LearningEngine
│
├── karl-room/            // KARL ROOM Implementation Module
│   ├── build.gradle.kts        // ROOM module build script (JVM / Multiplatform)
│   ├── README.md               // ROOM module documentation
│   └── src/
│       └── jvmMain/              // JVM implementation for DataStorage
│           └── kotlin/
│               └── com/karl/room/
│                   └── KarlDao.kt // Implementation of DataStorage
│                   └── KarlRoomDatabase.kt // ROOM Database definition
│                   └── RoomDataStorage.kt // ROOM DataStorage implementatione
│
├── karl-compose-ui/            // KARL Jetpack Compose UI Components Module
│   ├── build.gradle.kts        // Compose UI module build script (Multiplatform)
│   ├── README.md               // Compose UI module documentation
│   └── src/
│       ├── commonMain/           // Common Compose UI code
│       │   └── kotlin/
│       │       └── com/karl/ui/
│       │           ├── KarlContainerUI.kt
│       │           └── LearningProgressIndicator.kt
│       └── jvmMain/              // JVM-specific Compose code (e.g., Previews)
│           └── kotlin/
│               └── com/karl/ui/preview/
│                   └── KarlUIPreviews.kt // Desktop @Preview definitions
│
├── karl-example-desktop/       // KARL Example Desktop Application Module
│   ├── build.gradle.kts        // Example app build script (JVM)
│   ├── README.md               // Example app documentation
│   └── src/
│       └── jvmMain/
│           └── kotlin/
│               └── main.kt         // Entry point for the example Desktop app
│
└── docs/                       // Project Documentation Root
└── index.md                // Main documentation page (or structure)
```