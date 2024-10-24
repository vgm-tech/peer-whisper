package com.cezila.peerwhisper.feature.chatcommunication.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthMessage(
    val name: String,
    val profileImage: String // Base64 encoded image
)
