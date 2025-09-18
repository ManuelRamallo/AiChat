package com.mramallo.aichat.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val role: MessageRole
) {
    val isFromUser: Boolean
        get() = role == MessageRole.USER
}
