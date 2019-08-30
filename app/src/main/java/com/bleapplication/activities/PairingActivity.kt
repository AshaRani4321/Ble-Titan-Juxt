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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bleapplication.R
import com.bleapplication.interfaces.OnDeviceScanListener
import com.bleapplication.modules.*

class PairingActivity : AppCompatActivity(), OnDeviceScanListener {

    private var mDeviceAddress: String = ""
    private val TAG = "PairingActivity"
    private val REQUEST_ENABLE_BT = 1000

    override fun onScanCompleted(deviceDataList: BleDeviceData) {
        //Initiate a dialog Fragment from here and ask the user to select his device
        // If the application already know the Mac address, we can simply call connect device
        mDeviceAddress = deviceDataList.mDeviceAddress
        if (!BLEConnectionManager.connect(deviceDataList.mDeviceAddress))
            scanDevice(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_searching)

        checkLocationPermission()

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

        //broadCast receiver for GattServices CallBAck
        registerServiceReceiver()

        //Bluetooth device scan callBack
        BLEDeviceManager.setListener(this)

        BLEConnectionManager.initBLEService(this@PairingActivity)

        //Enable Bluetooth Manually
        if (!BLEDeviceManager.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            scanDevice(true)
        }

    }

    /**
     * Register GATT update receiver
     */
    private fun registerServiceReceiver() {
        val manager = LocalBroadcastManager.getInstance(this)
        manager.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
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
                   // BLEConnectionManager.findBLEGattService(this@PairingActivity)
                }
                BLEConstants.ACTION_GATT_DEVICE_CONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_DEVICE_CONNECTED ")
                    val data = intent.getBooleanExtra("deviceConnected", false)
                    if (data) {
                        try {
                            setContentView(R.layout.device_connected)
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }

                BLEConstants.ACTION_GATT_DEVICE_DISCONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_DEVICE_DISCONNECTED ")
                    val data = intent.getBooleanExtra("deviceConnected", false)
                }
                BLEConstants.ACTION_GATT_SERVICES_DISCOVERED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED1 ")
                    BLEConnectionManager.findBLEGattService(this@PairingActivity)
                }

                BLEConstants.ACTION_DATA_AVAILABLE.equals(action) -> {
                    val data = intent.getByteArrayExtra("byteArray")
                    Log.i(TAG, "ACTION_DATA_AVAILABLE $data")

                    if (data != null) {
                        if (BLESystemConfigCommand.verifySetAcknowledgement(BLEConstants().byteArrayToArraylist(data))) {
                            BLEConnectionManager.writeToConnectedDevice(
                                BLESystemConfigCommand.read(BLEConfigurationID.enabled)
                            )
                        } else if (BLESystemConfigCommand.verifyReadAcknowledgement(
                                BLEConstants().byteArrayToArraylist(
                                    data
                                )
                            )
                        ) {
                            launchHomeScreen()
                        } else {
                            BLEConnectionManager.writeToConnectedDevice(
                                BLESystemConfigCommand.set(BLEConfigurationID.enabled, basicDescription.enable.value)
                            )
                        }
                    }
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
        intentFilter.addAction(BLEConstants.ACTION_GATT_DEVICE_DISCONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BLEConstants.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(BLEConstants.ACTION_DATA_WRITTEN)
        intentFilter.addAction(BLEConstants.ACTION_GATT_DEVICE_CONNECTED)

        return intentFilter
    }

    private fun launchHomeScreen() {
        startActivity(Intent(this, CalibrateHourHandActivity::class.java))
        finish()
    }

    /**
     * Unregister GATT update receiver
     */
    private fun unRegisterServiceReceiver() {
        try {
            val manager = LocalBroadcastManager.getInstance(this)
            manager.unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            //May get an exception while user denies the permission and user exists the app
            Log.e(TAG, e.message)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterServiceReceiver()
    }
}
