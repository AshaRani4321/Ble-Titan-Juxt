package com.bleapplication.modules

object BLETimeConfigCommand {

    init {
    }

    fun set(): ArrayList<Byte> {
        val byteArray = BLEConstants().intToBytes(BLEConstants().getUnixTimeStamp())
        val mGMTOffset = BLEConstants().getTimeZoneOffset()

        val returnValue: ArrayList<Byte> = ArrayList()
        returnValue.add(BLECommands.TimeConfig.value)
        returnValue.add(byteArray[3])
        returnValue.add(byteArray[2])
        returnValue.add(byteArray[1])
        returnValue.add(byteArray[0])
        returnValue.add(mGMTOffset.toByte())
        return returnValue
    }

}