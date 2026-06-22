package com.nitha.ai

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API interface for OpenRouter
 */
interface OpenRouterApi {

    @POST("chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("HTTP-Referer") referer: String = "https://nitha.ai",
        @Header("X-Title") title: String = "NITHA AI Assistant",
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>

    @GET("models")
    suspend fun getModels(
        @Header("Authorization") auth: String
    ): Response<ModelsResponse>
}

data class ModelsResponse(
    val data: List<ModelData> = emptyList()
)

data class ModelData(
    val id: String = "",
    val name: String = "",
    val context_length: Int = 4096,
    val pricing: Pricing? = null
)

data class Pricing(
    val prompt: String = "0",
    val completion: String = "0"
)
