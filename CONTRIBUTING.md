# Contributing to Project KARL

First off, thank you for considering contributing to Project KARL! 🎉 We're excited to build a community around privacy-first, adaptive AI in Kotlin, and your help is invaluable. Whether you're fixing bugs, improving documentation, suggesting features, or writing code, your contributions are welcome.

This document provides guidelines to help make the contribution process clear and effective for everyone involved.

## Table of Contents

- [Contributing to Project KARL](#contributing-to-project-karl)
  - [Table of Contents](#table-of-contents)
  - [Code of Conduct](#code-of-conduct)
  - [Ways to Contribute](#ways-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Your First Code Contribution](#your-first-code-contribution)
  - [Setting Up Your Development Environment](#setting-up-your-development-environment)
  - [Pull Request Process](#pull-request-process)
  - [Coding Style Guidelines](#coding-style-guidelines)
  - [Testing](#testing)
  - [License](#license)

## Code of Conduct

This project and everyone participating in it is governed by the [Project KARL Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior.

## Ways to Contribute

There are many ways to contribute to Project KARL:

- 🐛 **Reporting Bugs:** If you find a bug, please report it!

- 💡 **Suggesting Enhancements:** Have an idea for a new feature or an improvement to an existing one? Let us know.

- 📝 **Improving Documentation:** Found a typo, an unclear explanation, or missing information in the docs? Help us improve it.
  
- 💻 **Writing Code:** Contribute bug fixes, new features, or improvements to the core library, implementations, or example app.
  
- ✅ **Adding Tests:** Help improve test coverage by adding unit or integration tests.
  
- ❓ **Answering Questions:** Help other users by answering questions in GitHub Issues or Discussions (if enabled).

## Reporting Bugs

Before submitting a new bug report, please check the existing [GitHub Issues](https://github.com/theaniketraj/project-karl/issues) to see if the bug has already been reported.

If you find a new bug, please provide a clear and detailed report including:

1. **A clear and descriptive title.**
2. **Steps to reproduce the bug:** Provide a minimal, reproducible example if possible.
3. **Expected behavior:** What did you expect to happen?
4. **Actual behavior:** What actually happened? Include error messages and stack traces if applicable.
5. **Environment details:** Your operating system, JDK version, Kotlin version, and the specific version of Project KARL you are using.

Use the "Bug Report" issue template if available.

## Suggesting Enhancements

We welcome suggestions for new features and improvements! Before submitting:

1. Check the existing [GitHub Issues](https://github.com/theaniketraj/project-karl/issues) and [Discussions](https://github.com/theaniketraj/project-karl/discussions) (if enabled) to see if your idea has already been discussed.
2. Consider the project's core philosophy (privacy-first, local learning, composable). Does your suggestion align with these principles?

When submitting an enhancement suggestion, please include:

1. **A clear and descriptive title.**
2. **Detailed description:** Explain the enhancement and why it would be valuable. What problem does it solve?
3. **Motivation:** Why is this feature needed? What use cases does it enable?
4. **(Optional) Proposed implementation:** Any ideas on how it could be implemented?

Use the "Feature Request" issue template if available.

## Your First Code Contribution

Unsure where to begin? Look for issues tagged `good first issue` or `help wanted` in the [GitHub Issues](https://github.com/theaniketraj/project-karl/issues). These are typically smaller, well-defined tasks suitable for newcomers.

Don't hesitate to ask questions on an issue thread if you need clarification before starting work!

## Setting Up Your Development Environment

1. **Prerequisites:**

- Git
- JDK (Java Development Kit) - Version 11 or later recommended (check project requirements).
- IntelliJ IDEA (Community or Ultimate) with the Kotlin plugin is the recommended IDE.

2. **Fork the Repository:** Click the "Fork" button on the top right of the [Project KARL repository page](https://github.com/theaniketraj/project-karl).

3. **Clone Your Fork:**

```bash
  git clone https://github.com/theaniketraj/project-karl.git
  cd project-karl
  ```

1. **Add Upstream Remote:**

    ```bash
    git remote add upstream https://github.com/theaniketraj/project-karl.git
    ```

    *(Replace `theaniketraj` with the actual owner of the main repository if it's not you)*
2. **Import into IntelliJ IDEA:** Open IntelliJ IDEA and select "Open", then navigate to the cloned `project-karl` directory. IntelliJ should automatically detect the Gradle project. Allow it to sync and download dependencies.
3. **Build the Project:** Verify your setup by running the build from the terminal or IDE:

    ```bash
    ./gradlew build
    ```

## Pull Request Process

1. **Ensure an Issue Exists:** For non-trivial changes (bug fixes, features), ensure there's a corresponding GitHub Issue discussing the problem or enhancement. Assign yourself or comment that you're working on it.
2. **Create a Feature Branch:** Create a new branch off the `main` branch (or `develop` if that's the primary development branch) for your changes:

    ```bash
    # Fetch latest changes from upstream
    git fetch upstream
    # Ensure your main branch is up-to-date
    git checkout main
    git merge upstream/main
    # Create your feature branch
    git checkout -b feature/your-descriptive-feature-name # e.g., feature/add-rnn-engine
    # Or for bug fixes:
    # git checkout -b fix/issue-123-description # e.g., fix/issue-123-preview-import
    ```

3. **Implement Your Changes:**
    - Write clear, maintainable code following the [Coding Style Guidelines](#coding-style-guidelines).
    - Add necessary tests ([Testing](#testing)).
    - Update documentation if needed.
4. **Test Your Changes:** Ensure all existing tests pass and your new tests cover your changes:

    ```bash
    ./gradlew check
    ```

5. **Commit Your Changes:** Use clear and descriptive commit messages. Consider using [Conventional Commits](https://www.conventionalcommits.org/) if you're comfortable with it.

    ```bash
    git add .
    git commit -m "feat: Add basic RNN implementation for LearningEngine"
    # Or: git commit -m "fix: Correct import path for Prediction model"
    ```

6. **Push Your Branch:** Push your changes to your fork:

    ```bash
    git push origin feature/your-descriptive-feature-name
    ```

7. **Open a Pull Request (PR):**
    - Go to your fork on GitHub.
    - Click the "Compare & pull request" button for your new branch.
    - Ensure the base repository and branch (`main` or `develop`) are correct.
    - Provide a clear title and description for your PR:
        - Link to the relevant GitHub Issue (e.g., "Closes #123").
        - Summarize the changes made.
        - Explain *why* these changes are needed.
        - Describe how you tested the changes.
    - Submit the PR.
8. **Code Review:** Project maintainers will review your PR. Address any feedback or requested changes by pushing new commits to your feature branch. The PR will update automatically.
9. **Merging:** Once approved, a maintainer will merge your PR. Congratulations! 🎉

## Coding Style Guidelines

- Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Use the IntelliJ IDEA Kotlin formatter (usually configured by default).
- Write clear, concise, and well-commented code where necessary. Focus on readability.
- Follow existing patterns and conventions within the codebase.

## Testing

- Contributions should include tests.
  
- **Unit Tests:** Use Kotlin's testing framework (likely via Kotlin Multiplatform testing libraries, built on JUnit 5 for JVM) for testing individual classes and functions, especially in `karl-core`. Place tests in the corresponding `src/<sourceSet>/kotlin/` directory (e.g., `src/commonTest/kotlin/`, `src/jvmTest/kotlin/`).
  
- **Integration Tests:** May be added for testing interactions between modules (e.g., ensuring the `karl-kldl` engine works correctly with `karl-core` APIs).

- Run all tests using `./gradlew check` before submitting a PR.

## License

By contributing to Project KARL, you agree that your contributions will be licensed under its **Apache License 2.0**. You can find the full license text in the [LICENSE](https://github.com/theaniketraj/project-karl/blob/main/LICENSE) file.

---

Thank you again for your interest in contributing! We look forward to collaborating with you.
