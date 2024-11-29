package com.example.newvideorecording.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import com.example.newvideorecording.CameraScreen
import com.example.newvideorecording.screens.CameraScreen
import com.example.newvideorecording.backend.AppViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: AppViewModel,
    onLogoutButtonClicked : () -> Unit = {}
) {
    // State and scope for controlling the drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // ModalDrawer to display the side drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // Enable swipe gestures when the drawer is open
        drawerContent = {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Hello ${uiState.currentUser}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProfilePicture(onProfileClick = {
                                    scope.launch { drawerState.open() }
                                })
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(text = "Welcome", fontWeight = FontWeight.Bold, fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(100.dp))
                                IconButton(onClick = {
                                    onLogoutButtonClicked()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Logout, // Use the appropriate icon for logout
                                        contentDescription = "Logout"
                                    )
                                }
                            }
                        },
//                        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White),
                    )
                },
                bottomBar = {
                    BottomNavigationBar(navController)
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        MainCard()
                        Spacer(modifier = Modifier.height(16.dp))
                        HeartRateCard()
                        Spacer(modifier = Modifier.height(16.dp))
                        FluctuationsCard()
                    }
                }
            )
        }
    )
}

@Composable
fun ProfilePicture(onProfileClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .clickable { onProfileClick() },  // Trigger the onProfileClick function when clicked
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Icon",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}





@Composable
fun MainCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            Text(text = "HRE AI is on the way", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeartRateCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan)
        ) {
            Text(text = "Recent Heart Rates", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FluctuationsCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)
        ) {
            Text(text = "Heart Rate Fluctuations", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(com.example.newvideorecording.CameraScreen.Home, Icons.Default.Home),
        BottomNavItem(com.example.newvideorecording.CameraScreen.Camera, Icons.Default.CameraAlt),
        BottomNavItem(com.example.newvideorecording.CameraScreen.Gallery, Icons.Default.Photo),
        BottomNavItem(com.example.newvideorecording.CameraScreen.VideoPlayer, Icons.Default.History)
    )
    NavigationBar(
        containerColor = Color.Cyan,
        tonalElevation = 30.dp,
        modifier = Modifier
            .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(item.icon, contentDescription = item.screen.name)
                },
                selected = currentRoute == item.screen.name,
                onClick = {
                    navController.navigate(item.screen.name) {
                        popUpTo(com.example.newvideorecording.CameraScreen.Home.name) {
                            saveState = true // Optional: if you want to keep the state
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val screen : CameraScreen, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
@Preview
fun DashboardScreenPreview() {
    DashboardScreen(navController = NavHostController(context = LocalContext.current) , viewModel = AppViewModel())
}