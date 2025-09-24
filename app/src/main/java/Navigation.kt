import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // Pass the navController to the Home screen so it can navigate
            HomeScreen(navController = navController)
        }
        // Add a new destination for the Reply Screen
        composable("reply") {
            ReplyScreen()
        }
    }
}
