package com.example.jobmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class jobviewmodel : ViewModel() {
    private val repository = JobRepository()

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> get() = _jobs

    fun fetchJobs() {
        viewModelScope.launch {
            _jobs.value = repository.getJobs()
        }
    }

    fun addJob(job: Job) {
        viewModelScope.launch {
            repository.createJob(job)
            fetchJobs()  // Refresh list
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
