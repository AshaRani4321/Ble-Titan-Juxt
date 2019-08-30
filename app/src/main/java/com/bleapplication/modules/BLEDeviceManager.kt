package com.bleapplication.modules

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import com.bleapplication.interfaces.OnDeviceScanListener
import com.bleapplication.modules.BleDeviceData
import java.util.*

object BLEDeviceManager {

    private val TAG = "BLEDeviceManager"
    private var mDeviceObject: BleDeviceData? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mOnDeviceScanListener: OnDeviceScanListener? = null
    private var mIsContinuesScan: Boolean = false
    private var mHandler: Handler? = null

    private var mLEScanner: BluetoothLeScanner? = null
    private var mScanning: Boolean = false
    private val mStopScanHandler = Handler()
    private val SCAN_TIMEOUT_MS: Long = 10000


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

    /**
     * Scan The BLE Device
     * Check the available BLE devices in the Surrounding
     * If the device is Already scanning then stop Scanning
     * Else start Scanning and check 10 seconds
     * Send the available devices as a callback to the system
     * Finish Scanning after 10 Seconds
     */
    fun scanBLEDevice(isContinuesScan: Boolean) {
        try {
            mIsContinuesScan = isContinuesScan
            mHandler = Handler()

            if (mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled) {
                scan()
            }
            /**
             * Stop Scanning after a Period of Time
             * Set a 10 Sec delay time and Stop Scanning
             * collect all the available devices in the 10 Second
             */
            if (isContinuesScan) {
                mHandler?.postDelayed({
                    // Set a delay time to Scanning
                    if (mDeviceObject != null)
                        stopScan(mDeviceObject)
                }, BLEConstants.SCAN_PERIOD) // Delay Period
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }


    private fun scan() {
        if (isLollyPopOrAbove()) {// Start Scanning For Lollipop devices
            mBluetoothAdapter?.bluetoothLeScanner?.startScan(
                scanFilters(),
                scanSettings(), scanCallback
            ) // Start BLE device Scanning in a separate thread
        } else {
            mBluetoothAdapter?.startLeScan(mLeScanCallback) // Start Scanning for Below Lollipop device
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

    /* Device scan CallBack for Above Lollipop device */
    var scanCallback = object : ScanCallback() {
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
                    stopScan(mDeviceObject)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            stopScan(mDeviceObject)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

            if (null != mOnDeviceScanListener) {
                val data = BleDeviceData()
                var ss=results?.get(0)
            }
        }
    }

    /* Device scan CallBack for Below Lollipop device */
    var mLeScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        if (device != null && device.address != null && null != mOnDeviceScanListener) {
            // Some case the Device Name will return as Null from BLE because of Swathing from one device to another
            val data = BleDeviceData()
            data.mDeviceName = (device.name)
            data.mDeviceAddress = (device.address)

            /**
             * Save the Valid Device info into a list
             * The List will display to the UI as a popup
             * User has an option to select one BLE from the popup
             * After selecting one BLE, the connection will establish and
             * communication channel will create if its valid device.
             */
            if (data.mDeviceName.contains("Oskron")) {
                mDeviceObject = data
                stopScan(mDeviceObject)
            }
        }
    }

    /* Stop Scanning Device*/
    private fun stopScan(data: BleDeviceData?) {

        if (mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled &&
            if (isLollyPopOrAbove()) scanCallback != null else mLeScanCallback != null
        ) {
            if (mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled) { // check if its Already available
                if (isLollyPopOrAbove()) {
                    mBluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
                } else {
                    mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                }
            }
            if (data != null) {
                mOnDeviceScanListener?.onScanCompleted(data)
            }
        }
    }


    //DeviceConnectionActivity Methods
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

}