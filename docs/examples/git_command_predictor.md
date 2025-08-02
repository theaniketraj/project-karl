# Example: Predicting the Next Git Command

This example demonstrates how Project KARL can be used to learn a user's common command sequences and predict the next likely action. This is a classic use case for on-device, adaptive AI, as a developer's command history is sensitive and highly personalized.

The full, runnable source code for a similar scenario can be found in the [`:karl-example-desktop`](https://github.com/theaniketraj/project-karl/tree/main/karl-example-desktop) module.

## Objective

To build a KARL integration that observes a user's Git command usage and suggests the next command. For example, after a user types `git add .`, KARL might learn to predict `git commit -m "COMMIT_MESSAGE"`.

## Key Concepts Demonstrated

* **Sequential Data:** Handling interactions that have a meaningful order.
* **Feature Engineering:** Converting string-based commands into numerical representations for the model.
* **Contextual Prediction:** Using a history of recent actions to inform the next prediction.

## 1. Designing the `InteractionData`

The first step is to define how we represent a "git command" action as `InteractionData`.

```kotlin
// Conceptual code within your DataSource
fun onGitCommandExecuted(command: String, arguments: List<String>) {
    val interaction = InteractionData(
        userId = "current-developer",
        type = "git_command", // A clear type for this event
        details = mapOf(
            "command" -> command, // e.g., "add", "commit", "push"
            "arg_count" -> arguments.size,
            // You could add more features like "has_flag_m" -> true for commits
        ),
        timestamp = System.currentTimeMillis()
    )
    // Pass this 'interaction' to the KarlContainer
    onNewData(interaction)
}
```

In this structure, we capture the core command and some metadata. We deliberately avoid capturing sensitive information like commit messages or file contents.

## 2. The LearningEngine Strategy

The LearningEngine (e.g., :karl-kldl) needs to be configured to handle this sequential data.

## **Feature Engineering**

Inside the trainStep and predict methods of the LearningEngine, we need to convert the history of InteractionData objects into a numerical vector. A simple strategy is:

1. Create a Vocabulary: Maintain a mapping from command strings to integer indices (e.g., {"add": 1, "commit": 2, "push": 3, ...})

2. Create an Input Vector: For prediction, take the last N commands from the user's history (e.g., N=3). Convert these commands to their integer indices. This creates an input vector like [1, 2, 3] (representing add -> commit -> push)

3. Create a Target Label: For training, the target is the command that followed the input sequence, converted to a one-hot encoded vector

## **Model Architecture**

A simple Multi-Layer Perceptron (MLP), as implemented in SimpleMLPModel.kt, can be used as a starting point. It takes the fixed-size input vector (e.g., [1, 2, 3]) and tries to predict the probability of each known command being the next one.

For more advanced sequence modeling, a Recurrent Neural Network (RNN) or LSTM model would be a more powerful choice, as they are specifically designed to understand temporal patterns. This would be a future enhancement for the LearningEngine.

## 3. The Prediction Flow

1. The user executes a command (e.g., git commit). This is captured by the DataSource.
2. The application's UI then immediately calls karlContainer.getPrediction().
3. The KarlContainer passes the request to the LearningEngine.
4. The LearningEngine retrieves the recent command history from DataStorage (or its internal state), creates an input vector (e.g., [... "add", "commit"]), and feeds it to the model.
5. The model outputs a probability distribution over all known commands.
6. The LearningEngine finds the command with the highest probability (e.g., push) and returns it as a Prediction object.
7. The UI displays "Suggestion: git push".

## 4. Conclusion

This example illustrates the complete end-to-end flow of KARL for a practical, real-world problem. It highlights the importance of thoughtful InteractionData design and shows how even a simple on-device model can learn personalized user workflows over time, all while respecting user privacy.
