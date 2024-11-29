package com.example.newvideorecording.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.newvideorecording.backend.AppViewModel
import com.example.newvideorecording.clearData

//@Composable
//fun FramesScreen(
//    context: Context,
//    viewModel: AppViewModel
//) {
//    val uiState by viewModel.frameList.collectAsState()
//    val faceDetectedUiState by viewModel.faceDetectedList.collectAsState()
//    val blackenedUiState by viewModel.blackenedAreaBitmap.collectAsState()
//
//    LazyColumn {
//        item {
//            Spacer(modifier = Modifier.padding(top = 40.dp))
//            Text(
//                text = "Total frames: ${uiState.totalFrames}",
//                style = MaterialTheme.typography.h5
//            )
//            Spacer(modifier = Modifier.padding(top = 50.dp))
//            Text(
//                text = "Face Detected frames : ${faceDetectedUiState.faceDetectedFrames}",
//                style = MaterialTheme.typography.h5
//            )
//            Spacer(modifier = Modifier.padding(top = 50.dp))
//            Text(text = "Average Red Array: ", style = MaterialTheme.typography.h5)
//            Spacer(modifier = Modifier.padding(top = 50.dp))
//
//            // Display blackened bitmap if available
//            blackenedUiState.blackedBitmap?.let { bitmap ->
//                Spacer(modifier = Modifier.height(20.dp))
//                Image(
//                    painter = BitmapPainter(bitmap.asImageBitmap()),
//                    contentDescription = "Detected Face Bitmap",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp)
//                        .padding(16.dp)
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//        }
//
//        // Display Red Array
//        item {
//            Text(text = "Red Array:", style = MaterialTheme.typography.h6)
//        }
//        items(uiState.redArray.size) { index ->
//            val row = uiState.redArray[index].map { value ->
//                if (faceDetectedUiState.faceDetectedFrames != 0) {
//                    value / faceDetectedUiState.faceDetectedFrames
//                } else {
//                    0 // Default value if faceDetectedFrames is zero
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .horizontalScroll(rememberScrollState())
//                    .padding(vertical = 4.dp)
//            ) {
//                Text(
//                    text = "Row $index: ",
//                    style = MaterialTheme.typography.body1,
//                    modifier = Modifier.width(80.dp)
//                )
//
//                row.forEach { value ->
//                    Text(
//                        text = value.toString(),
//                        style = MaterialTheme.typography.body1,
//                        modifier = Modifier.width(50.dp)
//                    )
//                }
//            }
//        }
//
//        // Display Green Array
//        item {
//            Spacer(modifier = Modifier.height(50.dp))
//            Text(text = "Average Green Array: ", style = MaterialTheme.typography.h5)
//        }
//        items(uiState.greenArray.size) { index ->
//            val row = uiState.greenArray[index].map { value ->
//                if (faceDetectedUiState.faceDetectedFrames != 0) {
//                    value / faceDetectedUiState.faceDetectedFrames
//                } else {
//                    0
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .horizontalScroll(rememberScrollState())
//                    .padding(vertical = 4.dp)
//            ) {
//                Text(
//                    text = "Row $index: ",
//                    style = MaterialTheme.typography.body1,
//                    modifier = Modifier.width(80.dp)
//                )
//
//                row.forEach { value ->
//                    Text(
//                        text = value.toString(),
//                        style = MaterialTheme.typography.body1,
//                        modifier = Modifier.width(50.dp)
//                    )
//                }
//            }
//        }
//
//        // Display Blue Array
//        item {
//            Spacer(modifier = Modifier.height(50.dp))
//            Text(text = "Average Blue Array: ", style = MaterialTheme.typography.h5)
//        }
//        items(uiState.blueArray.size) { index ->
//            val row = uiState.blueArray[index].map { value ->
//                if (faceDetectedUiState.faceDetectedFrames != 0) {
//                    value / faceDetectedUiState.faceDetectedFrames
//                } else {
//                    0
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .horizontalScroll(rememberScrollState())
//                    .padding(vertical = 4.dp)
//            ) {
//                Text(
//                    text = "Row $index: ",
//                    style = MaterialTheme.typography.body1,
//                    modifier = Modifier.width(80.dp)
//                )
//
//                row.forEach { value ->
//                    Text(
//                        text = value.toString(),
//                        style = MaterialTheme.typography.body1,
//                        modifier = Modifier.width(50.dp)
//                    )
//                }
//            }
//        }
//
////        Spacer(modifier = Modifier.height(100.dp))
//    }
//}


@Composable
fun FramesScreen(
    context: Context,
    navController: NavHostController,
    viewModel: AppViewModel,

) {
    val uiState by viewModel.frameList.collectAsState()
    val faceDetectedUiState by viewModel.faceDetectedList.collectAsState()
    val blackenedUiState by viewModel.blackenedAreaBitmap.collectAsState()

    BackHandler {
        // Navigate to the gallery page
        clearData(viewModel,navController)
    }

    LazyColumn {
        item {
            Spacer(modifier = Modifier.padding(top = 40.dp))
            Text(
                text = "Total frames: ${uiState.totalFrames}",
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.padding(top = 50.dp))
            Text(
                text = "Face Detected frames : ${faceDetectedUiState.faceDetectedFrames}",
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.padding(top = 50.dp))
            Text(text = "Average Red Array: ", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.padding(top = 50.dp))

            // Display blackened bitmap if available
            blackenedUiState.blackedBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = "Detected Face Bitmap",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Display the red, green, and blue arrays
            uiState.redArray.forEachIndexed { index, row ->
                Text(
                    text = "Row $index: ${
                        row.joinToString(" ") { value ->
                            if (faceDetectedUiState.faceDetectedFrames != 0) {
                                (value / faceDetectedUiState.faceDetectedFrames).toString()
                            } else {
                                "0" // Default value to show if faceDetectedFrames is zero
                            }
                        }
                    }",
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Text(text = "Average Green Array: ", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(50.dp))
            uiState.greenArray.forEachIndexed { index, row ->
                Text(
                    text = "Row $index: ${
                        row.joinToString(" ") { value ->
                            if (faceDetectedUiState.faceDetectedFrames != 0) {
                                (value / faceDetectedUiState.faceDetectedFrames).toString()
                            } else {
                                "0" // Default value to show if faceDetectedFrames is zero
                            }
                        }
                    }",
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Text(text = "Average Blue Array: ", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(50.dp))
            uiState.blueArray.forEachIndexed { index, row ->
                Text(
                    text = "Row $index: ${
                        row.joinToString(" ") { value ->
                            if (faceDetectedUiState.faceDetectedFrames != 0) {
                                (value / faceDetectedUiState.faceDetectedFrames).toString()
                            } else {
                                "0" // Default value to show if faceDetectedFrames is zero
                            }
                        }
                    }",
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}







