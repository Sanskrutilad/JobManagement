package com.example.jobmanagement.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.jobmanagement.ApiService
import com.example.jobmanagement.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: String,
    employerId: String,
    apiService: ApiService
) {
    // State for holding job and company name data
    var job by remember { mutableStateOf<Job?>(null) }
    var companyName by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Coroutine scope for making API calls
    val scope = rememberCoroutineScope()

    // Fetch job and company details once when the screen is loaded
    LaunchedEffect(jobId, employerId) {
        loading = true
        try {
            // Fetch job details
            val fetchedJob = apiService.getJobById(jobId)
            job = fetchedJob

            // Fetch company name
            val fetchedCompanyName = apiService.getCompanyName(employerId)
            companyName = fetchedCompanyName.companyName
        } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE1BEE7))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> {
                    Text(
                        text = "Loading job details...",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                }
                job != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFCDA2D4)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFCDA2D4))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = job?.title ?: "No title",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF73317E)
                            )
                            Text(
                                text = "Company:  ${companyName ?: "Loading..."}",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Location: ${job?.location ?: "N/A"}",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Salary: $${job?.salary ?: 0}",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Description: ${job?.description ?: "No description available"}",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "No job details available.",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
