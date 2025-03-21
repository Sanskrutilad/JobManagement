package com.example.jobmanagement

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Data Class for Job
data class Job(
    val _id: String? = null,
    val title: String,
    val description: String,
    val company: String,
    val location: String,
    val salary: Int
)

// Retrofit Instance
object JobApi {
    private const val BASE_URL = "http://192.168.71.52:5000/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

// Retrofit API Interface
interface ApiService {
    @GET("jobs")
    suspend fun getJobs(): List<Job>

    @POST("jobs")
    suspend fun createJob(@Body job: Job): Job

    @GET("jobs/{id}")
    suspend fun getJobById(@Path("id") jobId: String): Job

    @PUT("jobs/{id}")
    suspend fun updateJob(@Path("id") id: String, @Body job: Job): Job

    @DELETE("jobs/{id}")
    suspend fun deleteJob(@Path("id") id: String): Job
}

// Repository to handle API calls
class JobRepository {
    private val api = JobApi.api

    suspend fun getJobs() = withContext(Dispatchers.IO) { api.getJobs() }
    suspend fun createJob(job: Job) = withContext(Dispatchers.IO) { api.createJob(job) }
    suspend fun updateJob(id: String, job: Job) = withContext(Dispatchers.IO) { api.updateJob(id, job) }
    suspend fun deleteJob(id: String) = withContext(Dispatchers.IO) { api.deleteJob(id) }
    suspend fun getJobById(jobId: String) = withContext(Dispatchers.IO) { api.getJobById(jobId) }
}
