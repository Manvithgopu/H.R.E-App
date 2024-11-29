package com.example.newvideorecording

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newvideorecording.backend.AppViewModel
import com.example.newvideorecording.loginpages.CreateAccountScreen
import com.example.newvideorecording.loginpages.ForgotPasswordScreen
import com.example.newvideorecording.loginpages.LoginScreen
import com.example.newvideorecording.screens.CameraScreen
import com.example.newvideorecording.screens.DashboardScreen
import com.example.newvideorecording.screens.FramesScreen
import com.example.newvideorecording.screens.GalleryScreen
import com.example.newvideorecording.screens.VideoDetailsScreen
import com.example.newvideorecording.ui.theme.NewVideoRecordingTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(!hasRequiredpermissions()){
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS,0
            )
        }

        setContent {
            NewVideoRecordingTheme {
                CameraApp()
            }
        }
    }

    private fun hasRequiredpermissions() : Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS =  arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen : CameraScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick =
                    if (currentScreen == CameraScreen.Frames || currentScreen == CameraScreen.VideoPlayer ){
                        onBackPressed
                    }else{
                        navigateUp
                    }

                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

enum class CameraScreen (@StringRes val title : Int){
                                                    Login(title = R.string.login),
    Forgotpassword(title = R.string.forgot_password),
    CreateAccount(title = R.string.create_account),
    Home(title = R.string.home),
    Camera(title = R.string.camera),
    Gallery(title = R.string.gallery),
    VideoPlayer(title = R.string.video_player),
    Frames(title = R.string.frames)
}


@Composable
fun CameraApp(viewModel: AppViewModel = androidx.lifecycle.viewmodel.compose.viewModel() ){
    val context = LocalContext.current
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance() // Initialize Firebase Auth

    // Check if a user is currently authenticated
    val isUserLoggedIn = remember { auth.currentUser != null }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CameraScreen.valueOf(
        backStackEntry?.destination?.route ?: if (isUserLoggedIn) CameraScreen.Home.name else CameraScreen.Login.name
    )

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onBackPressed = {
                    clearData(viewModel, navController) // Call clearData here
                }
            )
        }
    ) { innerPadding ->
//        val uiState by viewModel.uiState.collectAsState()

        NavHost(navController = navController,
            startDestination =if (isUserLoggedIn) CameraScreen.Home.name else CameraScreen.Login.name,
                    modifier = Modifier.padding(innerPadding)
        ){
            composable(route = CameraScreen.Login.name){
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {navController.navigate(CameraScreen.Home.name)},
                    onLoginFailed = {},
                    onForgotButtonClicked = {navController.navigate(CameraScreen.Forgotpassword.name)},
                    onCreateButtonClicked = {navController.navigate(CameraScreen.CreateAccount.name)}
                )
            }
            composable(route = CameraScreen.Forgotpassword.name){
                ForgotPasswordScreen(
                    onBackButtonClicked = {navController.navigate(CameraScreen.Login.name)},
                    onCreateButtomClicked = {navController.navigate(CameraScreen.Home.name)}
                )
            }
            composable(route = CameraScreen.CreateAccount.name){
                CreateAccountScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {navController.navigate(CameraScreen.Home.name)},
                    onBackToLogin = {navController.navigate(CameraScreen.Login.name)}
                )
            }
            composable(route = CameraScreen.Camera.name){
                CameraScreen(
                    onGalleryButtonClicked = {navController.navigate(CameraScreen.Gallery.name)},
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(route = CameraScreen.Home.name){
                DashboardScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onLogoutButtonClicked = {
                        logoutAndNavigateToStart(
                            isUserLoggedIn,
                            viewModel,
                            navController
                        )
                    }
                )
            }

            composable(route = CameraScreen.Gallery.name){
                GalleryScreen(
                    viewModel = viewModel,
                    onVideoClicked = {navController.navigate(CameraScreen.VideoPlayer.name)},
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(route = CameraScreen.VideoPlayer.name) {
                VideoDetailsScreen(
                    context = context,
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel
                )
            }

            composable(route = CameraScreen.Frames.name){
                FramesScreen(context = context,viewModel = viewModel, navController = navController)
            }

        }
    }

}

private fun logoutAndNavigateToStart(
    isUserLoggedIn : Boolean,
    viewModel: AppViewModel,
    navController: NavHostController
){
    viewModel.logoutUser()
    navController.navigate(CameraScreen.Login.name) {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
        launchSingleTop = true // Prevent multiple copies of Login screen in the back stack
    }
}

fun clearData(
    viewModel: AppViewModel,
    navController: NavHostController
) {
    // Reset the data in the ViewModel
    viewModel.resetAllData() // Ensure your ViewModel's data is reset

    // Navigate to the gallery screen and pop back to it
    navController.navigate(CameraScreen.Gallery.name) {
        // This will pop back to the Gallery screen, but keep it in the stack
        popUpTo(CameraScreen.Gallery.name) {
            inclusive = false // Do not remove the Gallery screen itself
        }
        launchSingleTop = true // Prevent multiple copies of the Gallery screen in the back stack
    }
}




