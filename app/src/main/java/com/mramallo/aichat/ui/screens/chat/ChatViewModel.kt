package com.mramallo.aichat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mramallo.aichat.data.repository.ChatRepository
import com.mramallo.aichat.ui.screens.chat.model.ChatState
import com.mramallo.aichat.ui.screens.chat.model.ConversationItem
import com.mramallo.aichat.ui.screens.chat.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    private val _currentConversationId = MutableStateFlow<String?>(null)


    private val messagesFlow = _currentConversationId.flatMapLatest { conversationId ->
        if (conversationId != null) {
            chatRepository.getMessagesForConversation(conversationId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val conversationsFlow = chatRepository.getAllConversations().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val state: StateFlow<ChatState> = combine(
        messagesFlow,
        conversationsFlow,
        _currentConversationId,
        _uiState
    ) { messages, conversations, currentId, uiState ->
        ChatState(
            messages = messages,
            conversations = conversations.map { ConversationItem(id = it.id, title = it.title) },
            currentConversationId = currentId,
            isLoading = uiState.isLoading,
            error = uiState.error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChatState()
    )

    init {
        viewModelScope.launch {
            _currentConversationId.value = chatRepository.getCurrentConversationId()
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            chatRepository.sendMessage(content)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error: ${exception.localizedMessage}"
                        )
                    }
                }
        }
    }

    fun createNewConversation() {
        viewModelScope.launch {
            val newConversationId = chatRepository.createNewConversation("Nueva conversación")
            _currentConversationId.value = newConversationId
        }
    }

    fun selectConversation(conversationId: String) {
        _currentConversationId.value = conversationId
    }

    fun deleteCurrentConversation() {
        viewModelScope.launch {
            _currentConversationId.value?.let { id ->
                chatRepository.deleteConversation(id)
                _currentConversationId.value = chatRepository.getCurrentConversationId()
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            val wasCurrentConversation = _currentConversationId.value == conversationId

            chatRepository.deleteConversation(conversationId)

            if(wasCurrentConversation){
                _currentConversationId.value = chatRepository.getCurrentConversationId()
            }
        }
    }
}


