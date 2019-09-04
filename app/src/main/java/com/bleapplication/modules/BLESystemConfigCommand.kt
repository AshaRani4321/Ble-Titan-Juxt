package com.bleapplication.modules

enum class BLEConfigurationID(val value: Byte) {
    airplaneMode(0x02),
    enabled(0x04),
    clockFormat(0x08),
    topKeyCustomization(0x11),
    analogHandsConfig(0x12),
    systemUnit(0x14),
    tempUnit(0x16),
    systemLanguage(0x17)

}

enum class basicDescription(val value: Byte) {
    disable(0x00),
    enable(0x01)
}

enum class topKeyDescription(val value: Byte) {
    defaultControl(0x00),
    remoteCamera(0x01),
    findMyPhone(0x02),
    musicControl(0x03)
}

enum class languageDescription(val value: Byte) {
    english(0x00),
    tc(0x01),
    sc(0x02)
}

enum class clockFormatDescription(val value: Byte) {
    format12h(0x00),
    format24h(0x01)
}

enum class analogHandsConfigDescription(val value: Byte) {
    currentTime(0x00),
    worldClock(0x01)

}

enum class SystemUnitEnglishDescription(val value: Byte) {
    kilometer(0x00),
    mile(0x01)

}

enum class tempUnitDescription(val value: Byte) {
    celsius(0x00),
    fahrenheit(0x01)

}

object BLESystemConfigCommand {

    init {
    }

    fun set(ConfigurationID: BLEConfigurationID, enable: Byte): ArrayList<Byte> {
        val returnValue: ArrayList<Byte> = ArrayList()
        returnValue.add(BLECommands.SetSystemConfig.value)
        returnValue.add(ConfigurationID.value)
        returnValue.add(0x01)
        returnValue.add(enable)
        return returnValue
    }

    fun read(ConfigurationID: BLEConfigurationID): ArrayList<Byte> {
        val returnValue: ArrayList<Byte> = ArrayList()
        returnValue.add(BLECommands.ReadSystemConfig.value)
        returnValue.add(ConfigurationID.value)
        return returnValue
    }

    fun verifySetAcknowledgement(data: ArrayList<Byte>) : Boolean {
        if (data[0] == BLECommands.SEQBit.value && data[1] == BLECommands.SetSystemConfig.value) {
            return true
        }

        return false
    }

    fun verifyReadAcknowledgement(data: ArrayList<Byte>) : Boolean {
        if (data[0] == BLECommands.SEQBit.value && data[1] == BLECommands.ReadSystemConfig.value && data[4] == BLECommands.ReadStatus.value ) {
            return true
        }

        return false
    }

}