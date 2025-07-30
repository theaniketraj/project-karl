package com.karl.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * A composable that visually indicates the learning progress or "maturity" of the KARL model.
 *
 * @param progress A float value between 0.0 and 1.0 representing the learning progress.
 * @param label A text label to display alongside the indicator (e.g., "AI Maturity").
 */
@Composable
fun KarlLearningProgressIndicator(
    progress: Float,
    label: String = "Learning Progress",
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        )
        // Add more spacing between text and indicator
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .width(200.dp) // Wider progress bar
                    .height(8.dp) // Much thicker progress bar
                    .clip(RoundedCornerShape(4.dp)), // Rounded corners for better appearance
        )
        // Add spacing after the indicator too
        Spacer(modifier = Modifier.height(8.dp))
        // Optional: Add a text representation of the percentage
        Text(
            text = "${(progress * 100).toInt()}%",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
        )
    }
}

// Example Usage (for Preview or within another Composable)
// @Preview
// @Composable
// fun PreviewKarlLearningProgressIndicator() {
//     KarlLearningProgressIndicator(progress = 0.75f)
// }
