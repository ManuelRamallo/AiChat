package com.mramallo.aichat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _lastSentMessage = MutableStateFlow("")
    val lastSentMessage: StateFlow<String> = _lastSentMessage

    fun sendMessage(text: String) {
        _lastSentMessage.value = text
    }
}


