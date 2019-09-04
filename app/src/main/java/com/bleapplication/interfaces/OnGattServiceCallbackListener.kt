package com.bleapplication.interfaces

import com.bleapplication.modules.BleDeviceData

interface OnGattServiceCallbackListener {

    /**
     * Scan Completed -
     *
     * @param deviceDataList - Send available devices as a list to the init Activity
     * The List Contain, device name and mac address,
     */
    fun isDeviceConnected(isConnected: Boolean)

    fun onCharChangeSuccess(data: ByteArray)
}