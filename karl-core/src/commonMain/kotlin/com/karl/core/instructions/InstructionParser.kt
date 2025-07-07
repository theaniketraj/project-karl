package com.karl.core.instructions

import com.karl.core.models.KarlInstruction
// import com.karl.core.models.KarlInstruction // Import the data class/sealed class we defined earlier

/**
 * Interface for parsing raw user input into structured KarlInstruction objects.
 * Implementations handle specific input formats (e.g., parsing a string-based DSL,
 * interpreting a configuration file, etc.).
 *
 * The KarlContainer uses an implementation of this interface to understand user-defined
 * rules that modify its behavior.
 */
interface InstructionParser {
    /**
     * Parses the raw input into a list of KarlInstruction objects.
     *
     * @param rawInput The raw input from the user (e.g., a string from a UI text field,
     *                 content read from a configuration file, etc.). The expected format
     *                 depends on the specific implementation of this interface.
     * @return A list of parsed KarlInstruction objects. Returns an empty list if the input
     *         is valid but contains no instructions.
     * @throws KarlInstructionParseException if the input is invalid and cannot be parsed
     *         according to the implementation's format.
     */
    fun parse(rawInput: String): List<KarlInstruction>
    // Note: Using 'String' is a common case. For maximum flexibility, you might use 'Any'
    // or a sealed class representing different input types (e.g., InstructionInput.StringInput, InstructionInput.JsonInput).
}

/**
 * Exception thrown when KarlInstruction parsing fails due to invalid input format or content.
 * This custom exception helps differentiate parsing errors from other types of exceptions.
 *
 * @param message A descriptive message about the parsing error, ideally explaining
 *                what was wrong with the input.
 * @param cause The underlying cause of the exception (optional, e.g., from a JSON parsing library).
 */
class KarlInstructionParseException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

// Note: You would need to create concrete implementations of this interface later,
// for example:
// class StringDslInstructionParser : InstructionParser {
//     override fun parse(rawInput: String): List<KarlInstruction> {
//         // Implement logic to parse a string like "ignore data_type: 'git_command'"
//         // and return a list like listOf(KarlInstruction.IgnoreDataType("git_command"))
//         // Throw KarlInstructionParseException if the string format is wrong
//         TODO("Implement parsing logic")
//     }
// }
// The KarlContainerBuilder or your application code would then instantiate
// and provide a specific parser implementation.
