package com.example.jobmanagement.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmanagement.ApiService
import com.example.jobmanagement.Candidate
import com.example.jobmanagement.FcmToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@Composable
fun CandidateRegistrationScreen(
    navController: NavController,
    apiService: ApiService
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register as Candidate",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(value = skills, onValueChange = { skills = it }, label = { Text("Skills (comma-separated)") })
            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text("Years of Experience") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(value = education, onValueChange = { education = it }, label = { Text("Education") })
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || email.isBlank() || phone.isBlank() || skills.isBlank() || education.isBlank() || location.isBlank() || password.isBlank()) {
                        errorMessage = "All fields must be filled"
                        return@Button
                    }

                    // Validate phone number format (e.g., +91XXXXXXXXXX)
                    val phoneRegex = "^\\+?[0-9]{10,15}$".toRegex()
                    if (!phone.matches(phoneRegex)) {
                        errorMessage = "Invalid phone number format"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    val auth = FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                val candidateUid = firebaseUser?.uid ?: return@addOnCompleteListener

                                val candidate = Candidate(
                                    uid = candidateUid,
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    skills = skills.split(",").map { it.trim() },
                                    experience = experience.toIntOrNull() ?: 0,
                                    education = education,
                                    location = location
                                )

                                coroutineScope.launch {
                                    try {
                                        apiService.registerCandidate(candidate)

                                        // Fetch and register FCM token
                                        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                                            if (tokenTask.isSuccessful) {
                                                val token = tokenTask.result
                                                Log.d("FCM", "Token: $token")

                                                coroutineScope.launch {
                                                    try {
                                                        apiService.registerCandidateToken(candidateUid, FcmToken(token))
                                                        Log.d("FCM", "Token registered successfully")
                                                    } catch (e: Exception) {
                                                        Log.e("FCM", "Error sending token to backend: ${e.message}")
                                                    }
                                                }
                                            } else {
                                                Log.e("FCM", "Fetching FCM token failed", tokenTask.exception)
                                            }
                                        }

                                        isLoading = false
                                        navController.navigate("candidatejob_list")
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = e.message ?: "Registration failed"
                                        Log.e("CandidateRegistration", "Error: $errorMessage")
                                    }
                                }
                            } else {
                                isLoading = false
                                errorMessage = task.exception?.message ?: "Registration failed"
                                Log.e("FirebaseAuth", "Error: ${task.exception?.message}")
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register")
                }
            }
        }
    }
}
