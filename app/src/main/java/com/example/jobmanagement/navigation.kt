package com.example.jobmanagement

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jobmanagement.screen.AddEditJobScreen
import com.example.jobmanagement.jobviewmodel
import com.example.jobmanagement.screen.CandidateJobListScreen
import com.example.jobmanagement.screen.CandidateRegistrationScreen
import com.example.jobmanagement.screen.CompanyJobListScreen
import com.example.jobmanagement.screen.CompanyRegistrationScreen
import com.example.jobmanagement.screen.JobDetailsScreen
import com.example.jobmanagement.screen.LoginScreen
import com.example.jobmanagement.screen.UserSelectionScreen


@Composable
fun JobNavGraph(navController: NavHostController, viewModel: jobviewmodel) {
    NavHost(navController, startDestination = "user_selection") {
        composable("companyjob_list") { CompanyJobListScreen(viewModel, navController) }
        composable("candidatejob_list") { CandidateJobListScreen(viewModel, navController) }
        composable("add_edit_job") {
            AddEditJobScreen(navController = navController, viewModel = viewModel)
        }
        composable("add_edit_job/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")
            AddEditJobScreen(navController, viewModel, jobId)
        }
        composable("jobDetails/{jobId}/{employerId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            val employerId = backStackEntry.arguments?.getString("employerId") ?: ""
            JobDetailsScreen(jobId, employerId)
        }
        composable("user_selection") {
            UserSelectionScreen(navController)
        }
        composable("login/{userType}") { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "candidate" // Default to candidate
            LoginScreen(navController, userType)
        }

        composable("candidateRegistration") {
            CandidateRegistrationScreen(navController)
        }

        composable("companyRegistration") {
            CompanyRegistrationScreen(navController = navController, viewModel = viewModel)
        }



    }
}
