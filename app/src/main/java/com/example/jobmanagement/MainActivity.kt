package com.example.jobmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.jobmanagement.ui.theme.JobManagementTheme

class MainActivity : ComponentActivity() {
    private val viewModel: jobviewmodel by viewModels()
    val apiService = createApiService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobManagementTheme {
                val navController = rememberNavController()
                JobNavGraph(navController, viewModel,apiService)
            }
        }
    }
}
