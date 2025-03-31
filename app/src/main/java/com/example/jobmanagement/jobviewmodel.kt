package com.example.jobmanagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData

class jobviewmodel : ViewModel() {
    private val repository = JobRepository()

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> get() = _jobs
    private val _jobById = MutableLiveData<Job?>()
    val jobById: LiveData<Job?> get() = _jobById
    private val _companyName = MutableLiveData<String>()
    val companyName: LiveData<String> get() = _companyName
    private val _candidate = MutableLiveData<Candidate?>()
    val candidate: LiveData<Candidate?> get() = _candidate
    private val _company = MutableLiveData<Company?>()
    val company: LiveData<Company?> get() = _company
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
            fetchJobs()
        }
    }

    fun getJobById(jobId: String) {
        viewModelScope.launch {
            try {
                val jobData = repository.getJobById(jobId)
                _jobById.value = jobData
            } catch (e: Exception) {
                Log.e("jobviewmodel", "Error fetching job by ID: ${e.message}")
                _jobById.value = null
            }
        }
    }
    fun fetchCompanyName(companyId: String) {
        viewModelScope.launch {
            try {
                _companyName.value = repository.getCompanyName(companyId)
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error fetching company name: ${e.message}")
                _companyName.value = "Unknown Company"
            }
        }
    }

    fun registerCompany(company: Company, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.registerCompany(company)
                onSuccess()
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error registering company: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }
    fun registerCandidate(candidate: Candidate, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.registerCandidate(candidate)
                onSuccess()
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error registering candidate: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }
    suspend fun getCompanyName(companyId: String): String? {
        return try {
            repository.getCompanyName(companyId)
        } catch (e: Exception) {
            Log.e("JobViewModel", "Error fetching company name: ${e.message}")
            null  // Return null if there's an error
        }
    }
    fun getCandidateByUid(uid: String) {
        viewModelScope.launch {
            try {
                val candidateData = repository.getCandidateByUid(uid)
                if (candidateData != null) {
                    _candidate.postValue(candidateData) // Ensure LiveData is updated
                } else {
                    Log.e("JobViewModel", "Candidate not found")
                    _candidate.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error fetching candidate: ${e.message}")
                _candidate.postValue(null)
            }
        }
    }


    fun getCompanyByUid(uid: String) {
        viewModelScope.launch {
            try {
                _company.value = repository.getCompanyByUid(uid)
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error fetching company: ${e.message}")
                _company.value = null
            }
        }
    }



}
