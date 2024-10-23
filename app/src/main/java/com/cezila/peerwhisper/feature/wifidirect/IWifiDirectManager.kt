package com.cezila.peerwhisper.feature.wifidirect

import android.net.wifi.p2p.WifiP2pManager
import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel
import kotlinx.coroutines.flow.StateFlow

interface IWifiDirectManager {

    /** Start Wi-Fi Direct operations */
    fun start()

    /** Stop Wi-Fi Direct operations */
    fun stop()

    /** Expose the discovered devices */
    val discoveredDevices: StateFlow<List<WifiP2pDeviceModel>>

    /** Start discovering services */
    fun discoverServices()

    /** Connect to a device */
    fun connectToDevice(
        device: WifiP2pDeviceModel,
        onSuccess: () -> Unit,
        onFailure: (reason: Int) -> Unit
    )

    /** Set the connection info listener */
    fun setConnectionInfoListener(listener: WifiP2pManager.ConnectionInfoListener)

}