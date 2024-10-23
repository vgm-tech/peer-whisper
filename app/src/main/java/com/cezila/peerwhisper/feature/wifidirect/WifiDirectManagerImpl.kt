package com.cezila.peerwhisper.feature.wifidirect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class WifiDirectManagerImpl @Inject constructor(
    private val context: Context,
    private val wifiDirectService: IWifiDirectService
): IWifiDirectManager {

    private val intentFilter = IntentFilter()
    private var connectionInfoListener: WifiP2pManager.ConnectionInfoListener? = null

    init {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun start() {
        context.registerReceiver(wifiP2pReceiver, intentFilter)
        wifiDirectService.advertiseService()
    }

    override fun stop() {
        context.unregisterReceiver(wifiP2pReceiver)
        wifiDirectService.stopDiscovery()
        wifiDirectService.removeService()
    }

    /** Expose the discovered devices */
    override val discoveredDevices: StateFlow<List<WifiP2pDeviceModel>> = wifiDirectService.discoveredDevices

    /** Start discovering services */
    override fun discoverServices() {
        wifiDirectService.discoverServices()
    }

    /** Connect to a device */
    override fun connectToDevice(device: WifiP2pDeviceModel, onSuccess: () -> Unit, onFailure: (reason: Int) -> Unit) {
        wifiDirectService.connectToDevice(device.deviceAddress, onSuccess, onFailure)
    }

    /** Set the connection info listener */
    override fun setConnectionInfoListener(listener: WifiP2pManager.ConnectionInfoListener) {
        connectionInfoListener = listener
    }

    /** BroadcastReceiver to handle Wi-Fi Direct events */
    private val wifiP2pReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    // Check if Wi-Fi P2P is enabled
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        // Wi-Fi P2P is enabled
                    } else {
                        // Wi-Fi P2P is not enabled
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    // Peers changed, but we're handling discovery via services
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo?.isConnected == true) {
                        // Connected to another device
                        connectionInfoListener?.let { wifiDirectService.requestConnectionInfo(it) }
                    } else {
                        // Disconnected
                    }
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    // This device's details have changed
                }
            }
        }
    }
}