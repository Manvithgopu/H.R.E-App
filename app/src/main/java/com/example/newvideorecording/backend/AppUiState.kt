package com.example.newvideorecording.backend

import android.graphics.Bitmap

data class AppUiState(
    val currentUser : String = "",
    val userPassword : String = "",
    val confirmPassword: String = "",
    val videoList: List<VideoData> = emptyList()
)

data class VideoData(
    val videoUrl: String = "",
    val videoName: String = "",
)

data class FrameData(
    val totalFrames : Int = 0,
    val frameNumber : Int = 0,
    val redArray: Array<IntArray> = Array(11) { IntArray(11) { 0 } },
    val greenArray: Array<IntArray> = Array(11) { IntArray(11) { 0 } },
    val blueArray: Array<IntArray> = Array(11) { IntArray(11) { 0 } },
    val faceDetectedFrames : Int = 0,
    var printed : Boolean = false
)

data class FaceDetectedFrames(
    val faceDetectedFrames: Int = 0
)
data class BlackenedAreaBitmap(
    val blackedBitmap : Bitmap? = null
)
