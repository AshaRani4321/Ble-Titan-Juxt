package com.bleapplication.modules

class BLEConstants {

    companion object {
        const val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED" //Strings representing actions to broadcast to activities
        const val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED" // Old com.zco.ble
        const val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        const val ACTION_DATA_WRITTEN = "ACTION_DATA_WRITTEN"
        const val EXTRA_DATA = "EXTRA_DATA"
        const val EXTRA_UUID = "EXTRA_UUID"
        const val ACTION_DEVICE_CONNECTED = "DEVICE_CONNECTED_SUCCESSFULLY"

        const val SCAN_PERIOD: Long = 10000
    }
}