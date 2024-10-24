package com.cezila.peerwhisper.feature.chatcommunication.service

import com.cezila.peerwhisper.feature.chatcommunication.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject

class SocketServiceImpl @Inject constructor() : ISocketService {

    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private val _incomingMessages = MutableSharedFlow<ChatMessage>()
    override val incomingMessages: SharedFlow<ChatMessage> = _incomingMessages

    override suspend fun startServer() {
        withContext(Dispatchers.IO) {
            serverSocket = ServerSocket(8888)
            socket = serverSocket?.accept()
            listenForMessages()
        }
    }

    override suspend fun connectToServer(hostAddress: String) {
        withContext(Dispatchers.IO) {
            socket = Socket()
            socket?.connect(InetSocketAddress(hostAddress, 8888), 5000)
            listenForMessages()
        }
    }

    private suspend fun listenForMessages() {
        withContext(Dispatchers.IO) {
            socket?.let { sock ->
                val reader = BufferedReader(InputStreamReader(sock.getInputStream()))
                var line: String?
                while (sock.isConnected) {
                    line = reader.readLine()
                    if (line != null) {
                        val message = Json.decodeFromString<ChatMessage>(line)
                        _incomingMessages.emit(message)
                    }
                }
            }
        }
    }

    override suspend fun sendMessage(message: ChatMessage) {
        withContext(Dispatchers.IO) {
            socket?.let { sock ->
                val writer = BufferedWriter(OutputStreamWriter(sock.getOutputStream()))
                val jsonMessage = Json.encodeToJsonElement(message)
                writer.write(jsonMessage.toString())
                writer.newLine()
                writer.flush()
            }
        }
    }

    override fun closeConnection() {
        socket?.close()
        serverSocket?.close()
    }

}