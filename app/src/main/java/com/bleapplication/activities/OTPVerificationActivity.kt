package com.bleapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bleapplication.R
import com.chaos.view.PinView

class OTPVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        val phoneNum = intent.getStringExtra("phoneNumber")

        val otp_pin_view = findViewById(R.id.pinview) as PinView
        val otp_incorrect_msg = findViewById(R.id.textView7) as TextView
        val userPhoneNum = findViewById(R.id.editText) as EditText

        userPhoneNum.setText(phoneNum)

        otp_pin_view.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var ss = s.toString()
                if (s.length == 4 && ss.equals("1111")) {
                    otp_incorrect_msg.visibility = View.GONE
                    otp_pin_view.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme())
                    );
                    launchOTPVerificationActivity()
                } else if (s.length == 4 && !ss.equals("1111")) {
                    otp_incorrect_msg.visibility = View.VISIBLE
                    otp_pin_view.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.red, getTheme())
                    );
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun launchOTPVerificationActivity() {

        Handler().postDelayed({
            startActivity(Intent(this, PermissionAccessActivity::class.java))
            finish()
        }, 1000)



    }

}
