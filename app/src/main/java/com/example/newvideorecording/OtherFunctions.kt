package com.example.newvideorecording

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun FaceScannerOverlay(
    color: Color

) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Fills the entire screen
            .padding(16.dp) // Padding around the square to position it centrally or at the top

    ) {
        Box(
            modifier = Modifier
                .width(300.dp) // Size of the QR code scanning square
                .height(400.dp)
                .align(Alignment.Center) // Align the square to the top center of the screen
                .border(
                    width = 10.dp, // Thickness of the border
                    color = color, // Color of the border
                    shape = RectangleShape // The square shape,

                )

        ){


        }
    }
}