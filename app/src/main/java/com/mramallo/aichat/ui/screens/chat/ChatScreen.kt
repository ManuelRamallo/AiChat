package com.mramallo.aichat.ui.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.rememberLazyListState
import com.mramallo.aichat.ui.screens.chat.components.ChatInputField
import com.mramallo.aichat.ui.screens.chat.components.MessageList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    var inputText by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    fun handleSend() {
        val textToSend = inputText.trim()
        if (textToSend.isNotEmpty()) {
            viewModel.sendMessage(textToSend)
            inputText = ""
            focusManager.clearFocus()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "AI Chat") })
        },
        bottomBar = {
            ChatInputField(
                modifier = Modifier.fillMaxWidth(),
                value = inputText,
                onValueChange = { inputText = it },
                onSend = { handleSend() }
            )
        }
    ) { paddingValues ->
        val messages by viewModel.messages.collectAsState(initial = emptyList())
        val listState = rememberLazyListState()

        // Auto-scroll al final cuando se añaden mensajes
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            MessageList(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                messages = messages,
                contentPadding = PaddingValues(12.dp),
                listState = listState
            )
        }
    }
}

// MessageBubble y ChatInputField movidos a ui/MessageList.kt y ui/ChatInputField.kt


