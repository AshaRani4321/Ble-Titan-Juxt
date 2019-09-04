package com.bleapplication.modules

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.ParcelUuid
import com.bleapplication.interfaces.OnDeviceScanListener
import java.util.*
import kotlin.collections.ArrayList

object BLEDeviceConnectionManager {

    private var mDeviceObject: BleDeviceData? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mOnDeviceScanListener: OnDeviceScanListener? = null
    private var mScanning: Boolean = false
    private val mStopScanHandler = Handler()
    private val SCAN_TIMEOUT_MS: Long = 2000

    /**
     * Initialize BluetoothAdapter
     * Check the device has the hardware feature BLE
     * Then enable the hardware,
     */
    fun init(context: Context): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        return mBluetoothAdapter != null && context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    /**
     * Check bluetooth is enabled or not.
     */
    fun isEnabled(): Boolean {

        return mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled
    }

    /**
     * setListener
     */
    fun setListener(onDeviceScanListener: OnDeviceScanListener) {
        mOnDeviceScanListener = onDeviceScanListener
    }


    private fun isLollyPopOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }


    fun scanBLEDevice() {
        mScanning = true
        mBluetoothAdapter!!.getBluetoothLeScanner().startScan(scanFilters(), scanSettings(), mScanCallback)
        // Stops scanning after a pre-defined scan period.
        mStopScanHandler.postDelayed(mStopScanRunnable, SCAN_TIMEOUT_MS)
    }

    private val mStopScanRunnable = Runnable {
        if (mDeviceObject != null)
            stopLeScan(mDeviceObject)
    }

    private val mScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            if (null != mOnDeviceScanListener && result != null && result.device != null && result.device.address != null) {
                val data = BleDeviceData()
                data.mDeviceName = if (result.device.name != null)
                    result.device.name else "Unknown"
                // Some case the Device Name will return as Null from BLE
                // because of Swathing from one device to another
                data.mDeviceAddress = (result.device.address)

                if (data.mDeviceName.contains("Oskron")) {
                    mDeviceObject = data
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }
    }

    private fun stopLeScan(data: BleDeviceData?) {
        if (mScanning) {
            if (data != null) {
                mScanning = false
                mBluetoothAdapter!!.getBluetoothLeScanner().stopScan(mScanCallback)
                mStopScanHandler.removeCallbacks(mStopScanRunnable)
                mOnDeviceScanListener?.onScanCompleted(data)
            }
        }
    }


    private fun scanFilters(): List<ScanFilter> {
        val filters = ArrayList<ScanFilter>()
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString("F0BA3120-6CAC-4C99-9089-4B0A1DF45002"))).build())
        return filters
    }

    private fun scanSettings(): ScanSettings {
        return ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
    }
}