package com.example.jobmanagement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmanagement.Job
import com.example.jobmanagement.jobviewmodel


@Composable
fun AddEditJobScreen(navController: NavController, viewModel: jobviewmodel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        TextField(value = company, onValueChange = { company = it }, label = { Text("Company") })
        TextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
        TextField(value = salary, onValueChange = { salary = it }, label = { Text("Salary") })

        Button(onClick = {
            viewModel.addJob(Job(title = title, description = description, company = company, location = location, salary = salary.toInt()))
            navController.popBackStack()
        }) {
            Text("Save Job")
        }
    }
}
