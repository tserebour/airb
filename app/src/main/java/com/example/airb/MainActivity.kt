package com.example.airb


import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // It's a good practice to wrap your app's content in a theme.
            // For this example, we'll create a simple dark theme wrapper.
            // In a larger app, this would be defined in a separate Theme.kt file.
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // Matching the background of your screen
                ) {
                    // Create a NavController that will be used by our AppNavigation composable.
                    val navController = rememberNavController()

                    // Call our AppNavigation function to set up the navigation graph.
                    // This is where our single-Activity architecture is realized.
                    AppNavigation(navController = navController)

                }
            }
        }
    }
}
