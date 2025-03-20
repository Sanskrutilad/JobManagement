package com.example.jobmanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobmanagement.Job
import com.example.jobmanagement.jobviewmodel
import kotlinx.coroutines.launch

@Composable
fun JobListScreen(viewModel: jobviewmodel = viewModel()) {
    val jobs = viewModel.jobs.observeAsState(emptyList()) // Use observeAsState for LiveData
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Job List", style = MaterialTheme.typography.titleLarge)

        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
            items(jobs.value) { job ->
                JobItem(job = job) {
                    coroutineScope.launch {
                        viewModel.deleteJob(job._id ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun JobItem(job: Job, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Title: ${job.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Company: ${job.company}")
            Text(text = "Location: ${job.location}")
            Text(text = "Salary: ${job.salary}")
            Text(text = "Description: ${job.description}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onDelete) {
                Text("Delete Job")
            }
        }
    }
}
