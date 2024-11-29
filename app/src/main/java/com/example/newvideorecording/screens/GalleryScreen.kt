package com.example.newvideorecording.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newvideorecording.backend.AppViewModel
import com.example.newvideorecording.processingFunc.downloadVideoAndProcess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch


@Composable
fun GalleryScreen(
    viewModel: AppViewModel,
    onVideoClicked : () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var videos by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val scope =  rememberCoroutineScope()

    // Fetch videos from Firebase
    LaunchedEffect(Unit) {
        getVideosFromFirebase(context) { fetchedVideos ->
            videos = fetchedVideos
        }
    }
    val squareSize = 11

    if (videos.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            items(videos.size) { index ->
                val (videoName, videoUrl) = videos[index]

//                Toast.makeText(context,"${videoUrl}",Toast.LENGTH_LONG).show()
//                // Ensure videoUrl is a valid Firebase Storage path, e.g., "videos/video.mp4"
//                val storage = FirebaseStorage.getInstance()
//                val storageRef = storage.reference.child(videoUrl)
//
//                // Define a local file to store the downloaded video
//                val localFile = File.createTempFile("tempVideo", "mp4")

                Text(
                    text = videoName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            Toast
                                .makeText(context, "${videoUrl}", Toast.LENGTH_LONG)
                                .show()
                            scope.launch {
                                downloadVideoAndProcess(viewModel,context, videoUrl, 11)
                            }

                            viewModel.updateVideoList(videoUrl = videoUrl, videoName = videoName)
                            // Navigate when video data is valid
                            if (videoUrl.isNotEmpty() && videoName.isNotEmpty()) {
                                onVideoClicked()
                            } else {
                                Toast
                                    .makeText(context, "Invalid video data", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                )
                Divider()
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No Videos")
        }
    }
}

private fun getVideosFromFirebase(context: Context, onVideosFetched: (List<Pair<String, String>>) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Check if the user is authenticated
    if (userId == null) {
        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        return
    }

    val databaseRef = FirebaseDatabase.getInstance().getReference("videos").child(userId)

    // Show a message indicating the loading process has started
    Toast.makeText(context, "Loading videos...", Toast.LENGTH_SHORT).show()

    databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Check if the user has any videos stored
            if (!snapshot.exists() || !snapshot.hasChildren()) {
                Toast.makeText(context, "No videos found", Toast.LENGTH_SHORT).show()
                onVideosFetched(emptyList()) // Return an empty list
                return
            }

            val videos = mutableListOf<Pair<String, String>>()
            for (videoSnapshot in snapshot.children) {
                val videoUrl = videoSnapshot.child("url").getValue(String::class.java)
                val videoName = videoSnapshot.child("name").getValue(String::class.java)

                // Check for null values and add valid videos to the list
                if (videoUrl != null && videoName != null) {
                    videos.add(Pair(videoName, videoUrl))
                }
            }
            // Invoke the callback with the list of videos
            onVideosFetched(videos)

        }

        override fun onCancelled(error: DatabaseError) {
            // Handle potential errors in a user-friendly way
            Log.e("FirebaseError", "Failed to load videos: ${error.message}")
            Toast.makeText(context, "Failed to load videos: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    })
}



