package com.example.jobmanagement

import com.google.gson.annotations.SerializedName
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
    val companyId: String,  // New field to link job to employer
    val location: String,
    val salary: Int
)

data class Company(
    @SerializedName("_id") val companyId: String? = null,
    val uid: String,                 // Firebase UID for authentication
    val companyName: String,
    val industry: String,
    val location: String,
    val foundedYear: Int,
    val email: String,
    val phone: String,
    val size: Int,                   // Number of employees
    val companyType: String,       // Private, Public, etc.
)
data class Candidate(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val skills: List<String>,
    val experience: Int,
    val education: String,
    val location: String
)
data class CompanyNameResponse(
    val companyName: String
)

// Retrofit Instance
object JobApi {
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

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

fun createApiService(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.71.52:5000/api/") // Updated URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(ApiService::class.java)
}


// Retrofit API Interface
interface ApiService {
    @GET("jobs")
    suspend fun getJobs(@Query("companyId") companyId: String? = null): List<Job>

    @POST("jobs")
    suspend fun createJob(@Body job: Job): Job

    @GET("jobs/{id}")
    suspend fun getJobById(@Path("id") jobId: String): Job

    @PUT("jobs/{id}")
    suspend fun updateJob(@Path("id") id: String, @Body job: Job): Job

    @DELETE("jobs/{id}")
    suspend fun deleteJob(@Path("id") id: String): Job

    @POST("companies/register")
    suspend fun registerCompany(@Body company: Company): Company

    @POST("candidates/register")
    suspend fun registerCandidate(@Body candidate: Candidate)

    @GET("companyName/{companyId}")
    suspend fun getCompanyName(@Path("companyId") companyId: String): CompanyNameResponse

    @GET("candidates/{uid}")
    suspend fun getCandidateByUid(@Path("uid") uid: String): Candidate

    @GET("companies/{uid}")
    suspend fun getCompanyByUid(@Path("uid") uid: String): Company
}

// Repository to handle API calls
class JobRepository {
    private val api = createApiService()
    suspend fun registerCandidate(candidate: Candidate) = withContext(Dispatchers.IO) { api.registerCandidate(candidate) }
    suspend fun registerCompany(company: Company) = withContext(Dispatchers.IO) { api.registerCompany(company) }
    suspend fun getJobs(companyId: String? = null) = withContext(Dispatchers.IO) {api.getJobs(companyId) }
    suspend fun createJob(job: Job) = withContext(Dispatchers.IO) { api.createJob(job) }
    suspend fun updateJob(id: String, job: Job) = withContext(Dispatchers.IO) { api.updateJob(id, job) }
    suspend fun deleteJob(id: String) = withContext(Dispatchers.IO) { api.deleteJob(id) }
    suspend fun getJobById(jobId: String) = withContext(Dispatchers.IO) { api.getJobById(jobId) }
    suspend fun getCompanyName(companyId: String): String = withContext(Dispatchers.IO) { api.getCompanyName(companyId).companyName }
    suspend fun getCandidateByUid(uid: String) = withContext(Dispatchers.IO) { api.getCandidateByUid(uid) }
    suspend fun getCompanyByUid(uid: String) = withContext(Dispatchers.IO) { api.getCompanyByUid(uid) }
}
