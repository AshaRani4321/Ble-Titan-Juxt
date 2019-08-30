package com.bleapplication.modules

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class BLEGattService : Service() {

    private val TAG = "BLEGattService"
    private val mBinder = LocalBinder()                     //Binder for Activity that binds to this Service
    private var mBluetoothManager: BluetoothManager? = null //BluetoothManager used to get the BluetoothAdapter
    private var mBluetoothAdapter: BluetoothAdapter? =
        null //The BluetoothAdapter controls the BLE radio in the phone/tablet
    private var mBluetoothGatt: BluetoothGatt? = null       //BluetoothGatt controls the Bluetooth communication link
    private var mBluetoothDeviceAddress: String? = null     //Address of the connected BLE device
    private val mCompleResponseByte = ByteArray(100)

    // An activity has bound to this service
    override fun onBind(intent: Intent): IBinder? {
        //Return LocalBinder when an Activity binds to this Service
        return mBinder
    }

    // Initialize by getting the BluetoothManager and BluetoothAdapter
    fun initialize(): Boolean {
        if (mBluetoothManager == null) {                                                //See if we do not already have the BluetoothManager
            mBluetoothManager =
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager //Get the BluetoothManager

            if (mBluetoothManager == null) {                                            //See if we failed
                Log.i(TAG, "Unable to initialize BluetoothManager.")
                return false                                                           //Report the error
            }
        }
        mBluetoothAdapter =
            mBluetoothManager!!.adapter                             //Ask the BluetoothManager to get the BluetoothAdapter

        if (mBluetoothAdapter == null) {                                                //See if we failed
            Log.i(TAG, "Unable to obtain a BluetoothAdapter.")

            return false                                                               //Report the error
        }

        return true                                                                    //Success, we have a BluetoothAdapter to control the radio
    }

    // A Binder to return to an activity to let it bind to this service
    inner class LocalBinder : Binder() {
        internal fun getService(): BLEGattService {
            return this@BLEGattService//Return this instance of BluetoothLeService so clients can call its public methods
        }
    }

    fun connect(address: String?): Boolean {
        try {

            if (mBluetoothAdapter == null || address == null) {
                //Check that we have a Bluetooth adappter and device address
                Log.i(
                    TAG,
                    "BluetoothAdapter not initialized or unspecified address."
                ) //Log a warning that something went wrong
                return false
                //Failed to connect
            }

            // Previously connected device.  Try to reconnect.
            if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress && mBluetoothGatt != null) {
                //See if there was previous connection to the device
                Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.")
                //See if we can connect with the existing BluetoothGatt to connect
                //Success
                //Were not able to connect
                return mBluetoothGatt!!.connect()
            }

            val device = mBluetoothAdapter!!.getRemoteDevice(address)
                ?: //Check whether a device was returned
                return false

            //Failed to find the device
            //No previous device so get the Bluetooth device by referencing its address
            mBluetoothGatt = device.connectGatt(
                this,
                false,
                mGattCallback
            )                //Directly connect to the device so autoConnect is false
            mBluetoothDeviceAddress =
                address
            //Record the address in case we need to reconnect with the existing BluetoothGatt
            return true

        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }

        return false
    }


    //GattService callback
    private val mGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(
            gatt: BluetoothGatt, status: Int, newState: Int
        ) {
            //Change in connection state
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //See if we are connected
                Log.i(TAG, "**ACTION_SERVICE_CONNECTED**$status")
               // broadcastUpdate(BLEConstants.ACTION_GATT_CONNECTED)
                //Go broadcast an intent to say we are connected
                 gatt.discoverServices()
                //mBluetoothGatt?.discoverServices()
                //Discover services on connected BLE device
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //See if we are not connectedLog.i(TAG, "**ACTION_SERVICE_DISCONNECTED**" + status);
                broadcastUpdate(
                    BLEConstants.ACTION_GATT_DEVICE_DISCONNECTED, false
                )//Go broadcast an intent to say we are disconnected
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {//BLE service discovery complete
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //See if the service discovery was successful
                Log.i(TAG, "**ACTION_SERVICE_DISCOVERED**$status")
                broadcastUpdate(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
                //Go broadcast an intent to say we have discovered services
            } else {
                //Service discovery failed so log a warning
                Log.i(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?) {
            if (characteristic != null && characteristic.properties == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                Log.e(TAG, "**THIS IS A NOTIFY MESSAGE")
            }
            if (characteristic != null) {
                broadcastUpdate(
                    BLEConstants.ACTION_DATA_AVAILABLE,
                    characteristic
                )//Go broadcast an intent with the characteristic data
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            broadcastUpdate(
                BLEConstants.ACTION_GATT_DEVICE_CONNECTED,
                true
            )//Go broadcast an intent to say we are disconnected
        }

    }


    // Broadcast an intent with a string representing an action
    private fun broadcastUpdate(action: String) {

        val intent = Intent(action)
        val manager = LocalBroadcastManager.getInstance(applicationContext)
        manager.sendBroadcast(intent)                                             //Broadcast the intent
    }

    // Broadcast an intent with a string representing an action
    private fun broadcastUpdate(action: String, dCon: Boolean) {
        val intent = Intent(action)
        val manager = LocalBroadcastManager.getInstance(applicationContext)
        intent.putExtra("deviceConnected", dCon)
        manager.sendBroadcast(intent)                                         //Broadcast the intent
    }


    // Broadcast an intent with a string representing an action an extra string with the data
    // Modify this code for data that is not in a string format
    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        //Create new intent to broadcast the action
        val intent = Intent(action)
        val manager = LocalBroadcastManager.getInstance(applicationContext)

        val value = characteristic.value

        if (value != null) {
            val extras = Bundle()
            extras.putByteArray("byteArray", value)
            intent.putExtras(extras)
            manager.sendBroadcast(intent)
        }
    }

    // Enable notification on a characteristic
    // For information only. This application uses Indication, not Notification
    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, enabled: Boolean) {
        try {
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {                      //Check that we have a GATT connection
                Log.i(TAG, "BluetoothAdapter not initialized")

                return
            }
            mBluetoothGatt!!.setCharacteristicNotification(
                characteristic,
                enabled
            )          //Enable notification and indication for the characteristic

            for (des in characteristic.descriptors) {

                if (null != des) {
                    des.value =
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE         //Set the value of the descriptor to enable notification
                    mBluetoothGatt!!.writeDescriptor(des)

                }
            }

        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }

    // Enable indication on a characteristic
    fun setCharacteristicIndication(characteristic: BluetoothGattCharacteristic, enabled: Boolean) {
        try {
            if (mBluetoothAdapter == null || mBluetoothGatt == null) { //Check that we have a GATT connection
                Log.i(TAG, "BluetoothAdapter not initialized")
                return
            }

            mBluetoothGatt!!.setCharacteristicNotification(
                characteristic,
                enabled
            )//Enable notification and indication for the characteristic

            for (des in characteristic.descriptors) {
                if (null != des) {
                    des.value =
                        BluetoothGattDescriptor.ENABLE_INDICATION_VALUE//Set the value of the descriptor to enable indication
                    mBluetoothGatt!!.writeDescriptor(des)
                    Log.i(TAG, "***********************SET CHARACTERISTIC INDICATION SUCCESS**")//Write the descriptor
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }

    }

    // Write to a given characteristic. The completion of the write is reported asynchronously through the
    // BluetoothGattCallback onCharacteristic Wire callback method.
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic) {
        try {

            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                //Check that we have access to a Bluetooth radio

                return
            }
            val test =
                characteristic.properties
            //Get the properties of the characteristic

            if (test and BluetoothGattCharacteristic.PROPERTY_WRITE == 0 && test and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE == 0) {
                //Check that the property is writable

                return
            }
            if (mBluetoothGatt!!.writeCharacteristic(characteristic)) {                   //Request the BluetoothGatt to do the Write
                Log.i(
                    TAG,
                    "***WRITE CHARACTERISTIC SUCCESSFUL**$characteristic"
                )   //The request was accepted, this does not mean the write completed

            } else {
                Log.i(
                    TAG,
                    "writeCharacteristic failed"
                )                            //Write request was not accepted by the BluetoothGatt
            }

        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }

    }

    // Retrieve and return a list of supported GATT services on the connected device
    fun getSupportedGattServices(): List<BluetoothGattService>? {

        return if (mBluetoothGatt == null) {          //Check that we have a valid GATT connection

            null
        } else mBluetoothGatt!!.services

    }
}