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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmanagement.ApiService
import com.example.jobmanagement.Company
import com.example.jobmanagement.FcmToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@Composable
fun CompanyRegistrationScreen(
    navController: NavController,
    apiService: ApiService
) {
    var companyName by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var foundedYear by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var revenue by remember { mutableStateOf("") }
    var companyType by remember { mutableStateOf("") }
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
                text = "Register Company",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(value = companyName, onValueChange = { companyName = it }, label = { Text("Company Name") })
            OutlinedTextField(value = industry, onValueChange = { industry = it }, label = { Text("Industry") })
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
            OutlinedTextField(value = foundedYear, onValueChange = { foundedYear = it }, label = { Text("Founded Year") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

            // Validate phone number
            val phoneRegex = "^\\+?[0-9]{10,15}$".toRegex()
            if (!phone.matches(phoneRegex)) {
                errorMessage = "Invalid phone number format"
            }

            OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Company Size (No. of Employees)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = revenue, onValueChange = { revenue = it }, label = { Text("Revenue (Optional)") })
            OutlinedTextField(value = companyType, onValueChange = { companyType = it }, label = { Text("Company Type") })

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (companyName.isBlank() || industry.isBlank() || location.isBlank() || email.isBlank() || phone.isBlank() || companyType.isBlank() || password.isBlank()) {
                        errorMessage = "All fields must be filled"
                        return@Button
                    }

                    if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        return@Button
                    }

                    // Validate password strength
                    val passwordRegex = "(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}".toRegex()
                    if (!password.matches(passwordRegex)) {
                        errorMessage = "Password must include at least one uppercase letter, one number, and one special character"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = task.result?.user
                                val companyUid = firebaseUser?.uid ?: return@addOnCompleteListener

                                val company = Company(
                                    uid = companyUid,
                                    companyName = companyName,
                                    industry = industry,
                                    location = location,
                                    foundedYear = foundedYear.toIntOrNull() ?: 0,
                                    email = email,
                                    phone = phone,
                                    size = size.toIntOrNull() ?: 0,
                                    companyType = companyType
                                )

                                coroutineScope.launch {
                                    try {
                                        apiService.registerCompany(company)

                                        // Fetch and send FCM token
                                        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                                            if (tokenTask.isSuccessful) {
                                                val token = tokenTask.result
                                                Log.d("FCM", "Company Token: $token")
                                                coroutineScope.launch {
                                                    try {
                                                        apiService.registerCompanyToken(companyUid, FcmToken(token))
                                                        Log.d("FCM", "Company FCM token sent to backend.")
                                                    } catch (e: Exception) {
                                                        Log.e("FCM", "Error sending company token: ${e.message}")
                                                    }
                                                }
                                            } else {
                                                Log.e("FCM", "Failed to fetch FCM token", tokenTask.exception)
                                            }
                                        }

                                        isLoading = false
                                        navController.navigate("companyjob_list/${companyUid}")
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = e.message ?: "Registration failed"
                                        Log.e("CompanyRegistration", "Error: $errorMessage")
                                    }
                                }
                            } else {
                                isLoading = false
                                errorMessage = task.exception?.localizedMessage ?: "Registration failed"
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
