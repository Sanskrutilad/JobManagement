package com.example.jobmanagement

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jobmanagement.screen.AddEditJobScreen
import com.example.jobmanagement.screen.JobDetailScreen
import com.example.jobmanagement.screen.JobListScreen
import com.example.jobmanagement.jobviewmodel


@Composable
fun JobNavGraph(navController: NavHostController, viewModel: jobviewmodel) {
    NavHost(navController, startDestination = "job_list") {
        composable("job_list") { JobListScreen(viewModel) }
        composable("add_edit_job") { AddEditJobScreen(navController, viewModel) }
        composable("job_detail/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetailScreen(navController, viewModel, jobId)
        }
    }
}
