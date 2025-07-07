package com.karl.ui.preview

import androidx.compose.runtime.Composable
// Keep ONLY the standard Preview import from ui-tooling
// This requires the compose.uiTooling dependency in jvmMain
// import androidx.compose.ui.tooling.preview.Preview
import com.karl.core.models.Prediction
import androidx.compose.desktop.ui.tooling.preview.Preview // Desktop-specific Preview

import com.karl.ui.KarlContainerUI // Import your common composables
import com.karl.ui.KarlLearningProgressIndicator
import kotlinx.coroutines.flow.MutableStateFlow // Use StateFlow for preview state

// --- Previews for KarlLearningProgressIndicator ---

@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlLearningProgressIndicator_Half() {
    KarlLearningProgressIndicator(progress = 0.5f)
}

@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlLearningProgressIndicator_Full() {
    KarlLearningProgressIndicator(progress = 1.0f, label = "AI is Mature!")
}

// --- Previews for KarlContainerUI ---

@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlContainerUI_NoSuggestion() {
    // Create dummy StateFlows for this preview
    // Prediction should now be resolved via the corrected import
    val dummyPrediction = MutableStateFlow<Prediction?>(null)
    val dummyProgress = MutableStateFlow(0.1f) // Low progress

    KarlContainerUI(
        predictionState = dummyPrediction,
        learningProgressState = dummyProgress,
    )
}

@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlContainerUI_WithSuggestion() {
    // Create dummy StateFlows for this preview
    // Prediction should now be resolved via the corrected import
    val dummyPrediction = MutableStateFlow(Prediction(suggestion = "git commit", confidence = 0.9f, type = "next_command"))
    val dummyProgress = MutableStateFlow(0.75f) // Higher progress

    KarlContainerUI(
        predictionState = dummyPrediction,
        learningProgressState = dummyProgress,
    )
}

// You can add more preview functions here with different states or configurations
