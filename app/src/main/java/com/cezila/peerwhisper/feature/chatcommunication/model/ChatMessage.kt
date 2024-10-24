package com.cezila.peerwhisper.feature.chatcommunication.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val sender: String,
    val content: String,
    val timestamp: Long
)
