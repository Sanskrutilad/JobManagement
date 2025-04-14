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
    viewModel: jobviewmodel,
    jobId: String? = null,
    companyId: String?
) {
    val coroutineScope = rememberCoroutineScope()
    val job by viewModel.jobById.observeAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var companyIdState by remember { mutableStateOf(companyId ?: "") }
    var companyName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var salaryError by remember { mutableStateOf(false) }

    // Load job details only once
    LaunchedEffect(jobId) {
        if (jobId != null) {
            viewModel.getJobById(jobId)
        }
    }

    // Set form fields only once after job is loaded
    LaunchedEffect(job) {
        job?.let {
            title = it.title
            description = it.description
            companyIdState = it.companyId
            location = it.location
            salary = it.salary.toString()
            viewModel.fetchCompanyName(it.companyId)
        }
    }

    // Get company name from ViewModel
    val observedCompanyName by viewModel.companyName.observeAsState("")
    LaunchedEffect(companyIdState) {
        if (companyIdState.isNotEmpty()) {
            viewModel.fetchCompanyName(companyIdState)
        }
    }
    LaunchedEffect(observedCompanyName) {
        if (!observedCompanyName.isNullOrEmpty()) {
            companyName = observedCompanyName
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
                        val jobToSave = Job(
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
                                if (jobId != null) {
                                    viewModel.updateJob(jobId, jobToSave)
                                } else {
                                    viewModel.addJob(jobToSave)
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
