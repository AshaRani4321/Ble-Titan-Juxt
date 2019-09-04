package com.bleapplication.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.bleapplication.R
import com.bleapplication.interfaces.OnDeviceScanListener
import com.bleapplication.interfaces.OnGattServiceCallbackListener
import com.bleapplication.modules.*
import org.jetbrains.anko.toast

class DeviceConnectionActivity : AppCompatActivity(), OnDeviceScanListener, OnGattServiceCallbackListener {
    override fun isDeviceConnected(isConnected: Boolean) {
        if (isConnected)
            changeActivityViewLayout()
    }

    override fun onCharChangeSuccess(data: ByteArray) {

        if (data != null) {
            if (BLESystemConfigCommand.verifySetAcknowledgement(BLEConstants().byteArrayToArraylist(data))) {
                BLEGattServiceConnectionManagger.writeToConnectedDevice(
                    BLESystemConfigCommand.read(BLEConfigurationID.enabled)
                )
            } else if (BLESystemConfigCommand.verifyReadAcknowledgement(
                    BLEConstants().byteArrayToArraylist(data)
                )
            ) {
                launchHomeScreen()
            } else {
                BLEGattServiceConnectionManagger.writeToConnectedDevice(
                    BLESystemConfigCommand.set(BLEConfigurationID.enabled, basicDescription.enable.value)
                )
            }
        }
    }

    private fun changeActivityViewLayout() {
        runOnUiThread {
            setContentView(R.layout.device_connected)
        }
    }

    private fun launchHomeScreen() {

        runOnUiThread {
            Handler().postDelayed({
                startActivity(Intent(this, CalibrateHourHandActivity::class.java))
                finish()
            }, 2000)        }
    }

    private val REQUEST_ENABLE_BT = 1000
    private var mDeviceAddress: String = ""
    private var onGattServiceCallbackListener: OnGattServiceCallbackListener? = null

    override fun onScanCompleted(deviceDataList: BleDeviceData) {
        mDeviceAddress = deviceDataList.mDeviceAddress

        BLEGattServiceConnectionManagger.setListener(this)
        BLEGattServiceConnectionManagger.connectToGatt(this, mDeviceAddress)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_searching)
        prepareForScan()
    }

    private fun prepareForScan() {
        if (BLEDeviceConnectionManager.init(this)) {

            //Bluetooth device scan callBack
            BLEDeviceConnectionManager.setListener(this)

            //Enable Bluetooth Manually
            if (!BLEDeviceConnectionManager.isEnabled()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                if (isLocationPermissionEnabled()) {
                    scanBLEDevice()
                } else {
                    toast("Location permission not given")
                }
            }
        } else {
            toast("BLE NOT SUPPORTED")
            return
        }
    }

    private fun scanBLEDevice() {
        BLEDeviceConnectionManager.scanBLEDevice()
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

    override fun onDestroy() {
        super.onDestroy()
        BLEGattServiceConnectionManagger.unregisterReceiver(this)
    }
}
