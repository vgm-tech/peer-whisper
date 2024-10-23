package com.cezila.peerwhisper.feature.wifidirect

import android.net.wifi.p2p.WifiP2pManager
import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel
import kotlinx.coroutines.flow.StateFlow

interface IWifiDirectService {

    /** Advertise the local service */
    fun advertiseService()

    /** Start discovering services */
    fun discoverServices()

    /** Stop discovering services */
    fun stopDiscovery()

    /** Remove the local service */
    fun removeService()

    /** Expose the discovered devices */
    val discoveredDevices: StateFlow<List<WifiP2pDeviceModel>>

    /** Connect to a device */
    fun connectToDevice(
        deviceAddress: String,
        onSuccess: () -> Unit,
        onFailure: (reason: Int) -> Unit
    )

    /** Request connection info */
    fun requestConnectionInfo(listener: WifiP2pManager.ConnectionInfoListener)

}