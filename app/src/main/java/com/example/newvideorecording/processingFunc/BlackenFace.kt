package com.example.newvideorecording.processingFunc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.widget.Toast

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await

suspend fun FramesWithFace(context: Context, bitmap: Bitmap): Boolean {
    val image = InputImage.fromBitmap(bitmap, 0)
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .enableTracking()
        .build()
    val detector = FaceDetection.getClient(options)

    return try {
        val faces = detector.process(image).await() // Await the result of face detection
        faces.isNotEmpty() // Return true if any face is detected, false otherwise
    } catch (e: Exception) {
        false // Return false if face detection fails
    }
}


fun detectFaceAndBlackenRest(context: Context, bitmap: Bitmap, onComplete: (Bitmap?) -> Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)

    // Set options for face detection
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .enableTracking()
        .build()

    val detector = FaceDetection.getClient(options)

    detector.process(image)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                val face = faces[0] // Assuming we're working with the first detected face
                val resultBitmap = cropAndBlackenOutsideFaceContour(bitmap, face)
                onComplete(resultBitmap)
            } else {
                onComplete(null)
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Face detection failed", Toast.LENGTH_SHORT).show()
            onComplete(null)
        }
}

fun cropAndBlackenOutsideFaceContour(bitmap: Bitmap, face: Face): Bitmap {
    // Get the bounding box of the face and ensure it's within the bitmap's dimensions
    val faceBoundingBox = face.boundingBox
    val left = faceBoundingBox.left.coerceAtLeast(0)
    val top = faceBoundingBox.top.coerceAtLeast(0)
    val right = faceBoundingBox.right.coerceAtMost(bitmap.width)
    val bottom = faceBoundingBox.bottom.coerceAtMost(bitmap.height)

    // Calculate the width and height for the cropped area
    val width = right - left
    val height = bottom - top

    // Crop the bitmap to just the face bounding box
    val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
    val resultBitmap = croppedBitmap.copy(croppedBitmap.config, true)
    val canvas = Canvas(resultBitmap)

    // Create a path for the face contour
    val facePath = Path()
    face.getContour(FaceContour.FACE)?.points?.let { points ->
        // Start the path with the first point of the face contour, offset within cropped bitmap
        facePath.moveTo(
            points[0].x - left,
            points[0].y - top
        )

        // Draw lines between each point in the contour to form the boundary
        for (i in 1 until points.size) {
            facePath.lineTo(
                points[i].x - left,
                points[i].y - top
            )
        }

        // Close the path to complete the face contour
        facePath.close()
    }

    // Blacken the entire cropped image initially
    canvas.drawRect(0f, 0f, resultBitmap.width.toFloat(), resultBitmap.height.toFloat(), Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    })

    // Reveal the face area by drawing the cropped bitmap inside the face contour path
    canvas.save()
    canvas.clipPath(facePath) // Clip to the face contour
    canvas.drawBitmap(croppedBitmap, 0f, 0f, null)
    canvas.restore() // Remove clipping

    return resultBitmap
}











