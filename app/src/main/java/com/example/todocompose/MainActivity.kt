package com.example.todocompose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.todocompose.ui.HomeScreen
import com.example.todocompose.ui.theme.TodoComposeTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TodoComposeTheme {
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        Log.i("MainAct", "Permission is $isGranted")
                    }
                )
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.i("FCM Token", "Token: $token")
                    } else {
                        Log.e("FCM Token", "Error fetching FCM token: ${task.exception}")
                    }
                }
                RequestPermission(launcher = launcher)
                // A surface container using the 'background' color from the theme
                HomeScreen()
            }
        }
    }

}

@Composable
fun RequestPermission(launcher:ActivityResultLauncher<String>) {
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(false) // Initialize to false initially
    }

    // Request notification permission when the app is launched
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                // Request notification permission if not already granted
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For older Android versions, assume notification permission is granted
            hasNotificationPermission = true
        }
    }
}