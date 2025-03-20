package com.example.jobmanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jobmanagement.jobviewmodel
import com.example.jobmanagement.Job
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun JobDetailScreen(
    navController: NavController,
    viewModel: jobviewmodel = viewModel(),
    jobId: String
) {
    val jobs by viewModel.jobs.observeAsState(emptyList())
    val job = jobs.find { it._id == jobId }

    if (job != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Title: ${job.title}", style = MaterialTheme.typography.titleLarge)
            Text("Description: ${job.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Company: ${job.company}", style = MaterialTheme.typography.bodyMedium)
            Text("Location: ${job.location}", style = MaterialTheme.typography.bodyMedium)
            Text("Salary: ${job.salary}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.deleteJob(job._id ?: "")
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Job", color = MaterialTheme.colorScheme.onError)
            }
        }
    } else {
        Text("Job not found", modifier = Modifier.padding(16.dp))
    }
}
