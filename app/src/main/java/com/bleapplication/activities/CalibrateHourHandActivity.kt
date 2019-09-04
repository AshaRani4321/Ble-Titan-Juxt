package com.bleapplication.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bleapplication.R
import com.bleapplication.interfaces.OnGattServiceCallbackListener
import com.bleapplication.modules.*

class CalibrateHourHandActivity : AppCompatActivity(), OnGattServiceCallbackListener {
    override fun isDeviceConnected(isConnected: Boolean) {
        if (!isConnected)
            return
    }

    override fun onCharChangeSuccess(data: ByteArray) {
        var timeAckw = data
        if (data != null)
            isHourHandSet = true
    }

    private var isHourHandSet = false
    private var isSpeakButtonLongPressed = false

    private val TAG = "CalibrateHour"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibrate_hour_hand)

        BLEGattServiceConnectionManagger.setListener(this)


        BLEGattServiceConnectionManagger.writeToConnectedDevice(
            BLESystemSettingCommand.set(
                BLEapplicationID.analogMovement, analogMovement.startHandsCalibrationMode
            )
        )

        val btn_hour_reverse = findViewById(R.id.imageButton2) as ImageButton
        val btn_hour_advance = findViewById(R.id.imageButton3) as ImageButton

        val btn_next = findViewById(R.id.textView3) as TextView

        //btn_hour_reverse.setOnTouchListener(myViewTouch)
        btn_hour_reverse.setOnLongClickListener(hourHandListener)
        btn_hour_reverse.setOnTouchListener(hourHandTouchListener)
        btn_hour_advance.setOnLongClickListener(hourHandListener)
        btn_hour_advance.setOnTouchListener(hourHandTouchListener)


        btn_next.setOnClickListener {
            startActivity(Intent(this, CalibrateMinuteHandActivity::class.java))
            finish()
        }
        }

        private val hourHandListener = object : View.OnLongClickListener {
            override fun onLongClick(pView: View): Boolean {
                pView.performClick()            // Do something when your hold starts here.
                Log.i(TAG, "onLongClick Started")
                isSpeakButtonLongPressed = true

                val viewId = pView.getTag().toString()
                when (viewId) {
                    "hour_reverse" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                        BLESystemSettingCommand.set(
                            BLEapplicationID.analogMovement, analogMovement.hourHandStartReverseContinuously
                        )
                    )
                    "hour_advance" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                        BLESystemSettingCommand.set(
                            BLEapplicationID.analogMovement, analogMovement.hourHandStartAdvanceContinuously
                        )
                    )
                }
                return true
            }
        }

        private val hourHandTouchListener = object : View.OnTouchListener {
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
                            "hour_reverse" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                                BLESystemSettingCommand.set(
                                    BLEapplicationID.analogMovement, analogMovement.hourHandReverseOneStep
                                )
                            )
                            "hour_advance" -> BLEGattServiceConnectionManagger.writeToConnectedDevice(
                                BLESystemSettingCommand.set(
                                    BLEapplicationID.analogMovement, analogMovement.hourHandAdvanceOneStep
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
