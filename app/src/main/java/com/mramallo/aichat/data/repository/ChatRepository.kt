package com.mramallo.aichat.data.repository

import com.mramallo.aichat.data.datasource.OpenAIDataSource
import com.mramallo.aichat.data.local.ConversationDao
import com.mramallo.aichat.data.local.ConversationEntity
import com.mramallo.aichat.data.local.MessageDao
import com.mramallo.aichat.data.model.ChatMessage
import com.mramallo.aichat.data.model.MessageRole
import com.mramallo.aichat.data.model.toDomain
import com.mramallo.aichat.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val openAIDataSource: OpenAIDataSource,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    private var currentConversationId: String? = null

    suspend fun sendMessage(content: String): Result<ChatMessage> {
        if(content.isBlank()) return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío"))

        try {
            val conversationId = getCurrentConversationId()

            val isFirstUserMessage = isFirstUserMessage(conversationId)
            val userMessage = createUserMessage(content)
            saveMessage(userMessage, conversationId)

            val allMessages = messageDao.getMessagesByConversationId(conversationId).map {
                it.map { entity -> entity.toDomain() }
            }.first()

            val response = openAIDataSource.getChatCompletion(allMessages)

            val assistantMessage = createAssistantMessage(response)
            saveMessage(assistantMessage, conversationId)

            conversationDao.updateConversationTimestamp(conversationId, Instant.now().toEpochMilli())

            if(isFirstUserMessage) {
                generateTitleForConversation(conversationId, content)
            }

            return Result.success(assistantMessage)
        } catch (e: Exception) {
            return Result.failure(e)
        }

    }

    suspend fun saveMessage(message: ChatMessage, conversationId: String) {
        val nextPosition = messageDao.getMessageCount(conversationId)
        val messageEntity = message.toEntity(conversationId, nextPosition)
        messageDao.insertMessage(messageEntity)
    }

    suspend fun createNewConversation(title: String = ""): String {
        val conversationId = UUID.randomUUID().toString()
        val conversation = ConversationEntity(
            id = conversationId,
            title = title,
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli()
        )
        conversationDao.insertConversation(conversation)

        val systemMessage = createSystemMessage()
        saveMessage(systemMessage, conversationId)

        currentConversationId = conversationId

        return conversationId
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return messageDao.getMessagesByConversationId(conversationId)
            .map { messages ->
                messages.map { it.toDomain() }
                    .filter { it.role != MessageRole.SYSTEM }
            }
    }

    suspend fun updateConversationTitle(conversationId: String, title: String) {
        val conversation = conversationDao.getConversationById(conversationId).first()
        if(conversation != null) {
            val updated = conversation.copy(title = title)
            conversationDao.updateConversation(updated)
        }
    }

    suspend fun isFirstUserMessage(conversationId: String): Boolean {
        val messages = messageDao.getMessagesByConversationId(conversationId).first()
        return messages.size <= 1
    }

    fun getAllConversations(): Flow<List<ConversationEntity>> {
        return conversationDao.getAllConversations()
    }

    suspend fun getCurrentConversationId(): String {
        if(currentConversationId == null) {
            val conversations = conversationDao.getAllConversations().first()
            currentConversationId = if(conversations.isNotEmpty()) {
                conversations.first().id
            } else {
                createNewConversation()
            }
        }

        return currentConversationId!!
    }

    suspend fun deleteConversation(conversationId: String) {
        conversationDao.deleteConversation(conversationId)

        if(conversationId == currentConversationId) {
            currentConversationId = null
        }
    }

    private fun createUserMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.USER
        )
    }

    private fun createAssistantMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.ASSISTANT
        )
    }

    private fun createSystemMessage(): ChatMessage {
        return ChatMessage(
            content = "Eres un asistente útil y amigable. Responde de manera clara y concisa.",
            role = MessageRole.ASSISTANT
        )
    }

    /**
     * Genera un título para la conversación usando IA y actualiza la conversación
     */
    private suspend fun generateTitleForConversation(conversationId: String, firstMessage: String) {
        try {
            val generatedTitle = openAIDataSource.generateConversationTitle(firstMessage)
            updateConversationTitle(conversationId, generatedTitle)
        } catch (e: Exception) {
            // Si hay un error al generar el título, usar el primer mensaje como fallback
            updateConversationTitle(conversationId, firstMessage)
        }
    }

}

