package com.example.jobmanagement.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.jobmanagement.Job
import com.example.jobmanagement.jobviewmodel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyJobListScreen(
    viewModel: jobviewmodel = viewModel(),
    navController: NavHostController,
    companyId: String?
) {
    val jobs = viewModel.jobs.observeAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()

    // Search state
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        Log.d("CompanyJobListScreen", "Company ID: $companyId")
        viewModel.fetchJobs()
    }

    // Filtering jobs based on search query & provided company ID
    val filteredJobs = jobs.value.filter {
        it.companyId == companyId && (
                it.title.contains(searchQuery.text, ignoreCase = true) ||
                        it.company.contains(searchQuery.text, ignoreCase = true) ||
                        it.location.contains(searchQuery.text, ignoreCase = true)
                )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Job Listings", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE1BEE7)),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("companyprofile")
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to add job screen (pass companyId directly)
                    navController.navigate("add_job/$companyId")
                },
                containerColor = Color(0xFFE1BEE7)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Job", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Title, Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (filteredJobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (jobs.value.isEmpty()) {
                            "You haven't posted any jobs yet. Add a new job!"
                        } else {
                            "No matching jobs found. Try a different search!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(filteredJobs) { job ->
                        JobItem(
                            job = job,
                            onUpdate = {
                                // Navigate to edit job screen with jobId and companyId
                                navController.navigate("add_edit_job/${job._id}/${companyId}")
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    viewModel.deleteJob(job._id ?: "")
                                }
                            },
                            onClick = {
                                navController.navigate("job_details/${job._id}/${companyId}")
                            }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun JobItem(job: Job, onUpdate: () -> Unit, onDelete: () -> Unit,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9AAE1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${job.title}", style = MaterialTheme.typography.titleMedium, color = Color(0xFF73317E),
                fontSize = 25.sp, fontWeight = FontWeight.Bold )
            Text(text = "Company: ${job.company}")
            Text(text = "Location: ${job.location}")
            Text(text = "Salary: $${job.salary}")
            Text(text = "Description: ${job.description}")

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = onUpdate,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D5098), contentColor = Color.White)
                ) {
                    Text("Update Job")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE18C8C), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
