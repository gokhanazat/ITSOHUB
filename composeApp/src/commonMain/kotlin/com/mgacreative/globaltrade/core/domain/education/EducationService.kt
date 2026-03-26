package com.mgacreative.globaltrade.core.domain.education

import com.mgacreative.globaltrade.getNowMillis
import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.ui.education.Education
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserCertificate(
    val id: Int = 0,
    val email: String,
    val certCode: String,
    val eduId: String,
    val createdAt: Long
)

class EducationService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                coerceInputValues = true
                encodeDefaults = true
                isLenient = true
            })
        }
    }

    suspend fun saveEducation(education: Education): Result<Unit> {
        return try {
            val docId = education.id.ifEmpty { "edu_${getNowMillis()}" }
            val newEducation = education.copy(
                id = docId,
                createdAt = if (education.createdAt == 0L) getNowMillis() else education.createdAt
            )
            
            val response: HttpResponse = client.post(ApiConfig.EDUCATIONS_URL) {
                contentType(ContentType.Application.Json)
                setBody(newEducation)
            }
            
            if (response.status.value in 200..299) Result.success(Unit)
            else Result.failure(Exception("API Hatası: ${response.status}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllEducations(): Result<List<Education>> {
        return try {
            val response: HttpResponse = client.get(ApiConfig.EDUCATIONS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list: List<Education> = response.body()
                Result.success(list.sortedByDescending { it.createdAt })
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEducationById(id: String): Result<Education> {
        return try {
            val response: HttpResponse = client.get("${ApiConfig.EDUCATIONS_URL}?id=$id")
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                val all = getAllEducations().getOrThrow()
                val edu = all.find { it.id == id } ?: throw Exception("Eğitim bulunamadı")
                Result.success(edu)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCertificates(email: String): Result<List<UserCertificate>> {
        return try {
            val response: HttpResponse = client.get("${ApiConfig.BASE_URL}/certificates/get?email=$email")
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEducation(id: String): Result<Unit> {
        return try {
            val response: HttpResponse = client.delete("${ApiConfig.EDUCATIONS_URL}?id=$id")
            if (response.status == HttpStatusCode.OK) Result.success(Unit)
            else Result.failure(Exception("API Hatası: ${response.status}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
