package com.cezila.peerwhisper.feature.chatcommunication.repository

import com.cezila.peerwhisper.feature.chatcommunication.model.ChatMessage
import com.cezila.peerwhisper.feature.chatcommunication.service.ISocketService
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ChatCommunicationRepositoryImpl @Inject constructor(
    private val socketService: ISocketService
) : IChatCommunicationRepository {

    override val messages: SharedFlow<ChatMessage> = socketService.incomingMessages

    override suspend fun initializeConnection(isGroupOwner: Boolean, hostAddress: String?) {
        if (isGroupOwner) {
            socketService.startServer()
        } else {
            hostAddress?.let { socketService.connectToServer(it) }
        }
    }

    override suspend fun sendMessage(message: ChatMessage) {
        socketService.sendMessage(message)
    }

    override fun closeConnection() {
        socketService.closeConnection()
    }
}