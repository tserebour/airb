import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


/**
 * A composable function that represents the Home Screen of the application.
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    val cardContents = listOf(
        Pair("Reply", "reply"),
        Pair("Card 2 Placeholder", "card2"),
        Pair("Card 3 Placeholder", "card3"),
        Pair("Card 4 Placeholder", "card4"),
        Pair("Card 5 Placeholder", "card5"),
        Pair("Card 6 Placeholder", "card6"),
        Pair("Card 7 Placeholder", "card7"),
        Pair("Card 8 Placeholder", "card8")
    )

    // We now wrap the LazyVerticalGrid in a Box with center alignment.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(cardContents) { (title, route) ->
                CardComponent(
                    title = title,
                    onCardClick = {
                        // Navigate directly using the route from the list
                        navController.navigate(route)
                    }
                )
            }
        }
    }
}

/**
 * Provides a preview of the HomeScreen composable.
 */
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
//    HomeScreen(navController: NavHostController)
}
