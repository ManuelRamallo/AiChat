package com.mramallo.aichat.data.model

import com.mramallo.aichat.data.local.MessageEntity

fun ChatMessage.toEntity(conversationId: String, position: Int): MessageEntity {
    return MessageEntity(
        id = this.id,
        conversationId = conversationId,
        content = this.content,
        role = this.role,
        position = position
    )
}

fun MessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = this.id,
        content = this.content,
        role = this.role
    )
}