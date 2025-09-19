package com.mramallo.aichat.data.datasource

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.api.chat.ChatMessage as OpenAiChatMessage
import com.mramallo.aichat.data.model.ChatMessage
import com.mramallo.aichat.data.model.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIDataSource @Inject constructor(
    private val openAI: OpenAI
) {

    suspend fun getChatCompletion(messages: List<ChatMessage>): String {
        val openAiMessages = messages.map { it.toOpenAiChatMessage() }

        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = openAiMessages
        )

        val completion = openAI.chatCompletion(completionRequest)
        return completion.choices.first().message.content ?: "No se pudo obtener una respuesta"
    }

    suspend fun generateConversationTitle(firstMessage: String): String {
        val systemMessage = OpenAiChatMessage(
            role = ChatRole.System,
            content = "Genera un título corto y descriptivo (máximo 6 palabras) para una conversación basado en el siguiente mensaje. No uses comillas ni puntuación."
        )

        val userMessage = OpenAiChatMessage(
            role = ChatRole.User,
            content = firstMessage
        )

        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(systemMessage, userMessage)
        )

        val completion = openAI.chatCompletion(completionRequest)
        return completion.choices.first().message.content?.trim() ?: "Nueva conversación"
    }

    private fun ChatMessage.toOpenAiChatMessage(): OpenAiChatMessage {
        val role = when(this.role) {
            MessageRole.USER -> ChatRole.User
            MessageRole.ASSISTANT -> ChatRole.Assistant
            MessageRole.SYSTEM -> ChatRole.System
        }

        return OpenAiChatMessage(
            role = role,
            content = this.content
        )
    }

}