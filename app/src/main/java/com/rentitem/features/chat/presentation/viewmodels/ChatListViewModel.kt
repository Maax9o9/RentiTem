package com.rentitem.features.chat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rentitem.features.chat.domain.entities.Conversation
import com.rentitem.features.chat.domain.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatListUiState {
    object Loading : ChatListUiState()
    data class Success(val conversations: List<Conversation>) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private val _totalUnreadCount = MutableStateFlow(0)
    val totalUnreadCount: StateFlow<Int> = _totalUnreadCount.asStateFlow()

    init {
        loadConversations()
        loadTotalUnread()
    }

    private fun loadTotalUnread() {
        viewModelScope.launch {
            chatRepository.getTotalUnreadCount().collect { count ->
                _totalUnreadCount.value = count
            }
        }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            try {
                chatRepository.getConversations().collect { conversations ->
                    _uiState.value = ChatListUiState.Success(conversations)
                }
            } catch (e: Exception) {
                _uiState.value = ChatListUiState.Error(e.message ?: "Error al cargar chats")
            }
        }
    }
}
