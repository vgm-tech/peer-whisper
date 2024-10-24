package com.cezila.peerwhisper.feature.chatcommunication.model

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isConnected: Boolean = false,
    val errorMessage: String? = null
)
