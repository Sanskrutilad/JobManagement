package com.example.jobmanagement

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jobmanagement.screen.AddEditJobScreen
import com.example.jobmanagement.screen.CandidateJobListScreen
import com.example.jobmanagement.screen.CandidateProfileScreen
import com.example.jobmanagement.screen.CandidateRegistrationScreen
import com.example.jobmanagement.screen.CompanyJobListScreen
import com.example.jobmanagement.screen.CompanyProfileScreen
import com.example.jobmanagement.screen.CompanyRegistrationScreen
import com.example.jobmanagement.screen.JobDetailsScreen
import com.example.jobmanagement.screen.LoginScreen
import com.example.jobmanagement.screen.UserSelectionScreen

@Composable
fun JobNavGraph(navController: NavHostController, viewModel: jobviewmodel, apiService: ApiService) {
    NavHost(navController, startDestination = "user_selection") {
        composable("companyjob_list/{companyId}") { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId")
            CompanyJobListScreen(viewModel, navController,companyId)
        }
        composable("candidatejob_list") { CandidateJobListScreen(viewModel, navController) }
        composable("add_job/{companyId}") { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            AddEditJobScreen(
                navController = navController,
                jobId = null,  // Empty jobId for adding
                companyId = companyId,
                apiService = apiService
            )
        }
        composable("add_edit_job/{jobId}/{companyId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            AddEditJobScreen(
                navController = navController,
                jobId = jobId,  // Provide jobId for editing
                companyId = companyId,
                apiService = apiService
            )
        }

        composable("job_details/{jobId}/{companyId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            val companyId = backStackEntry.arguments?.getString("companyId") ?: "" // Fixed here
            JobDetailsScreen(jobId, companyId)
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
        composable("candidateprofile") {
            CandidateProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable("companyprofile") {
            CompanyProfileScreen(navController = navController, viewModel = viewModel)
        }



    }
}
