package com.cezila.peerwhisper.feature.wifidirect

import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Looper
import com.cezila.peerwhisper.feature.wifidirect.model.WifiP2pDeviceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class WifiDirectServiceImpl @Inject constructor(
    context: Context
) : IWifiDirectService {

    companion object {
        private const val SERVICE_INSTANCE = "MyAppService"
        private const val SERVICE_REG_TYPE = "_presence._tcp"
    }

    private val wifiP2pManager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel: WifiP2pManager.Channel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null)

    private val _discoveredDevices = MutableStateFlow<List<WifiP2pDeviceModel>>(emptyList())
    override val discoveredDevices: StateFlow<List<WifiP2pDeviceModel>> = _discoveredDevices

    private var serviceRequest: WifiP2pDnsSdServiceRequest? = null

    override fun advertiseService() {
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            SERVICE_INSTANCE,
            SERVICE_REG_TYPE,
            null // Additional TXT records can be added here
        )

        wifiP2pManager.addLocalService(
            channel,
            serviceInfo,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Service registered successfully
                    // You might log this or update a status
                }

                override fun onFailure(reason: Int) {
                    // Handle failure
                }
            })
    }

    /** Start discovering services */
    override fun discoverServices() {
        // Initialize the listeners
        initServiceListeners()

        // Create a service request
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        wifiP2pManager.addServiceRequest(
            channel,
            serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Service request added
                }

                override fun onFailure(reason: Int) {
                    // Handle failure
                }
            })

        // Start service discovery
        wifiP2pManager.discoverServices(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Discovery started
            }

            override fun onFailure(reason: Int) {
                // Handle failure
            }
        })
    }

    /** Initialize service listeners */
    private fun initServiceListeners() {
        // Listener for service responses
        val serviceListener =
            WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, srcDevice ->
                if (instanceName == SERVICE_INSTANCE) {
                    // Device is running our app
                    val device = WifiP2pDeviceModel(
                        deviceName = srcDevice.deviceName,
                        deviceAddress = srcDevice.deviceAddress
                    )
                    addDevice(device)
                }
            }

        // Listener for TXT record responses (if needed)
        val txtRecordListener =
            WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, txtRecordMap, srcDevice ->
                // Handle TXT records if you have any
            }

        wifiP2pManager.setDnsSdResponseListeners(channel, serviceListener, txtRecordListener)
    }

    /** Add a discovered device to the list */
    private fun addDevice(device: WifiP2pDeviceModel) {
        val currentList = _discoveredDevices.value.toMutableList()
        if (!currentList.contains(device)) {
            currentList.add(device)
            _discoveredDevices.value = currentList
        }
    }

    /** Stop discovering services */
    override fun stopDiscovery() {
        serviceRequest?.let {
            wifiP2pManager.removeServiceRequest(
                channel,
                it,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        // Service request removed
                    }

                    override fun onFailure(reason: Int) {
                        // Handle failure
                    }
                })
        }
        wifiP2pManager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Discovery stopped
            }

            override fun onFailure(reason: Int) {
                // Handle failure
            }
        })
    }

    /** Remove the local service */
    override fun removeService() {
        wifiP2pManager.clearLocalServices(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Local services cleared
            }

            override fun onFailure(reason: Int) {
                // Handle failure
            }
        })
    }

    override fun connectToDevice(
        deviceAddress: String,
        onSuccess: () -> Unit,
        onFailure: (reason: Int) -> Unit
    ) {
        val config = WifiP2pConfig().apply {
            this.deviceAddress = deviceAddress
            wps.setup = WpsInfo.PBC
        }

        wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                onSuccess()
            }

            override fun onFailure(reason: Int) {
                onFailure(reason)
            }
        })
    }

    override fun requestConnectionInfo(listener: WifiP2pManager.ConnectionInfoListener) {
        wifiP2pManager.requestConnectionInfo(channel, listener)
    }
}