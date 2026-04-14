package com.rentitem.features.chat.presentation.viewmodels

import com.rentitem.features.chat.domain.entities.Message

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}
