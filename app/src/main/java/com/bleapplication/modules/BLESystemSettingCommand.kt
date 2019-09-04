package com.bleapplication.modules

enum class BLEapplicationID(val value: Byte) {
    analogMovement(0x01),
    compass(0x02)
}

enum class analogMovement(val value: Byte) {
    exitHandsCalibrationMode(0x00),
    startHandsCalibrationMode(0x01),
    stopAllHandsMovement(0x10),
    minuteHandAdvanceOneStep (0x15),
    minuteHandreverseOneStep(0x16),
    minuteHandStartAdvanceContinuousl(0x17),
    minuteHandStartReverseContinuousl(0x18),
    hourHandAdvanceOneStep(0x19),
    hourHandReverseOneStep(0x1A),
    hourHandStartAdvanceContinuously(0x1B),
    hourHandStartReverseContinuously(0x1C),

}

object BLESystemSettingCommand {
    init {

    }

    fun set(
        applicationId: BLEapplicationID, startHandsCalibrationMode: analogMovement): ArrayList<Byte> {
        val returnValue: ArrayList<Byte> = ArrayList()
        returnValue.add(BLECommands.StartSystemSetting.value)
        returnValue.add(applicationId.value)
        returnValue.add(startHandsCalibrationMode.value)

        return returnValue
    }
}