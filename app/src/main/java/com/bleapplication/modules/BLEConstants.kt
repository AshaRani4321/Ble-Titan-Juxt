package com.bleapplication.modules

import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList


enum class BLECommands(val value : Byte) {
    SEQBit (0x80.toByte()),
    ReadStatus (0x01),
    SystemEvent (0x02),
    TimeConfig (0x03),
    SetAppConfig (0x04),
    ReadAppConfig (0x05),
    SetWorldClock (0x06),
    ReadWorldClock (0x07),
    ReadBattery (0x0E),
    SetSystemConfig (0x0F),
    ReadSystemConfig (0x10),
    SetGoal (0x12),
    ReadGoal (0x13),
    StepsOnOtherWatches (0x30),
    SetUserProfile (0x31),
    ReadUserProfile (0x32),
    ReadActivity (0x14),
    SetCategoryPriority (0x1C),
    ReadCategoryPriority (0x1D),
    SetAppPriority (0x1E),
    ReadAppPriority (0x1F),
    UpdateAppPriority (0x20),
    SetHapticPatterns (0x3B),
    ReadHapticPatterns (0x3C),
    SetNotificationFilter (0xA),
    UpdateNotificationFilter (0xB),
    ReadNotificationFilter (0x17),
    UpdateContactsApplications (0x1B),
    ReadContactsrFilter (0x19),
    SetContactsFilter (0x18),
    UpdateContactsFilter (0x1A),
    SetWeatherLocations (0x24),
    ReadWeatherLocations (0x25),
    UpdateWeatherInfo (0x26),
    StartSystemSetting (0x34),
    FindMyPhone (0x36),
    SetDailyAlarm (0x37),
    ReadDailyAlarm (0x38),
    SetCountdownTimer (0x39),
    ReadCountdownTimer (0x3A),
    UrbanNavigation (0x3D),
    TriggerBehavior (0x21),
    ClearConnection (0x23),
    SwitchToDFUMode (0x70),
    ReadSystemAttribute (0x15),
}


class BLEConstants {

    companion object {
        const val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED" //Strings representing actions to broadcast to activities
        const val ACTION_GATT_DEVICE_DISCONNECTED = "ACTION_GATT_DISCONNECTED" // Old com.zco.ble
        const val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        const val ACTION_DATA_WRITTEN = "ACTION_DATA_WRITTEN"
        const val EXTRA_DATA = "EXTRA_DATA"
        const val EXTRA_UUID = "EXTRA_UUID"
        const val ACTION_GATT_DEVICE_CONNECTED = "ACTION_GATT_DEVICE_CONNECTED"

        const val SCAN_PERIOD: Long = 10000
    }

    fun byteArrayToArraylist(byteArray : ByteArray) : ArrayList<Byte>{
        val list = ArrayList<Byte>()
        for (b in byteArray) {
            list.add(b)
        }
        return list
    }


    fun intToBytes(i: Int): ByteArray {
        val bb = ByteBuffer.allocate(4)
        bb.putInt(i)
        return bb.array()
    }

    fun convertBytesToInt(byteArray: ByteArray): Int {
        return ByteBuffer.wrap(byteArray).int
    }

    fun shortToBytes(i: Short): ByteArray {
        val bb = ByteBuffer.allocate(2)
        bb.putShort(i)
        return bb.array()
    }

    fun convertBytesToShort(byteArray: ByteArray): Short {
        return ByteBuffer.wrap(byteArray).short
    }
/*
    public byte[] setCurrentTime() {
        int unixTime = 0;
        short xx = 10000;
        byte xxxx = 127;
        byte[] byteArray = intToBytes(unixTime);
        byte[] returnValue = {(byte) 0x80, 0x03, ,0x16};
        return  returnValue;
    } */


    fun getTimeZoneOffset(): Int {
        val mCalendar = GregorianCalendar()
        val mTimeZone = mCalendar.timeZone
        return mTimeZone.rawOffset / (1000 * 60 * 15)
    }

    fun getUnixTimeStamp(): Int {
        val unixTime = System.currentTimeMillis() / 1000
        return unixTime.toInt()
    }

    fun verifyDeviceConnectionAck(data: ArrayList<Byte>) : Boolean {
        if (data[0] == BLECommands.SEQBit.value && data[1] == BLECommands.SystemEvent.value && data[2] == BLECommands.ReadAppConfig.value ) {
            return true
        }

        return false
    }
}