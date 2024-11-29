package com.example.newvideorecording.processingFunc

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.newvideorecording.backend.AppViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


fun downloadVideoAndProcess(viewModel: AppViewModel,context: Context, videoUrl: String, squareSize: Int) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.getReferenceFromUrl(videoUrl)

    // Define a local file to store the downloaded video
    val localFile = File.createTempFile("tempVideo", "mp4")

    storageRef.getFile(localFile).addOnSuccessListener {
        // File downloaded successfully
        CoroutineScope(Dispatchers.IO).launch {
            processVideoFromUri(viewModel,context, Uri.fromFile(localFile), squareSize)
        }
    }.addOnFailureListener { exception ->
//        Toast.makeText(context, "Failed to download video: ${exception.message}", Toast.LENGTH_SHORT).show()
    }
}






fun processVideoFromUri(viewModel: AppViewModel, context: Context, videoUri: Uri, squareSize: Int) {

    // Run in a coroutine to avoid blocking the main thread
    CoroutineScope(Dispatchers.IO).launch {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        var frameCount = 0
        var frameNumber = 0
        val videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

        for (timeMs in 0 until videoDuration step 50){
            frameCount++
        }

        for (timeMs in 0 until videoDuration step 50) {
            frameNumber++
            val frameBitmap = retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST)

            frameBitmap?.let {
                val facedetected = FramesWithFace(context,it)
                viewModel.updateFaceDetectedFrames(facedetected)
                // Ensure Toast runs on the main thread
                Handler(Looper.getMainLooper()).post {
                    if (facedetected) {
                        detectFaceAndBlackenRest(context,it) { resultBitmap ->
                            if (resultBitmap != null) {
                                // Handle the result bitmap here
                                // For example, set it to an ImageView to display the processed image
                                viewModel.updateBitmap(resultBitmap)
                                val (array1,array2,array3) = processFaceIntoSquares(context, resultBitmap, squareSize)
                                viewModel.updateFrameData(frameCount,frameNumber,array1,array2,array3,facedetected)
                            } else {
//                                Toast.makeText(this, "No face detected or face detection failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
//                        Toast.makeText(context, "Face not Detected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Extracted frames = $frameCount", Toast.LENGTH_SHORT).show()
        }

        retriever.release()
    }
}


fun processFaceIntoSquares(context: Context, bitmap: Bitmap, squareSize: Int): Triple<Array<IntArray>, Array<IntArray>, Array<IntArray>> {
    val width = bitmap.width
    val height = bitmap.height

    // Calculate how many squares fit in width and height
    val numRows = height / squareSize
    val numCols = width / squareSize

    // Create a 2D array for storing RGB values
    val rgbArray = Array(squareSize) { Array(squareSize) { IntArray(3) } }
    val redArray = Array(squareSize) { IntArray(squareSize) }
    val greenArray = Array(squareSize) { IntArray(squareSize)}
    val blueArray = Array(squareSize) { IntArray(squareSize)}

    // Loop through the image and process each square
    for (row in 0 until squareSize) {
        for (col in 0 until squareSize) {
            // Extract the square region
            val startX = col * numCols
            val startY = row * numRows

            // Get average RGB values for the current square
            val avgColor = getAverageRGB(bitmap, startX, startY, numCols , numRows)

            // Store the RGB values in the array
            redArray[row][col] = avgColor[0] // Red
            greenArray[row][col] = avgColor[1] // Green
            blueArray[row][col] = avgColor[2] // Blue
        }
    }

//    withContext(Dispatchers.Main) {
//        Toast.makeText(context, " Red = ${redArray[0][0]} , Green = ${greenArray[0][0]} , Blue = ${blueArray[0][0]}", Toast.LENGTH_SHORT).show()
//    }
    return Triple(redArray,greenArray,blueArray)
}

fun getAverageRGB(bitmap: Bitmap, startX: Int, startY: Int, numCols : Int , numRows : Int): IntArray {
    var redSum = 0
    var greenSum = 0
    var blueSum = 0
    var pixelCount = 0


    // Loop through each pixel in the square to calculate the average RGB
    for (x in startX until startX + numCols) {
        for (y in startY until startY + numRows) {
            if (x < bitmap.width && y < bitmap.height) { // Ensure we don't go out of bounds
                val pixel = bitmap.getPixel(x, y)

                // Extract RGB components
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                // Sum RGB values
                redSum += red
                greenSum += green
                blueSum += blue
                pixelCount++
            }
        }
    }

    // Calculate average RGB values
    val avgRed = redSum / pixelCount
    val avgGreen = greenSum / pixelCount
    val avgBlue = blueSum / pixelCount

    return intArrayOf(avgRed, avgGreen, avgBlue)
}