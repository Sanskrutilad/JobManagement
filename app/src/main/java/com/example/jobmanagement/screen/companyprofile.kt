package com.example.jobmanagement.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.jobmanagement.Candidate
import com.example.jobmanagement.Company
import com.example.jobmanagement.jobviewmodel
import com.google.firebase.auth.FirebaseAuth
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(viewModel: jobviewmodel = viewModel(), navController: NavHostController) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val companyUid = firebaseUser?.uid ?: ""

    LaunchedEffect(companyUid) {
        if (companyUid.isNotEmpty()) {
            viewModel.getCompanyByUid(companyUid)
        }
    }

    val company by viewModel.company.observeAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Company Profile") }) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            company?.let {
                CompanyProfileContent(it,navController)
            } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun CompanyProfileContent(company: Company, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Company Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoRow(label = "Company Name", value = company.companyName)
        InfoRow(label = "Industry", value = company.industry)
        InfoRow(label = "Location", value = company.location)
        InfoRow(label = "Founded Year", value = company.foundedYear.toString())
        InfoRow(label = "Email", value = company.email)
        InfoRow(label = "Phone", value = company.phone)
        InfoRow(label = "Employees", value = company.size.toString())
        InfoRow(label = "Company Type", value = company.companyType)

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("user_selection") {  // Navigate to login screen after logout
                    popUpTo("user_selection") { inclusive = true } // Clears backstack
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
        ) {
            Text("Logout")
        }
    }
}

// Reusable Row for displaying information
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}
