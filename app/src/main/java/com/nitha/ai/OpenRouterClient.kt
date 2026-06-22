package com.nitha.ai

import android.content.Context
import com.google.gson.GsonBuilder
import com.nitha.utils.Constants
import com.nitha.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * OpenRouter API Client with fallback model support
 */
class OpenRouterClient(context: Context) {

    private val api: OpenRouterApi
    private var currentModelIndex = 0

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        api = Retrofit.Builder()
            .baseUrl(Constants.OPENROUTER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenRouterApi::class.java)
    }

    /**
     * Send message to OpenRouter with automatic fallback
     */
    suspend fun sendMessage(
        apiKey: String,
        messages: List<Message>,
        model: String = Constants.MODEL_DEEPSEEK_CHAT,
        maxTokens: Int = Constants.MAX_TOKENS,
        temperature: Double = 0.7
    ): AIResult {
        return withContext(Dispatchers.IO) {
            val modelsToTry = mutableListOf(model)
            modelsToTry.addAll(Constants.FALLBACK_MODELS.filter { it != model })

            for (m in modelsToTry) {
                try {
                    val request = OpenRouterRequest(
                        model = m,
                        messages = messages,
                        max_tokens = maxTokens,
                        temperature = temperature,
                        stream = false
                    )

                    val response = api.sendMessage(
                        auth = "Bearer $apiKey",
                        request = request
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.choices.isNotEmpty()) {
                            val content = body.choices[0].message?.content ?: ""
                            return@withContext AIResult.Success(
                                content = content,
                                model = body.model,
                                tokensUsed = body.usage?.total_tokens ?: 0
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (response.code() == 429 || response.code() == 503) {
                            continue // Try next model
                        }
                        return@withContext AIResult.Error(
                            "API Error ${response.code()}: ${errorBody ?: "Unknown error"}"
                        )
                    }
                } catch (e: Exception) {
                    continue // Try next model on exception
                }
            }

            AIResult.Error("All models failed. Please check your internet connection or API key.")
        }
    }

    /**
     * Quick health check
     */
    suspend fun checkConnection(apiKey: String): Boolean {
        return try {
            val request = OpenRouterRequest(
                model = Constants.MODEL_OPENROUTER_FREE,
                messages = listOf(Message("user", "Hi")),
                max_tokens = 10
            )
            val response = api.sendMessage(auth = "Bearer $apiKey", request = request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

sealed class AIResult {
    data class Success(
        val content: String,
        val model: String,
        val tokensUsed: Int
    ) : AIResult()

    data class Error(val message: String) : AIResult()
}
