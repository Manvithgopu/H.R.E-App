package com.example.newvideorecording.screens

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.newvideorecording.backend.AppViewModel
import com.example.newvideorecording.clearData


@Composable
fun VideoDetailsScreen(
    navController: NavHostController,
    viewModel: AppViewModel,
    context: Context,
    modifier: Modifier = Modifier
) {
    // MediaPlayer instance
    val mediaPlayer = remember { MediaPlayer() }
    var duration by remember { mutableStateOf(0) }
    val uiState by viewModel.videoList.collectAsState()
    val uiState2 by viewModel.frameList.collectAsState()
    var totalFrames = uiState2.totalFrames

    BackHandler {
        // Navigate to the gallery page
        clearData(viewModel,navController)
    }

    LaunchedEffect(uiState.videoUrl) {
        try {
            // Ensure videoUrl is not empty
            if (uiState.videoUrl.isNotEmpty()) {
                mediaPlayer.setDataSource(context, Uri.parse(uiState.videoUrl))
                mediaPlayer.prepare()
                duration = mediaPlayer.duration / 1000 // in seconds
            } else {
                Log.e("VideoDetailsScreen", "Invalid video URL: ${uiState.videoUrl}")
            }
        } catch (e: Exception) {
            Log.e("VideoDetailsScreen", "Error setting data source: ${e.message}")
            Toast.makeText(context, "Error loading video", Toast.LENGTH_SHORT).show()
        }
    }

    // Cleanup MediaPlayer when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Video player at the top
        VideoPlayer(videoUrl = uiState.videoUrl, context = context)

        // Display video details below
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Video Name: ${uiState.videoName}", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Duration: $duration seconds", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))

        // Add more video details as needed
        Text(text = "Additional details about the video can go here.", style = MaterialTheme.typography.h6)

        Button(
            onClick = { navController.navigate(com.example.newvideorecording.CameraScreen.Frames.name) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Go to frames page")
        }

    }
}

@Composable
fun VideoPlayer(videoUrl: String, context: Context, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(800.dp),
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(Uri.parse(videoUrl))
                setOnPreparedListener { it.start() } // Start video when prepared
            }
        },
        update = {
            it.setVideoURI(Uri.parse(videoUrl))
            it.start()  // Start playing video when the URI is updated
        }
    )
}

