package com.bleapplication.modules

import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.bleapplication.interfaces.OnDeviceScanListener
import com.bleapplication.interfaces.OnGattServiceCallbackListener
import java.util.*
import androidx.core.content.ContextCompat.getSystemService as getSystemService1

object BLEGattServiceConnectionManagger {
    private val TAG = "BLEGattService"
    private var mListener: OnGattServiceCallbackListener? = null
    private var mDeviceAddress: String? = null
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mDiscoveredCharacteristic: BluetoothGattCharacteristic? = null


    fun connectToGatt(context: Context, deviceAddress: String) {

      //  mListener = onGattServiceCallbackListener
        mDeviceAddress = deviceAddress

        mBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (mBluetoothManager != null)
            mBluetoothAdapter = mBluetoothManager!!.adapter

        if (!checkBluetoothSupport(context, mBluetoothAdapter)) {
            throw RuntimeException("GATT client requires Bluetooth support")
        }

        // Register for system Bluetooth events
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(mBluetoothReceiver, filter)
        if (!mBluetoothAdapter!!.isEnabled()) {
            Log.w(TAG, "Bluetooth is currently disabled... enabling")
            mBluetoothAdapter!!.enable()
        } else {
            Log.i(TAG, "Bluetooth enabled... starting client")
            startClient(context)
        }
    }

    private val mBluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)

            when (state) {
                BluetoothAdapter.STATE_ON -> startClient(context)
            }
        }
    }

    private fun startClient(context: Context) {
        val bluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(mDeviceAddress)
        mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mGattCallback)

        if (mBluetoothGatt == null) {
            Log.w(TAG, "Unable to create GATT client")
            return
        }
    }


    //GattService callback
    private val mGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt!!.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mListener!!.isDeviceConnected(false)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                var connected = false

                val service = gatt!!.getService(UUID.fromString("F0BA3120-6CAC-4C99-9089-4B0A1DF45002"))
                if (service != null) {
                    val characteristic =
                        service.getCharacteristic(UUID.fromString("F0BA3121-6CAC-4C99-9089-4B0A1DF45002"))
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true)
                        val descriptor =
                            characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                        if (descriptor != null) {
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            connected = gatt.writeDescriptor(descriptor)
                        }
                    }
                }
                mListener!!.isDeviceConnected(connected)
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {

            val data = characteristic!!.getValue()
            // An Async task always executes in new thread
            Thread(Runnable {
                // perform any operation
                println("Performing operation in Asynchronous Task")

                // check if listener is registered.
                if (mListener != null) {

                    // invoke the callback method of class A
                    mListener!!.onCharChangeSuccess(data)
                }
            }).start()
        }
    }

    fun writeToConnectedDevice(data: ArrayList<Byte>) {

        var byteArray = data
        val bleChunkLength = 20
        var numberOfCommands: Int = byteArray.size / (bleChunkLength - 1)
        if (byteArray.size % (bleChunkLength - 1) > 0 ) {
            numberOfCommands += 1
        }

        for (i in 0..(numberOfCommands - 1)) {
            byteArray.add((i * bleChunkLength), i.toByte())
            if (i == numberOfCommands - 1) {
                byteArray[i * bleChunkLength] = (byteArray[i * bleChunkLength].toInt() or BLECommands.SEQBit.value.toInt()).toByte()
            }
        }

        if (byteArray.size <= bleChunkLength) {

            mDiscoveredCharacteristic = mBluetoothGatt!!
                .getService(UUID.fromString("F0BA3120-6CAC-4C99-9089-4B0A1DF45002"))
                .getCharacteristic(UUID.fromString("F0BA3121-6CAC-4C99-9089-4B0A1DF45002"))
            mDiscoveredCharacteristic!!.setValue(ArrayList(byteArray).toByteArray() )
            mBluetoothGatt!!.writeCharacteristic(mDiscoveredCharacteristic)
          //  mBluetoothGatt!!.executeReliableWrite()
            // write "byteArray" to gattClient Connected Device / Characteristic
        } else {
            for (i in 0..(numberOfCommands - 1)) {
                val startingIndex = (i * bleChunkLength)
                var endingIndex = ((i + 1) * bleChunkLength) - 1
                if (endingIndex >= byteArray.size) {
                    endingIndex = startingIndex + (byteArray.size % bleChunkLength) - 1
                }

                val commandByteArray = byteArray.subList(startingIndex, endingIndex)

                // write "commandByteArray" to gattClient Connected Device / Characteristic

            }
        }
    }

    private fun stopClient() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt = null
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null
        }
    }

    private fun checkBluetoothSupport(context: Context, bluetoothAdapter: BluetoothAdapter?): Boolean {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported")
            return false
        }

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported")
            return false
        }
        return true
    }

    /**
     * setListener
     */
    fun setListener(onGattServiceCallbackListener: OnGattServiceCallbackListener) {
        mListener = onGattServiceCallbackListener
    }

    fun unregisterReceiver(context: Context) {
        mListener= null
        context.unregisterReceiver(mBluetoothReceiver)

    }

}