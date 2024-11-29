package com.example.newvideorecording.processingFunc

import android.content.Context
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoPlayer(videoUri: Uri, context: Context , modifier: Modifier = Modifier) {
    // Using AndroidView to display VideoView within Compose
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(videoUri)
                setOnPreparedListener { it.start() } // Start video when prepared
                setOnCompletionListener { /* Handle completion */ }
            }
        },
        update = {
            it.setVideoURI(videoUri)
            it.start()  // Start playing video when the URI is updated
        }
    )
}