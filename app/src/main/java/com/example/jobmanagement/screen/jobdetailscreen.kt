package com.example.jobmanagement.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobmanagement.jobviewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: String,
    employerId: String,
    viewModel: jobviewmodel = viewModel()
) {
    // Observe the jobById LiveData
    val job = viewModel.jobById.observeAsState()
    val companyName = viewModel.companyName.observeAsState()

    // Fetch job details once when the screen is loaded
    LaunchedEffect(jobId) {
        viewModel.getJobById(jobId)
    }
    LaunchedEffect(employerId) {
        viewModel.getCompanyName(employerId)
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
            job.value?.let { jobData ->
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
                            text = jobData.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF73317E)
                        )
                        Text(
                            text = "Company:  ${companyName.value ?: "Loading..."}",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Location: ${jobData.location}",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Salary: $${jobData.salary}",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Description: ${jobData.description}",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            } ?: Text(
                text = "Loading job details...",
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
    }
}
