package com.example.jobmanagement.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.jobmanagement.Candidate
import com.example.jobmanagement.jobviewmodel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateProfileScreen(viewModel: jobviewmodel = viewModel(), navController: NavHostController) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val candidateUid = firebaseUser?.uid ?: ""
    if (firebaseUser == null) {
        Log.e("CandidateProfile", "User is not logged in!")
    } else {
        val candidateUid = firebaseUser.uid
        Log.d("CandidateProfile", "Candidate UID: $candidateUid")
    }
    LaunchedEffect(candidateUid) {
        if (candidateUid.isNotEmpty()) {
            viewModel.getCandidateByUid(candidateUid)
        }
    }
    val candidate by viewModel.candidate.observeAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Candidate Profile") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                candidate == null -> CircularProgressIndicator()
                candidate != null -> CandidateProfileContent(candidate!!)
            }
        }
    }
}


@Composable
fun CandidateProfileContent(candidate: Candidate) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Candidate Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Name: ${candidate.fullName}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "Email: ${candidate.email}", fontSize = 18.sp)
        Text(text = "Phone: ${candidate.phone}", fontSize = 18.sp)
        Text(text = "Skills: ${candidate.skills.joinToString(", ")}", fontSize = 16.sp)
        Text(text = "Experience: ${candidate.experience} years", fontSize = 16.sp)
        Text(text = "Education: ${candidate.education}", fontSize = 16.sp)
        Text(text = "Location: ${candidate.location}", fontSize = 16.sp)


    }
}

