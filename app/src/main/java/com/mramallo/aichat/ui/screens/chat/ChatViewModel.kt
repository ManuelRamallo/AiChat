package com.mramallo.aichat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class Message(
    val id: Long,
    val text: String,
    val isUser: Boolean
)

class MainViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val userMsg = Message(id = System.currentTimeMillis(), text = trimmed, isUser = true)
        _messages.value = _messages.value + userMsg

        // Simular respuesta de IA
        viewModelScope.launch {
            delay(600)
            val aiReply = Message(
                id = System.currentTimeMillis() + 1,
                text = "IA: ${generateFakeReply(trimmed)}",
                isUser = false
            )
            _messages.value = _messages.value + aiReply
        }
    }

    private fun generateFakeReply(prompt: String): String {
        return when {
            prompt.endsWith("?") -> "Interesante pregunta. Diría que la respuesta es 42."
            prompt.length < 10 -> "Cuéntame un poco más para ayudarte mejor."
            else -> "Entendido. Procesaré tu mensaje y te daré más detalles pronto."
        }
    }
}


