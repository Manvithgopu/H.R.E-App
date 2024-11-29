package com.example.newvideorecording.loginpages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newvideorecording.R
import com.example.newvideorecording.backend.AppViewModel

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onLoginSuccess: () -> Unit = {},
    onLoginFailed: () -> Unit = {},
    onForgotButtonClicked : () -> Unit = {},
    onCreateButtonClicked : () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.app_background),
                contentScale = ContentScale.Crop  // Ensures the image fills the entire background
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(30.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(20.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
//                backgroundColor = Color.Transparent // Background can be controlled externally
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    CustomTextField(
                        value = uiState.currentUser,
                        onValueChanged = {viewModel.onUsernameChanged(it)},
                        label = "Username",
                        icon = Icons.Default.Person, // Replace with your drawable
                        inputType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CustomTextField(
                        value = uiState.userPassword,
                        onValueChanged = {viewModel.onPasswordChanged(it)},
                        label = "Passwordd",
                        icon = Icons.Default.Lock, // Replace with your drawable
                        inputType = KeyboardType.Password
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Forgot Password",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(top = 10.dp, end = 20.dp)
                            .align(Alignment.End)
                            .clickable { onForgotButtonClicked() },
                        color = Color.Red
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.loginUser(
                                email = uiState.currentUser,
                                password = uiState.userPassword,
                                context = context,
                                onLoginSuccess = onLoginSuccess,
                                onLoginFailed = onLoginFailed
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors( colorResource(id = R.color.teal_200))
                    ) {
                        Text(
                            text = "Login",
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Don't have an Account?",
                fontSize = 15.sp,
                color = Color.Black
            )

            Text(
                text = "Create account",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable { onCreateButtonClicked() },
                color = colorResource(id = R.color.teal_200)
            )
        }
    }
}

@Composable
fun CustomTextField(
    onValueChanged : (String) -> Unit ,
    value : String,
    label: String,
    icon: ImageVector,
    inputType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        keyboardOptions = KeyboardOptions(keyboardType = inputType),

        isError = false,
        keyboardActions = KeyboardActions(
            onDone = { }
        ),
        shape = RoundedCornerShape(80.dp),

        )
}

@Preview
@Composable
fun LoginPreview(){
    LoginScreen(viewModel = AppViewModel())
}



