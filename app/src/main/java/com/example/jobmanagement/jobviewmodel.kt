package com.example.jobmanagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class jobviewmodel : ViewModel() {
    private val repository = JobRepository()

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> get() = _jobs

    init {
        fetchJobs()
    }

    fun fetchJobs() {
        viewModelScope.launch {
            _jobs.value = repository.getJobs()
        }
    }

    fun addJob(job: Job) {
        viewModelScope.launch {
            try {
                val addedJob = repository.createJob(job)
                if (addedJob != null) {
                    Log.d("jobviewmodel", "Job added successfully: $addedJob")
                    fetchJobs()  // Refresh list after adding
                } else {
                    Log.e("jobviewmodel", "Failed to add job — Job is null")
                }
            } catch (e: Exception) {
                Log.e("jobviewmodel", "Error adding job: ${e.message}")
            }
        }
    }


    fun updateJob(id: String, job: Job) {
        viewModelScope.launch {
            repository.updateJob(id, job)
            fetchJobs()  // Refresh list
        }
    }

    fun deleteJob(id: String) {
        viewModelScope.launch {
            repository.deleteJob(id)
            fetchJobs()  // Refresh list
        }
    }
}
