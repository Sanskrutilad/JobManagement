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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobScreen(navController: NavController, viewModel: jobviewmodel, jobId: String? = null) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    LaunchedEffect(jobId) {
        if (jobId != null) {
            val job = viewModel.jobs.value?.find { it._id == jobId }
            job?.let {
                title = it.title
                description = it.description
                company = it.company
                location = it.location
                salary = it.salary.toString()
            }
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
                value = company,
                onValueChange = { company = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salary") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (jobId != null) {
                        viewModel.updateJob(
                            jobId,
                            Job(
                                _id = jobId,
                                title = title,
                                description = description,
                                company = company,
                                location = location,
                                salary = salary.toIntOrNull() ?: 0
                            )
                        )
                    } else {
                        viewModel.addJob(
                            Job(
                                title = title,
                                description = description,
                                company = company,
                                location = location,
                                salary = salary.toIntOrNull() ?: 0
                            )
                        )
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1BEE7)),
                shape = RoundedCornerShape(8.dp)
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
