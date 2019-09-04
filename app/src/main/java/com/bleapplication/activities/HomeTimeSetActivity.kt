package com.bleapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.bleapplication.R
import com.bleapplication.interfaces.OnGattServiceCallbackListener
import com.bleapplication.modules.*

class HomeTimeSetActivity : AppCompatActivity(), OnGattServiceCallbackListener {
    override fun isDeviceConnected(isConnected: Boolean) {
        if (!isConnected)
            return
    }

    override fun onCharChangeSuccess(data: ByteArray) {
        var timeack = data
        if (data != null)
            timeSetSuccess = true
    }

    private var timeSetSuccess = false
    private var setHomeTime = false
    lateinit var set_time_skip: TextView
    lateinit var set_time: TextView
    lateinit var home_time_text: TextView
    lateinit var home_time_set_text: TextView
    lateinit var home_time_info: TextView
    lateinit var home_time_success: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_time_set)

        BLEGattServiceConnectionManagger.setListener(this)

        BLEGattServiceConnectionManagger.writeToConnectedDevice(BLETimeConfigCommand.set())

        set_time_skip = findViewById(R.id.textView7) as TextView
        set_time = findViewById(R.id.textView6) as TextView
        home_time_text = findViewById(R.id.textView1) as TextView
        home_time_set_text = findViewById(R.id.textView3) as TextView
        home_time_info = findViewById(R.id.textView4) as TextView
        home_time_success = findViewById(R.id.textView5) as TextView

        set_time_skip.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        set_time.setOnClickListener {

            home_time_text.setText(R.string.home_time_text)
            home_time_set_text.setText(R.string.home_time_set_text)
            home_time_info.visibility = View.GONE
            home_time_success.setText(R.string.home_time_success)
            set_time.visibility = View.GONE
            set_time_skip.visibility = View.GONE

            Handler().postDelayed({
                setHomeTime = true
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 2000)
        }
    }

    override fun onResume() {
        super.onResume()
        if (setHomeTime) {
            val intent = intent
            finish()
            startActivity(intent)
        }
    }
}
