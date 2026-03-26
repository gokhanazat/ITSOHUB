package com.mgacreative.globaltrade.core.domain.b2b

import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.core.auth.SessionManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.Serializable

class CompanyService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true
            })
        }
    }

    private val COMPANIES_URL = "${ApiConfig.BASE_URL}/companies"

    suspend fun saveCompany(company: B2BCompany): Result<Unit> {
        return try {
            val userId = SessionManager.getUserId()
            if (userId == "guest") return Result.failure(Exception("Oturum acilmamis"))

            var finalLogoUrl = company.logoUrl

            if (company.logoUrl != null && company.logoUrl!!.startsWith("data:image/")) {
                val base64Header = company.logoUrl!!.substringBefore(",", "")
                val mimeType = if (base64Header.contains("image/png")) "image/png" 
                              else if (base64Header.contains("image/jpeg")) "image/jpeg"
                              else "image/webp"

                val base64Data = company.logoUrl!!.substringAfter("base64,")
                @OptIn(ExperimentalEncodingApi::class)
                val imageBytes = Base64.Default.decode(base64Data)

                val uploadResponse: HttpResponse = client.post(ApiConfig.UPLOAD_URL) {
                    header("Content-Type", mimeType)
                    setBody(imageBytes)
                }

                if (uploadResponse.status == HttpStatusCode.OK) {
                    val resultText = uploadResponse.bodyAsText().trim()
                    // Worker'dan dönen gerçek dosya adını al
                    finalLogoUrl = try {
                        val json = Json { ignoreUnknownKeys = true }
                        if (resultText.startsWith("{")) {
                            val map = json.decodeFromString<Map<String, String>>(resultText)
                            map["key"] ?: map["url"] ?: resultText
                        } else {
                            resultText
                        }
                    } catch (e: Exception) {
                        resultText
                    }
                }
            }

            // Agresif temizlik: Sadece ASCII karakterleri tut
            val cleanedLogoUrl = finalLogoUrl?.filter { it.code in 33..126 && it != '"' && it != '\'' }

            val finalCompany = company.copy(
                id = userId,
                logoUrl = cleanedLogoUrl,
                hasLogo = !cleanedLogoUrl.isNullOrBlank(),
                hasDescription = company.description.isNotBlank(),
                hasContactInfo = company.email.isNotBlank() || company.phone.isNotBlank() || company.gsm.isNotBlank()
            )

            val response = client.post(COMPANIES_URL) {
                contentType(ContentType.Application.Json)
                setBody(finalCompany)
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Kaydetme Hatasi: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOwnCompany(): Result<B2BCompany?> {
        return try {
            val userId = SessionManager.getUserId()
            if (userId == "guest") return Result.failure(Exception("Oturum acilmamis"))
            val response = client.get("$COMPANIES_URL?id=$userId")

            if (response.status == HttpStatusCode.OK) {
                val bodyText = response.bodyAsText().trim()
                if (bodyText == "null" || bodyText == "{}" || bodyText.isEmpty()) return Result.success(null)

                return try {
                    val company = response.body<B2BCompany>()
                    if (company.id.isEmpty() && company.name.isEmpty()) Result.success(null)
                    else Result.success(company)
                } catch (e: Exception) {
                    Result.success(null)
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompanyById(id: String): Result<B2BCompany?> {
        return try {
            val response = client.get("$COMPANIES_URL?id=$id")
            if (response.status == HttpStatusCode.OK) {
                val bodyText = response.bodyAsText().trim()
                if (bodyText == "null" || bodyText == "{}" || bodyText.isEmpty()) return Result.success(null)
                Result.success(response.body<B2BCompany>())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCompanies(): Result<List<B2BCompany>> {
        return try {
            val response = client.get(COMPANIES_URL)
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<List<B2BCompany>>())
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
