package com.cezila.peerwhisper.feature.devicediscovery.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cezila.peerwhisper.feature.devicediscovery.model.ConnectionInfo
import com.cezila.peerwhisper.feature.devicediscovery.model.DeviceDiscoveryUiEvent
import com.cezila.peerwhisper.feature.devicediscovery.model.DeviceDiscoveryUiState
import com.cezila.peerwhisper.feature.wifidirect.IWifiDirectManager
import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDiscoveryViewModel @Inject constructor(
    private val wifiDirectManager: IWifiDirectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDiscoveryUiState())
    val uiState: StateFlow<DeviceDiscoveryUiState> = _uiState.asStateFlow()

    init {
        observeDiscoveredDevices()
    }

    private fun observeDiscoveredDevices() {
        viewModelScope.launch {
            wifiDirectManager.discoveredDevices.collect { devices ->
                _uiState.update { it.copy(devices = devices) }
            }
        }
    }

    fun onEvent(event: DeviceDiscoveryUiEvent) {
        when (event) {
            is DeviceDiscoveryUiEvent.StartDiscovery -> startDiscovery()
            is DeviceDiscoveryUiEvent.StopDiscovery -> stopDiscovery()
            is DeviceDiscoveryUiEvent.ConnectToDevice -> connectToDevice(event.device)
            is DeviceDiscoveryUiEvent.ShowErrorMessage -> showErrorMessage(event.message)
        }
    }

    private fun startDiscovery() {
        _uiState.update { it.copy(isDiscovering = true, errorMessage = null) }
        wifiDirectManager.start()
        wifiDirectManager.discoverServices()
    }

    private fun stopDiscovery() {
        _uiState.update { it.copy(isDiscovering = false) }
        wifiDirectManager.stop()
    }

    private fun connectToDevice(device: WifiP2pDeviceModel) {
        wifiDirectManager.setConnectionInfoListener { info ->
            _uiState.update {
                it.copy(
                    connectionInfo = ConnectionInfo(
                        isGroupOwner = info.isGroupOwner,
                        hostAddress = info.groupOwnerAddress.hostAddress ?: ""
                    )
                )
            }
        }

        wifiDirectManager.connectToDevice(device,
            onSuccess = {},
            onFailure = { reason ->
                val errorMsg = "Failed to connect: $reason"
                showErrorMessage(errorMsg)
            })
    }

    private fun showErrorMessage(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    override fun onCleared() {
        super.onCleared()
        wifiDirectManager.stop()
    }
}