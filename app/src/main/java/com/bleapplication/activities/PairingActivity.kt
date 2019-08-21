package com.bleapplication.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bleapplication.R
import com.bleapplication.interfaces.OnDeviceConnectedListener
import com.bleapplication.interfaces.OnDeviceScanListener
import com.bleapplication.modules.BLEConnectionManager
import com.bleapplication.modules.BLEConstants
import com.bleapplication.modules.BLEDeviceManager
import com.bleapplication.modules.BleDeviceData

class PairingActivity : AppCompatActivity(), OnDeviceScanListener, OnDeviceConnectedListener {

    private var mDeviceAddress: String = ""
    private val TAG = "PairingActivity"
    private val REQUEST_ENABLE_BT = 1000

    override fun OnConnected(isConnected: Boolean) {
        setContentView(R.layout.device_connected)
    }

    override fun onScanCompleted(deviceDataList: BleDeviceData) {
        //Initiate a dialog Fragment from here and ask the user to select his device
        // If the application already know the Mac address, we can simply call connect device
        mDeviceAddress = deviceDataList.mDeviceAddress
        BLEConnectionManager.connect(deviceDataList.mDeviceAddress)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_searching)
        if (checkLocationPermission())
            scanDevice(false)
    }

    /**
     * Check the Location Permission before calling the BLE API's
     */
    private fun checkLocationPermission(): Boolean {
        if (isAboveMarshmallow()) {
            when {
                isLocationPermissionEnabled() -> initBLEModule()
            }
        } else {
            initBLEModule()
        }
        return true
    }

    /**
     * Check with the system- If the permission already enabled or not
     */
    private fun isLocationPermissionEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * The location permission is incorporated in Marshmallow and Above
     */
    private fun isAboveMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     *After receive the Location Permission, the Application need to initialize the
     * BLE Module and BLE Service
     */
    private fun initBLEModule() {
        // BLE initialization
        if (!BLEDeviceManager.init(this)) {
            Toast.makeText(this, "BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show()
            return
        }
        registerServiceReceiver()
        BLEDeviceManager.setListener(this)

        if (!BLEDeviceManager.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        BLEConnectionManager.initBLEService(this@PairingActivity)
    }

    /**
     * Register GATT update receiver
     */
    private fun registerServiceReceiver() {
        this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }


    /**
     * Scan the BLE device if the device address is null
     * else the app will try to connect with device with existing device address.
     */
    private fun scanDevice(isContinuesScan: Boolean) {
        if (!mDeviceAddress.isNullOrEmpty()) {
            connectDevice()
        } else {
            BLEDeviceManager.scanBLEDevice(isContinuesScan)
        }
    }

    /**
     * Connect the application with BLE device with selected device address.
     */
    private fun connectDevice() {
        Handler().postDelayed({
            BLEConnectionManager.initBLEService(this@PairingActivity)
            if (BLEConnectionManager.connect(mDeviceAddress)) {
                setContentView(R.layout.device_connected)

                Toast.makeText(this@PairingActivity, "DEVICE CONNECTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@PairingActivity, "DEVICE CONNECTION FAILED", Toast.LENGTH_SHORT).show()
            }
        }, 100)
    }


    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            when {
                BLEConstants.ACTION_GATT_CONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_CONNECTED ")
                    BLEConnectionManager.findBLEGattService(this@PairingActivity)
                }
                BLEConstants.ACTION_GATT_DISCONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_DISCONNECTED ")
                }
                BLEConstants.ACTION_GATT_SERVICES_DISCOVERED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED1 ")
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    BLEConnectionManager.findBLEGattService(this@PairingActivity)
                }

//                BLEConstants.ACTION_DEVICE_CONNECTED.equals(action) -> {
//                    Log.i(TAG, "DEVICE_CONNECTED_SUCCESSFULLY ")
//                    setContentView(R.layout.device_connected)
//                }
                BLEConstants.ACTION_DATA_AVAILABLE.equals(action) -> {
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    Log.i(TAG, "ACTION_DATA_AVAILABLE $data")

                    if (data == "-128 2 5") {
                        setContentView(R.layout.device_connected)
                    }
                    val returnValue = byteArrayOf(0x80.toByte(), 0x0f, 0x04, 0x01, 0x01)
                    BLEConnectionManager.writeToConnectedDevice(returnValue)
                }
                BLEConstants.ACTION_DATA_WRITTEN.equals(action) -> {
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    if (data == "-128 15")
                        launchHomeScreen()
                        Log.i(TAG, "ACTION_DATA_WRITTEN ")
                }
            }
        }
    }


    /**
     * Intent filter for Handling BLEGattService broadcast.
     */
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BLEConstants.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BLEConstants.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(BLEConstants.ACTION_DATA_WRITTEN)

        return intentFilter
    }

    private fun launchHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    /**
     * Unregister GATT update receiver
     */
    private fun unRegisterServiceReceiver() {
        try {
            this.unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            //May get an exception while user denies the permission and user exists the app
            Log.e(TAG, e.message)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        BLEConnectionManager.disconnect()
        unRegisterServiceReceiver()
    }
}
