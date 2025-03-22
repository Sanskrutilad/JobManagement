package com.example.jobmanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UserSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Registration Type", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("login/candidate")},
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
        ) {
            Text(text = "Candidate")
        }

        Button(
            onClick = { navController.navigate("login/company") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
        ) {
            Text(text = "Company")
        }
    }
}
