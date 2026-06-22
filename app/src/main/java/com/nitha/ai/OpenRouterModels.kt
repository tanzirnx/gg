package com.nitha.ai

/**
 * Data models for OpenRouter API
 */
data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 2048,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

data class Message(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val id: String = "",
    val model: String = "",
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null,
    val error: ErrorDetail? = null
)

data class Choice(
    val message: Message? = null,
    val index: Int = 0,
    val finish_reason: String? = null
)

data class Usage(
    val prompt_tokens: Int = 0,
    val completion_tokens: Int = 0,
    val total_tokens: Int = 0
)

data class ErrorDetail(
    val message: String = "",
    val type: String = "",
    val code: Int = 0
)

data class OpenRouterModel(
    val id: String,
    val name: String,
    val description: String,
    val context_length: Int = 4096
)
