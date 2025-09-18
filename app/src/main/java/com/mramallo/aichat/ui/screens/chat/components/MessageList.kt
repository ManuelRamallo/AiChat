package com.mramallo.aichat.ui.screens.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mramallo.aichat.ui.screens.chat.Message

@Composable
fun MessageList(
    modifier: Modifier,
    messages: List<Message>,
    contentPadding: PaddingValues,
    listState: LazyListState
) {
    if (messages.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Aún no hay mensajes")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = contentPadding,
            state = listState
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(message = msg)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val shape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = if (!message.isUser) 0.dp else 12.dp,
        bottomEnd = if (message.isUser) 0.dp else 12.dp
    )
    val background = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val content = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(shape),
            color = background
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = content,
                textAlign = if (message.isUser) TextAlign.End else TextAlign.Start
            )
        }
    }
}


