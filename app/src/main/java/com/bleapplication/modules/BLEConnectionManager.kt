package com.bleapplication.modules

import android.bluetooth.BluetoothGattCharacteristic
import android.content.*
import android.os.IBinder
import android.util.Log
import com.bleapplication.R
import com.bleapplication.activities.DeviceConnectionActivity
import com.bleapplication.interfaces.OnGattServiceCallbackListener

object BLEConnectionManager {

    private val TAG = "BLEConnectionManager"
    private var mBLEGattService: BLEGattService? = null
    private var isBind = false
    private var mDataBLE: BluetoothGattCharacteristic? = null



    private var mContext: DeviceConnectionActivity? = null
    private var mListener: OnGattServiceCallbackListener? = null
    private var mDeviceAddress: String? = null

    /**
     * Initialize Bluetooth service.
     */
    fun initBLEService(context: Context) {
        try {

            if (mBLEGattService == null) {
                val gattServiceIntent = Intent(context, BLEGattService::class.java)

                isBind = context.bindService(
                    gattServiceIntent, mServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }

        } catch (e: Exception) {
            Log.e(TAG, "initBLEService"+e.message)
        }

    }

    //Bind Service callback
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBLEGattService = (service as BLEGattService.LocalBinder).getService()

            if (!mBLEGattService?.initialize()!!) {
                Log.e(TAG, "Unable to initialize")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBLEGattService = null
        }
    }

    /**
     * Connect to a BLE Device
     */
    fun connect(deviceAddress: String): Boolean {
        var result = false

        if (mBLEGattService != null) {
            result = mBLEGattService!!.connect(deviceAddress)
        }

        return result

    }

    /**
     * findBLEGattService
     */
    fun findBLEGattService(mContext: Context) {

        if (mBLEGattService == null) {
            return
        }

        if (mBLEGattService!!.getSupportedGattServices() == null) {
            return
        }

        var uuid: String
        mDataBLE = null
        val serviceList = mBLEGattService!!.getSupportedGattServices()

        if (serviceList != null) {
            for (gattService in serviceList) {

                if (gattService.getUuid().toString().equals(
                        mContext.getString(R.string.service_uuid),
                        ignoreCase = true
                    )
                ) {
                    val gattCharacteristics = gattService.characteristics
                    for (gattCharacteristic in gattCharacteristics) {

                        uuid = if (gattCharacteristic.uuid != null) gattCharacteristic.uuid.toString() else ""

                        if (uuid.equals(
                                mContext.resources.getString(R.string.char_uuid),
                                ignoreCase = true
                            )
                        ) {
                            var newChar = gattCharacteristic
                            newChar = setProperties(newChar)
                            mDataBLE = newChar
                        }
                    }
                }

            }
        }

    }

    private fun setProperties(gattCharacteristic: BluetoothGattCharacteristic):
            BluetoothGattCharacteristic {
        val characteristicProperties = gattCharacteristic.properties

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            mBLEGattService?.setCharacteristicNotification(gattCharacteristic, true)
        }

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
            mBLEGattService?.setCharacteristicIndication(gattCharacteristic, true)
        }

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
            gattCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        }

        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
            gattCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        }
        if (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            gattCharacteristic.writeType = BluetoothGattCharacteristic.PROPERTY_READ
        }
        return gattCharacteristic
    }

    /**
     * Write BLE Characteristic.
     */
    private fun writeBLECharacteristic(characteristic: BluetoothGattCharacteristic?) {
        if (null != characteristic) {
            if (mBLEGattService != null) {
                mBLEGattService?.writeCharacteristic(characteristic)
            }
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
            if (mDataBLE != null) {
                mDataBLE!!.value = byteArray.toByteArray()
                writeBLECharacteristic(mDataBLE)
            }
            // write "byteArray" to gattClient Connected Device / Characteristic
        } else {
            for (i in 0..(numberOfCommands - 1)) {
                val startingIndex = (i * bleChunkLength)
                var endingIndex = ((i + 1) * bleChunkLength) - 1
                if (endingIndex >= byteArray.size) {
                    endingIndex = startingIndex + (byteArray.size % bleChunkLength) - 1
                }

                val commandByteArray = byteArray.subList(startingIndex, endingIndex)
                if (mDataBLE != null) {
                    mDataBLE!!.value = commandByteArray.toByteArray()
                    writeBLECharacteristic(mDataBLE)
                }
                // write "commandByteArray" to gattClient Connected Device / Characteristic

            }
        }
    }
}