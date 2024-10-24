package com.cezila.peerwhisper.feature.chatcommunication.service

import com.cezila.peerwhisper.feature.chatcommunication.model.ChatMessage
import kotlinx.coroutines.flow.SharedFlow

interface ISocketService {

    val incomingMessages: SharedFlow<ChatMessage>
    suspend fun startServer()
    suspend fun connectToServer(hostAddress: String)
    suspend fun sendMessage(message: ChatMessage)
    fun closeConnection()

}