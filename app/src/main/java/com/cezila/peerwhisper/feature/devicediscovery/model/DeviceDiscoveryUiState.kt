package com.cezila.peerwhisper.feature.devicediscovery.model

import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel

data class DeviceDiscoveryUiState(
    val isDiscovering: Boolean = false,
    val devices: List<WifiP2pDeviceModel> = emptyList(),
    val errorMessage: String? = null
)