package com.example.jobmanagement.screen

import android.util.Log
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
import androidx.navigation.NavController
import com.example.jobmanagement.ApiService
import com.example.jobmanagement.Job
import com.example.jobmanagement.jobviewmodel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobScreen(
    navController: NavController,
    apiService: ApiService,
    jobId: String? = null,
    companyId: String?
) {
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var companyIdState by remember { mutableStateOf(companyId ?: "") }
    var companyName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var salaryError by remember { mutableStateOf(false) }

    Log.d("AddEditJobScreen", "Initialized with jobId: $jobId, companyId: $companyIdState")

    // Fetch company name if companyIdState is not empty
    LaunchedEffect(companyIdState) {
        if (companyIdState.isNotEmpty()) {
            try {
                Log.d("AddEditJobScreen", "Fetching company name for companyId: $companyIdState")
                val companyResponse = apiService.getCompanyName(companyIdState)
                companyName = companyResponse.companyName
                Log.d("AddEditJobScreen", "Fetched company name: $companyName")
            } catch (e: Exception) {
                Log.e("AddEditJobScreen", "Error fetching company name: ${e.message}")
            }
        }
    }

    // Fetch job details if jobId is provided (Edit Mode)
    LaunchedEffect(jobId) {
        if (jobId != null) {
            try {
                Log.d("AddEditJobScreen", "Fetching job details for jobId: $jobId")
                val job = apiService.getJobById(jobId)
                job?.let {
                    title = it.title
                    description = it.description
                    companyIdState = it.companyId
                    location = it.location
                    salary = it.salary.toString()

                    Log.d("AddEditJobScreen", "Job details loaded: $it")

                    // Fetch company name again for the job
                    val companyResponse = apiService.getCompanyName(it.companyId)
                    companyName = companyResponse.companyName
                    Log.d("AddEditJobScreen", "Company Name for job: $companyName")
                }
            } catch (e: Exception) {
                Log.e("AddEditJobScreen", "Error fetching job details: ${e.message}")
            }
        } else {
            Log.d("AddEditJobScreen", "jobId is null, skipping job details fetch.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (jobId != null) "Edit Job" else "Add Job", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE1BEE7))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (jobId != null) "Edit Job Details" else "Add New Job",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE1BEE7)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Auto-filled company field (Not editable)
            OutlinedTextField(
                value = companyName,
                onValueChange = {},
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salary,
                onValueChange = {
                    salary = it
                    salaryError = it.isNotEmpty() && (it.toIntOrNull() == null || it.toInt() < 0)
                },
                label = { Text("Salary") },
                modifier = Modifier.fillMaxWidth(),
                isError = salaryError
            )
            if (salaryError) {
                Text(text = "Invalid salary input", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!salaryError) {
                        val job = Job(
                            _id = jobId ?: "",
                            title = title,
                            description = description,
                            company = companyName,
                            companyId = companyIdState,
                            location = location,
                            salary = salary.toIntOrNull() ?: 0
                        )

                        coroutineScope.launch {
                            try {
                                Log.d("AddEditJobScreen", "Saving job: $job")
                                if (jobId != null) {
                                    apiService.updateJob(jobId, job)
                                    Log.d("AddEditJobScreen", "Job updated successfully.")
                                } else {
                                    apiService.createJob(job)
                                    Log.d("AddEditJobScreen", "Job created successfully.")
                                }
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e("AddEditJobScreen", "Error saving job: ${e.message}")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1BEE7)),
                shape = RoundedCornerShape(8.dp),
                enabled = !salaryError
            ) {
                Text(
                    text = if (jobId != null) "Update Job" else "Save Job",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
