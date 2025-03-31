package com.example.jobmanagement.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateJobListScreen(
    viewModel: jobviewmodel = viewModel(),
    navController: NavHostController
) {
    val jobs = viewModel.jobs.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.fetchJobs()
    }

    // Filter jobs based on search query
    val filteredJobs = jobs.value.filter {
        it.title.contains(searchQuery.text, ignoreCase = true) ||
                it.company.contains(searchQuery.text, ignoreCase = true) ||
                it.location.contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Jobs", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE1BEE7)),
                actions = {
                    IconButton(onClick = { navController.navigate("candidateprofile") }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Title, Company, or Location") },
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
                        text = "No matching jobs found. Try a different search!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(filteredJobs) { job ->
                        CandidateJobItem(
                            job = job
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CandidateJobItem(job: Job) {
    var applied by remember { mutableStateOf(false) } // Track applied state

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9AAE1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF73317E)
            )
            Text(text = "Company: ${job.company}")
            Text(text = "Location: ${job.location}")
            Text(text = "Salary: $${job.salary}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { applied = true }, // Just update state, no navigation
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (applied) Color.Gray else Color(0xFF8D5098),
                    contentColor = Color.White
                ),
                enabled = !applied // Disable button after clicking
            ) {
                Text(if (applied) "Applied" else "Apply")
            }
        }
    }
}
