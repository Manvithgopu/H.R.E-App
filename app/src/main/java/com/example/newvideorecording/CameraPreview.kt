package com.example.newvideorecording

import android.content.Context
import android.util.Size
import com.google.mlkit.vision.face.Face
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors



    @Composable
fun CameraPreview (
    controller: LifecycleCameraController,
    onFacesDetected : (List<Face>,Int,Int) -> Unit,
    modifier: Modifier = Modifier
){

    val controller = controller
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    val x by remember {
        mutableStateOf(true)
    }
//    val executor = Executors.newSingleThreadExecutor()
//    val analyzer =  imageAnalyzer(context,onFacesDetected)
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(x) {
        controller.cameraSelector = cameraSelector
        controller.bindToLifecycle(lifeCycleOwner)
    }

    val cameraProvider = cameraProviderFuture.get()

    DisposableEffect(Unit) {
        onDispose {
            controller.unbind() // Unbind all use cases when composable leaves the composition
        }
    }

//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    AndroidView(
        factory = {
           PreviewView(it).apply {
               this.controller = controller

               try {
                   cameraProvider.unbindAll() // Unbind before rebinding
                   cameraProvider.bindToLifecycle(
                       lifeCycleOwner,
                       cameraSelector,
                       imageAnalyzer(context,onFacesDetected) // Attach analyzer for face detection
                   )
               } catch (e: Exception) {
                   e.printStackTrace() // Handle potential issues
               }

           }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalGetImage::class)
fun imageAnalyzer(context: Context, onFacesDetected: (List<Face>, Int, Int) -> Unit): ImageAnalysis {
    val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
    )


    return ImageAnalysis.Builder()
        .build()
        .also { imageAnalysis ->
            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    faceDetector.process(image)
                        .addOnSuccessListener { faces ->
                            val imageWidth = imageProxy.width
                            val imageHeight = imageProxy.height
                            onFacesDetected(faces , imageWidth ,imageHeight) // Callback with detected faces
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace() // Log or handle failure
                        }
                        .addOnCompleteListener {
                            imageProxy.close() // Ensure the proxy is closed after each frame
                        }
                }
            }
        }
}
