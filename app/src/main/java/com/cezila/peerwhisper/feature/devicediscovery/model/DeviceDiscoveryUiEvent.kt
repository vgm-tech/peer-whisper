package com.cezila.peerwhisper.feature.devicediscovery.model

import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel

sealed class DeviceDiscoveryUiEvent {
    data object StartDiscovery : DeviceDiscoveryUiEvent()
    data object StopDiscovery : DeviceDiscoveryUiEvent()
    data class ConnectToDevice(val device: WifiP2pDeviceModel) : DeviceDiscoveryUiEvent()
    data class ShowErrorMessage(val message: String) : DeviceDiscoveryUiEvent()
}