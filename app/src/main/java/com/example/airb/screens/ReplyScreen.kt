import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.airb.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

sealed class ApiState {
    object Idle : ApiState()
    object Loading : ApiState()
    data class Success(val response: String) : ApiState()
    data class Error(val message: String) : ApiState()
}

// Helper function to get a Bitmap from a Uri
fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private const val TAG = "ReplyScreen"

@Composable
fun ReplyScreen() {
    var replyGuide by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var apiResponse by remember { mutableStateOf("") }
    var apiState by remember { mutableStateOf<ApiState>(ApiState.Idle) }

    val coroutineScope = rememberCoroutineScope()

    // Get the current context
    val context = LocalContext.current

    // Initialize the GenerativeModel here
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash-lite",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = replyGuide,
                onValueChange = { replyGuide = it },
                label = { Text("Tell them, ") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )

            Button(
                onClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                enabled = apiState !is ApiState.Loading
            ) {
                Text(text = "Add Photo")
            }

            // Display the selected image if a URI exists
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            OutlinedTextField(
                value = apiResponse,
                onValueChange = { apiResponse = it },
                label = { Text("AI response") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        apiState = ApiState.Loading
                        Log.d(TAG, "Attempting to generate response...")

                        if (selectedImageUri == null) {
                            apiState = ApiState.Error("Please select a screenshot to proceed.")
                            Log.d(TAG, "No image selected. Exiting.")
                            return@launch
                        }

                        Log.d(TAG, "Selected image URI: $selectedImageUri")
                        // Convert URI to Bitmap
                        val bitmap = getBitmapFromUri(context.contentResolver, selectedImageUri!!)

                        if (bitmap == null) {
                            apiState = ApiState.Error("Failed to load image from URI.")
                            Log.d(TAG, "Bitmap conversion failed. Exiting.")
                            return@launch
                        }

                        // Create the content object for the API call
                        Log.d(TAG, "Creating model content object.")
                        val prompt = "Rewrite this message using Dale Carnegie’s principles from How to Win Friends and Influence People. Use a warm, customer service–oriented tone. Be concise, helpful, and friendly. Only provide one polished version of the message—no explanations, no options. don't put it in quotation marks"
                        val modelContent = content {
                            image(bitmap)
                            text("tell them, $replyGuide\n\n$prompt")
                        }

                        // Make the API call and handle the response
                        try {
                            Log.d(TAG, "Calling Gemini API...")
                            val response = generativeModel.generateContent(modelContent)
                            val generatedText = response.text ?: "No response from AI."
                            apiResponse = generatedText
                            apiState = ApiState.Success(generatedText)
                            Log.d(TAG, "API call successful. Generated text: $generatedText")
                        } catch (e: Exception) {
                            apiState = ApiState.Error("API call failed: ${e.message}")
                            Log.d(TAG, "API call failed: ${e.message}")
                        }
                    }
                },
                enabled = apiState !is ApiState.Loading
            ) {
                if (apiState is ApiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Send")
                }
            }
        }
    }
}
