import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

/**
 * A reusable composable for a card with a centered text label.
 *
 * @param title The text to display on the card.
 * @param modifier The modifier for this composable.
 * @param onCardClick The lambda function to be executed when the card is clicked.
 */
@Composable
fun CardComponent(
    title: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {} // Add this new parameter
) {
    Card(
        modifier = modifier
            .clickable(onClick = onCardClick), // Apply the clickable modifier
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Provides a preview of the CardComponent.
 */
@Preview(showBackground = true)
@Composable
fun CardComponentPreview() {
    CardComponent(title = "Reply")
}
