package com.mgacreative.globaltrade.core.domain.showroom

import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.getNowMillis
import com.mgacreative.globaltrade.core.auth.SessionManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.globaltrade.core.util.IntToBooleanSerializer

@Serializable
data class ShowroomProduct(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val ownerId: String = "",
    val companyName: String = "",
    val country: String = "",
    @Serializable(with = IntToBooleanSerializer::class)
    val isPremium: Boolean = false,
    val createdAt: Long = 0L
)

class ProductService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                coerceInputValues = true
                encodeDefaults = true
            })
        }
    }

    suspend fun saveProduct(product: ShowroomProduct): Result<Unit> {
        return try {
            val userId = SessionManager.getUserId()
            if (userId == "guest") return Result.failure(Exception("Oturum aÃ§Ä±lmamÄ±ÅŸ"))
            
            var finalImageUrl = product.imageUrl
            
            if (product.imageUrl != null && product.imageUrl!!.startsWith("data:image/")) {
                val base64Header = product.imageUrl!!.substringBefore(",", "")
                val mimeType = if (base64Header.contains("image/png")) "image/png" 
                              else if (base64Header.contains("image/jpeg")) "image/jpeg"
                              else "image/webp"

                val base64Data = product.imageUrl!!.substringAfter("base64,")
                @OptIn(ExperimentalEncodingApi::class)
                val imageBytes = Base64.Default.decode(base64Data)
                
                val uploadResponse: HttpResponse = client.post(ApiConfig.UPLOAD_URL) {
                    header("Content-Type", mimeType)
                    setBody(imageBytes)
                }
                
                if (uploadResponse.status == HttpStatusCode.OK) {
                    val resultText = uploadResponse.bodyAsText().trim()
                    finalImageUrl = try {
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

            // Regex yerine klasik temizlik (Build güvenliği için)
            val cleanedImageUrl = finalImageUrl?.replace("\"", "")?.replace("'", "")?.replace("\n", "")?.replace("\r", "")?.trim()
            val docId = product.id.ifEmpty { "prod_${getNowMillis()}" }
            
            val newProduct = product.copy(
                id = docId,
                ownerId = userId,
                imageUrl = cleanedImageUrl,
                createdAt = if (product.createdAt == 0L) getNowMillis() else product.createdAt
            )

            val response: HttpResponse = client.post(ApiConfig.PRODUCTS_URL) {
                contentType(ContentType.Application.Json)
                setBody(newProduct)
            }

            if (response.status == HttpStatusCode.OK) Result.success(Unit)
            else Result.failure(Exception("API Hatası: ${response.status}"))
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllProducts(): Result<List<ShowroomProduct>> {
        return try {
            val response: HttpResponse = client.get(ApiConfig.PRODUCTS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list: List<ShowroomProduct> = response.body()
                Result.success(list)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByOwnerId(ownerId: String): Result<List<ShowroomProduct>> {
        return try {
            val response: HttpResponse = client.get(ApiConfig.PRODUCTS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list: List<ShowroomProduct> = response.body()
                println("Filtreleniyor: Aranan OwnerId=$ownerId, Listedeki ilk product ownerId=${list.firstOrNull()?.ownerId}")
                Result.success(list.filter { it.ownerId == ownerId })
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String): Result<ShowroomProduct> {
        return try {
            val all = getAllProducts().getOrThrow()
            val product = all.find { it.id == productId } ?: throw Exception("Ürün bulunamadı")
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val response: HttpResponse = client.delete("${ApiConfig.PRODUCTS_URL}?id=$productId")
            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP Hatası: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOwnProducts(): Result<List<ShowroomProduct>> {
        val userId = SessionManager.getUserId()
        if (userId == "guest") return Result.failure(Exception("Oturum açılmamış"))
        return getProductsByOwnerId(userId)
    }

    suspend fun getProductsByCategory(category: String): Result<List<ShowroomProduct>> {
        return try {
            val response: HttpResponse = client.get(ApiConfig.PRODUCTS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list: List<ShowroomProduct> = response.body()
                Result.success(list.filter { it.category == category })
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
