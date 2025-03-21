package com.example.jobmanagement

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jobmanagement.screen.AddEditJobScreen
import com.example.jobmanagement.screen.JobListScreen
import com.example.jobmanagement.jobviewmodel
import com.example.jobmanagement.screen.JobDetailsScreen


@Composable
fun JobNavGraph(navController: NavHostController, viewModel: jobviewmodel) {
    NavHost(navController, startDestination = "job_list") {
        composable("job_list") { JobListScreen(viewModel, navController) }
        composable("add_edit_job") {
            AddEditJobScreen(navController = navController, viewModel = viewModel)
        }
        composable("add_edit_job/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")
            AddEditJobScreen(navController, viewModel, jobId)
        }
        composable("job_details/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetailsScreen(jobId = jobId, viewModel = viewModel)
        }


    }
}
