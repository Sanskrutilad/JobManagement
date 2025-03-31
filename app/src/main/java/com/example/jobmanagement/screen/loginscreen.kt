package com.example.jobmanagement.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController, userType: String) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Scaffold{
            innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login as ${userType.replaceFirstChar { it.uppercase() }}", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    isLoading = true
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val firebaseUser = FirebaseAuth.getInstance().currentUser
                                val companyUid = firebaseUser?.uid ?: ""

                                val nextScreen = if (userType == "candidate") {
                                    "candidatejob_list"
                                } else {
                                    "companyjob_list/${companyUid}" // Pass UID for company
                                }
                                navController.navigate(nextScreen)
                            } else {
                                errorMessage = task.exception?.localizedMessage ?: "Login failed"
                                Log.e("LoginScreen", "Error: ${task.exception}")
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Logging in..." else "Login")
            }


            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    val nextScreen = if (userType == "candidate") "candidateRegistration" else "companyRegistration"
                    navController.navigate(nextScreen)
                }
            ) {
                Text("Don't have an account? Sign Up")
            }
        }
    }

}

