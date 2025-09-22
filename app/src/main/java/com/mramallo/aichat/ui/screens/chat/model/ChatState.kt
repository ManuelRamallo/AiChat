package com.mramallo.aichat.ui.screens.chat.model

import com.mramallo.aichat.data.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val conversations: List<ConversationItem> = emptyList(),
    val currentConversationId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
