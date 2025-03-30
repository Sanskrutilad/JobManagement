package com.example.jobmanagement.screen

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
import androidx.navigation.NavController
import com.example.jobmanagement.Job
import com.example.jobmanagement.jobviewmodel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobScreen(navController: NavController, viewModel: jobviewmodel, jobId: String? = null) {
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var companyId by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid ?: "") }
    var companyName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var salaryError by remember { mutableStateOf(false) }  // Error state for salary

    // Fetch job details if editing
    LaunchedEffect(jobId) {
        if (jobId != null) {
            val job = viewModel.jobs.value?.find { it._id == jobId }
            job?.let {
                title = it.title
                description = it.description
                companyId = it.companyId
                location = it.location
                salary = it.salary.toString()

                // Fetch company name asynchronously
                companyName = (viewModel.getCompanyName(it.companyId) ?: "Unknown Company")
            }
        } else {
            // Auto-fill company name when adding a new job
            companyName = (viewModel.getCompanyName(companyId) ?: "Unknown Company")
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
                enabled = false  // Make it non-editable
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
                    salaryError = it.toIntOrNull() == null || it.toInt() < 0
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
                            _id = jobId ?: "",  // Ensure non-null ID
                            title = title,
                            description = description,
                            company = companyName,
                            companyId = companyId,
                            location = location,
                            salary = salary.toIntOrNull() ?: 0 // Handle conversion safely
                        )
                        if (jobId != null) {
                            viewModel.updateJob(jobId, job)
                        } else {
                            viewModel.addJob(job)
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1BEE7)),
                shape = RoundedCornerShape(8.dp),
                enabled = !salaryError  // Disable button if salary input is incorrect
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
