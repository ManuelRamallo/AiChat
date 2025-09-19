package com.mramallo.aichat.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class ConversationWithMessages(
    @Embedded
    val conversation: ConversationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "conversationId"
    )
    val messages: List<MessageEntity>
)
