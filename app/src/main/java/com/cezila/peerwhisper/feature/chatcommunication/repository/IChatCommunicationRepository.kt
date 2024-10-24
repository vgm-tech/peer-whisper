package com.cezila.peerwhisper.feature.chatcommunication.repository

import com.cezila.peerwhisper.feature.chatcommunication.model.ChatMessage
import kotlinx.coroutines.flow.SharedFlow

interface IChatCommunicationRepository {
    val messages: SharedFlow<ChatMessage>
    suspend fun initializeConnection(isGroupOwner: Boolean, hostAddress: String?)
    suspend fun sendMessage(message: ChatMessage)
    fun closeConnection()
}