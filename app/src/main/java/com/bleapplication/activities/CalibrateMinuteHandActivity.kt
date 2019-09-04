package com.bleapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.bleapplication.R
import com.bleapplication.interfaces.OnGattServiceCallbackListener
import com.bleapplication.modules.BLEGattServiceConnectionManagger
import com.bleapplication.modules.BLESystemSettingCommand
import com.bleapplication.modules.BLEapplicationID
import com.bleapplication.modules.analogMovement
import org.jetbrains.anko.toast

class CalibrateMinuteHandActivity : AppCompatActivity(), OnGattServiceCallbackListener {
    override fun isDeviceConnected(isConnected: Boolean) {
        if (!isConnected)
            return
    }

    override fun onCharChangeSuccess(data: ByteArray) {
        var timeAckw = data
        if (data != null)
            onCalibrationSuccess = true
    }

    private var isSpeakButtonLongPressed = false
    private var onCalibrationSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibrate_minute_hand)

        BLEGattServiceConnectionManagger.setListener(this)
        val btn_minute_reverse = findViewById(R.id.imageButton4) as ImageButton
        val btn_minute_advance = findViewById(R.id.imageButton5) as ImageButton
        val calibration_done = findViewById(R.id.textView3) as TextView


        btn_minute_reverse.setOnLongClickListener(minuteHandListener)
        btn_minute_reverse.setOnTouchListener(minuteHandTouchListener)
        btn_minute_advance.setOnTouchListener(minuteHandTouchListener)
        btn_minute_advance.setOnLongClickListener(minuteHandListener)


        calibration_done.setOnClickListener {

            BLEGattServiceConnectionManagger.writeToConnectedDevice(
                BLESystemSettingCommand.set(
                    BLEapplicationID.analogMovement, analogMovement.exitHandsCalibrationMode
                )
            )
            startActivity(Intent(this, HomeTimeSetActivity::class.java))
            finish()
        }
    }

    private val TAG = "CalibrateMinute"
    private val minuteHandListener = object : View.OnLongClickListener {
        override fun onLongClick(pView: View): Boolean {
            pView.performClick()            // Do something when your hold starts here.
            Log.i(TAG, "onLongClick Started")
            isSpeakButtonLongPressed = true

            val viewId = pView.getTag().toString()
            when (viewId) {
                "minute_reverse" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                    BLESystemSettingCommand.set(
                        BLEapplicationID.analogMovement, analogMovement.minuteHandStartReverseContinuousl
                    )
                )
                "minute_advance" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                    BLESystemSettingCommand.set(
                        BLEapplicationID.analogMovement, analogMovement.minuteHandStartAdvanceContinuousl
                    )
                )
            }
            return true
        }
    }

    private val minuteHandTouchListener = object : View.OnTouchListener {
        override fun onTouch(pView: View, pEvent: MotionEvent): Boolean {
            pView.performClick()
            pView.onTouchEvent(pEvent)
            // We're only interested in when the button is released.
            if (pEvent.getAction() === MotionEvent.ACTION_UP) {
                // We're only interested in anything if our speak button is currently pressed.
                if (isSpeakButtonLongPressed) {
                    Log.i(TAG, "onLongClick Ended")
                    BLEGattServiceConnectionManagger.writeToConnectedDevice(
                        BLESystemSettingCommand.set(
                            BLEapplicationID.analogMovement, analogMovement.stopAllHandsMovement
                        )
                    )
                } else {
                    Log.i(TAG, "OnClick")
                    pView.performClick()

                    val viewId = pView.getTag().toString()
                    when (viewId) {
                        "minute_reverse" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                            BLESystemSettingCommand.set(
                                BLEapplicationID.analogMovement, analogMovement.minuteHandreverseOneStep
                            )
                        )
                        "minute_advance" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                            BLESystemSettingCommand.set(
                                BLEapplicationID.analogMovement, analogMovement.minuteHandAdvanceOneStep
                            )
                        )
                    }
                }
                isSpeakButtonLongPressed = false
            }
            return false
        }
    }
}
