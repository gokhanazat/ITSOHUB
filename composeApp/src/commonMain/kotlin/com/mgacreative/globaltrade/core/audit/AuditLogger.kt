package com.mgacreative.globaltrade.core.audit

import com.mgacreative.globaltrade.core.network.ApiConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object AuditLogger {
    
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
            })
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val logQueue = Channel<AuditEvent>(Channel.UNLIMITED)

    init {
        startQueueProcessor()
    }

    fun logEvent(event: AuditEvent) {
        logQueue.trySend(event)
    }

    suspend fun getLogs(): Result<List<AuditEvent>> {
        return try {
            val response = client.get(ApiConfig.AUDIT_LOGS_URL)
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun startQueueProcessor() {
        scope.launch {
            for (event in logQueue) {
                try {
                    client.post(ApiConfig.AUDIT_LOGS_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(event)
                    }
                } catch (e: Exception) {
                    println("AuditLogger: Silently dropped event ${event.actionType} due to ${e.message}")
                }
            }
        }
    }
}
