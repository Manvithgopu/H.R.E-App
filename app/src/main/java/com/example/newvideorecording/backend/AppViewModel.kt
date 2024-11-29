package com.example.newvideorecording.backend

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState : StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _videoList = MutableStateFlow(VideoData())
    val videoList: StateFlow<VideoData> = _videoList.asStateFlow()

    private val _frameList = MutableStateFlow(FrameData())
    val frameList : StateFlow<FrameData> = _frameList.asStateFlow()

    private val _faceDetectionFrames = MutableStateFlow(FaceDetectedFrames())
    val faceDetectedList : StateFlow<FaceDetectedFrames> = _faceDetectionFrames.asStateFlow()

    private val _blackenedBitmap = MutableStateFlow(BlackenedAreaBitmap())
    val blackenedAreaBitmap : StateFlow<BlackenedAreaBitmap>  = _blackenedBitmap.asStateFlow()

    init {
        // Check if a user is currently logged in and update the UI state
        FirebaseAuth.getInstance().currentUser?.let { user ->
            _uiState.value = _uiState.value.copy(currentUser = user.email ?: "Guest")
        }
    }

    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        // Optionally, reset the UI state
        _uiState.value = AppUiState() // Resetting user info
    }

    fun registerUser(email: String, password: String, context: Context) {
        // Check if password and confirmPassword match
        if (_uiState.value.userPassword == _uiState.value.confirmPassword) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }

    fun loginUser(email: String, password: String, context: Context, onLoginSuccess: () -> Unit, onLoginFailed: () -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    onLoginFailed()
                }
            }
    }

    fun updateVideoList(videoUrl: String,videoName: String) {
        // Get the current list and append the new video
        _videoList.value = _videoList.value.copy(videoUrl = videoUrl, videoName = videoName) // Update the state with the new list
    }

    // Handle input for username, password, and confirmPassword
    fun onUsernameChanged(newUsername: String) {
        _uiState.value = _uiState.value.copy(currentUser = newUsername)
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(userPassword = newPassword)
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = newConfirmPassword)
    }


    fun updateFrameData(totalFrames : Int,frameNumber : Int , redArray : Array<IntArray> , greenArray : Array<IntArray> , blueArray : Array<IntArray>, faceDetected : Boolean){
        for(i in 0 until 11 ){
            for (j in 0 until 11){
                redArray[i][j] = redArray[i][j] + frameList.value.redArray[i][j]
                greenArray[i][j] = greenArray[i][j] + frameList.value.greenArray[i][j]
                blueArray[i][j] = blueArray[i][j] + frameList.value.blueArray[i][j]

//                redArray[i][j] = (redArray[i][j] + (frameList.value.redArray[i][j]*totalFrames))/totalFrames
//                greenArray[i][j] = (greenArray[i][j] + (frameList.value.greenArray[i][j]*totalFrames))/totalFrames
//                blueArray[i][j] = (blueArray[i][j] + (frameList.value.blueArray[i][j]*totalFrames))/totalFrames
            }
        }
        var x = frameList.value.faceDetectedFrames
        if(faceDetected){
            x += 1
        }

        _frameList.value = _frameList.value.copy(totalFrames,frameNumber = frameNumber,redArray,greenArray,blueArray,x , false)
    }

    fun updateFaceDetectedFrames(faceDetected: Boolean){
        var x = faceDetectedList.value.faceDetectedFrames
        if (faceDetected){
            x += 1
        }
        _faceDetectionFrames.value = _faceDetectionFrames.value.copy(x)
    }

    fun updateBitmap(Bitmap : Bitmap){
        _blackenedBitmap.value = _blackenedBitmap.value.copy(blackedBitmap = Bitmap)
    }

    private fun resetVideoData(): VideoData {
        return VideoData() // Returns a new instance with default values
    }

    private fun resetFrameData(): FrameData {
        return FrameData() // Returns a new instance with default values
    }

    private fun resetFaceDetectedFrames(): FaceDetectedFrames {
        return FaceDetectedFrames() // Returns a new instance with default values
    }

    private fun resetBlackenedBitmap(): BlackenedAreaBitmap {
        return BlackenedAreaBitmap() // Returns a new instance with a null bitmap
    }

    fun resetAllData() {
        _videoList.value = resetVideoData() // Reset video list
        _frameList.value = resetFrameData() // Reset frame list
        _faceDetectionFrames.value = resetFaceDetectedFrames() // Reset face detection frames
        _blackenedBitmap.value = resetBlackenedBitmap() // Reset blackened bitmap
    }

}