package com.cezila.peerwhisper.feature.chatcommunication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cezila.peerwhisper.feature.chatcommunication.model.ChatMessage
import com.cezila.peerwhisper.feature.chatcommunication.model.ChatUiState
import com.cezila.peerwhisper.feature.chatcommunication.repository.IChatCommunicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: IChatCommunicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            chatRepository.messages.collect { message ->
                _uiState.update { currentState ->
                    currentState.copy(messages = currentState.messages + message)
                }
            }
        }
    }

    fun initializeConnection(isGroupOwner: Boolean, hostAddress: String?) {
        viewModelScope.launch {
            chatRepository.initializeConnection(isGroupOwner, hostAddress)
        }
    }

    fun sendMessage(content: String) {
        val message = ChatMessage(
            sender = "Me", //  Change to collect in Shared Preferences
            content = content,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            chatRepository.sendMessage(message)
            _uiState.update { currentState ->
                currentState.copy(messages = currentState.messages + message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.closeConnection()
    }
}