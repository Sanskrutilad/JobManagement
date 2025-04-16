package com.example.jobmanagement.screen

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Composable
fun LoginScreen(navController: NavController, userType: String) {
    val auth = FirebaseAuth.getInstance()

    var loginMethod by remember { mutableStateOf("email") } // "email" or "phone"
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    var verificationId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login as ${userType.replaceFirstChar { it.uppercase() }}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(
                    onClick = { loginMethod = "email" },
                    colors = ButtonDefaults.buttonColors(
                        if (loginMethod == "email") MaterialTheme.colorScheme.primary else Color.LightGray
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Email Login")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { loginMethod = "phone" },
                    colors = ButtonDefaults.buttonColors(
                        if (loginMethod == "phone") MaterialTheme.colorScheme.primary else Color.LightGray
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Phone Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loginMethod == "email") {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number (+91...)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (verificationId != null) {
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("Enter OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    errorMessage = null
                    isLoading = true

                    if (loginMethod == "email") {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: ""
                                    val route = if (userType == "candidate") "candidatejob_list" else "companyjob_list/$uid"
                                    navController.navigate(route)
                                } else {
                                    errorMessage = task.exception?.localizedMessage ?: "Login failed"
                                }
                            }
                    } else {
                        if (verificationId == null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val client = OkHttpClient()
                                    val encodedPhone = java.net.URLEncoder.encode(phoneNumber, "UTF-8")
                                    val url = "http://192.168.71.52:5000/check-phone?phone=$encodedPhone"
                                    val request = Request.Builder().url(url).get().build()

                                    val response = client.newCall(request).execute()
                                    val body = response.body?.string()

                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful && body != null) {
                                            val json = JSONObject(body)
                                            if (json.getBoolean("exists")) {
                                                val options = PhoneAuthOptions.newBuilder(auth)
                                                    .setPhoneNumber(phoneNumber)
                                                    .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                                                    .setActivity(context as Activity)
                                                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                                            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                                                                isLoading = false
                                                                if (task.isSuccessful) {
                                                                    val uid = auth.currentUser?.uid ?: ""
                                                                    val route = if (userType == "candidate") "candidatejob_list" else "companyjob_list/$uid"
                                                                    navController.navigate(route)
                                                                } else {
                                                                    errorMessage = task.exception?.localizedMessage
                                                                }
                                                            }
                                                        }

                                                        override fun onVerificationFailed(e: FirebaseException) {
                                                            isLoading = false
                                                            errorMessage = e.localizedMessage
                                                        }

                                                        override fun onCodeSent(verificationIdParam: String, token: PhoneAuthProvider.ForceResendingToken) {
                                                            verificationId = verificationIdParam
                                                            isLoading = false
                                                        }
                                                    })
                                                    .build()
                                                PhoneAuthProvider.verifyPhoneNumber(options)
                                            } else {
                                                isLoading = false
                                                errorMessage = "Phone number not registered."
                                            }
                                        } else {
                                            isLoading = false
                                            errorMessage = "Server error. Please try again."
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        errorMessage = "Error: ${e.localizedMessage}"
                                    }
                                }
                            }
                        } else {
                            // ✅ Verify OTP
                            val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
                            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: ""
                                    val route = if (userType == "candidate") "candidatejob_list" else "companyjob_list/$uid"
                                    navController.navigate(route)
                                } else {
                                    errorMessage = task.exception?.localizedMessage
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "Processing..."
                    else if (loginMethod == "phone" && verificationId == null) "Send OTP"
                    else "Login"
                )
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

            Spacer(modifier = Modifier.height(8.dp))

            // Required for Firebase Phone Auth (reCAPTCHA)
            AndroidView(factory = { context ->
                val frameLayout = android.widget.FrameLayout(context)
                frameLayout.id = android.view.View.generateViewId()
                frameLayout
            }, modifier = Modifier.size(1.dp))
        }
    }
}
