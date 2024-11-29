package com.example.newvideorecording.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.newvideorecording.CameraPreview
import com.example.newvideorecording.FaceScannerOverlay
import com.example.newvideorecording.imageAnalyzer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.face.Face
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private var recording : Recording? = null

//val squareRect = android.graphics.Rect(130, 200, 350, 500)

@Composable
fun CameraScreen (
    onGalleryButtonClicked : () -> Unit = {},
    modifier: Modifier = Modifier
){
    var facesDetected by remember { mutableStateOf(listOf<Face>()) }
    val context = LocalContext.current
    var value by remember { mutableStateOf(value = 0) }
    var value_2 by remember { mutableStateOf(0) }
    var isUsingFrontCamera by remember { mutableStateOf(true) }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.VIDEO_CAPTURE
            )
        }
    }
    var value_3 by remember { mutableStateOf(0) }
    var value_4 by remember { mutableStateOf(0) }
    var isFaceFullyInView by remember { mutableStateOf(false) }
    var isRecording by remember {
        mutableStateOf(0)
    }

    val onFacesDetected: (List<Face>, Int, Int) -> Unit = { faces, viewWidth, viewHeight ->
        facesDetected = faces
        if (faces.isNotEmpty()) {
            value = if (value == 0) 1 else value
        } else {
            value = 0
        }

        if (faces.isNotEmpty()) {
            val face = faces[0]
            val boundingBox = face.boundingBox

            if (viewWidth >= 100 && viewHeight >= 240) {
                isFaceFullyInView = boundingBox.left >= 130 &&
                        boundingBox.top >= 200 &&
                        boundingBox.right <= 350 &&
                        boundingBox.bottom <= 490
            } else {
                isFaceFullyInView = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ){
        CameraPreview(controller , onFacesDetected , modifier)
        imageAnalyzer(context,onFacesDetected)

        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(70.dp)
                    .padding(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
            ) {

                Text(
                    text = if (value == 1 ) "face is detected " else "face is not detected ",
                    color = if (value == 0) Color.Blue else Color.Red, // Set text color to white for better visibility
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 12.dp)
                )
                Text(
                    text = if (value == 1 && isFaceFullyInView )  "face is fully detected " else if (value == 1) "face is detected but not fully" else "face not detected",
                    color = if (value == 0) Color.Blue else Color.Red, // Set text color to white for better visibility
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 12.dp)
                )
            }

        }


        Row (
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = onGalleryButtonClicked,
                modifier = Modifier
                    .fillMaxWidth(0.5f),

            ) {
                Text(
                    text = "Open Gallery"
                )
            }

            Button(
                onClick = {
                    recordVideo(context,controller);
                    if(isRecording == 0){
                        isRecording = 1
                    }else{
                        isRecording = 0
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text =

                        if(isRecording == 1 ){
                            "Stop Recording"
                        } else{
                            "Start Recording"
                        }

                )
            }

        }

        if (value == 1 && isFaceFullyInView){
            value_3 = 1
        }else{
            value_3 = 0
            value_4 = 0
        }

        FaceScannerOverlay(
            if(isFaceFullyInView){
                Color.Green
            }else{
                Color.Red
            }
        )

    }
}

private fun recordVideo(context: Context, controller: LifecycleCameraController) {
    if (recording != null) {
        recording?.stop()
        recording = null
        return
    }

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val outputFile = File(context.filesDir, "my-recording$timeStamp.mp4")

    recording = controller.startRecording(
        FileOutputOptions.Builder(outputFile).build(),
        AudioConfig.AUDIO_DISABLED,
        ContextCompat.getMainExecutor(context)
    ) { event ->
        when (event) {
            is VideoRecordEvent.Finalize -> {
                if (event.hasError()) {
                    recording?.close()
                    recording = null
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Video Captured ${outputFile.name}", Toast.LENGTH_SHORT).show()
                    uploadVideoToFirebase(context, outputFile)
                }
            }
        }
    }
}

private fun uploadVideoToFirebase(context: Context, outputFile: File) {
    // Check if the file exists
    if (!outputFile.exists()) {
        Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val videoRef = storageRef.child("videos/$userId/${outputFile.name}")

    val uri = Uri.fromFile(outputFile)

    videoRef.putFile(uri)
        .addOnSuccessListener {
            videoRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the video URL to the database only after a successful upload
                saveVideoUrlToDatabase(context, uri.toString(), outputFile.name)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("UploadError", "Upload Failed: ${exception.message}")
            Toast.makeText(context, "Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
        .addOnCompleteListener {
            // Optionally handle cleanup or other actions here
        }
}

private fun saveVideoUrlToDatabase(context: Context, videoUrl: String, videoName: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val databaseRef = FirebaseDatabase.getInstance().getReference("videos").child(userId)

    val videoId = databaseRef.push().key ?: return
    val videoData = mapOf(
        "url" to videoUrl,
        "name" to videoName,
        "timestamp" to System.currentTimeMillis()
    )
    databaseRef.child(videoId).setValue(videoData)
        .addOnSuccessListener {
            Toast.makeText(context, "Video URL saved", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { exception ->
            Log.e("DatabaseError", "Failed to save URL: ${exception.message}")
            Toast.makeText(context, "Failed to save URL: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}


