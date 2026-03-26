package com.mgacreative.globaltrade.core.domain.auth

import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.core.error.AppResult
import com.mgacreative.globaltrade.core.error.safeCall
import com.mgacreative.globaltrade.core.auth.Role
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
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val role: String? = null,
    val message: String? = null
)

@Serializable
data class LoginRequest(
    val registryNumber: String,
    val password: String
)

@Serializable
data class ChangePasswordRequest(
    val newPassword: String
)

class AuthService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                coerceInputValues = true
                isLenient = true
            })
        }
    }

    suspend fun login(registryNumber: String, password: String): AppResult<AuthResponse> {
        return safeCall {
            val response: HttpResponse = client.post(ApiConfig.LOGIN_URL) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(registryNumber, password))
            }
            response.body<AuthResponse>()
        }
    }

    suspend fun register(registryNumber: String, password: String): AppResult<AuthResponse> {
        return safeCall {
            val response: HttpResponse = client.post(ApiConfig.REGISTER_URL) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(registryNumber, password))
            }
            response.body<AuthResponse>()
        }
    }

    suspend fun changePassword(newPassword: String): AppResult<AuthResponse> {
        return safeCall {
            // Not: GerÃ§ek senaryoda burada Auth Token gÃ¶nderilmelidir.
            val response: HttpResponse = client.post("${ApiConfig.BASE_URL}/auth/change-password") {
                contentType(ContentType.Application.Json)
                setBody(ChangePasswordRequest(newPassword))
            }
            response.body<AuthResponse>()
        }
    }

    suspend fun adminResetPassword(registryNumber: String, newPassword: String): AppResult<AuthResponse> {
        return safeCall {
            val response: HttpResponse = client.post("${ApiConfig.BASE_URL}/auth/admin-reset-password") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(registryNumber, newPassword))
            }
            response.body<AuthResponse>()
        }
    }
}
