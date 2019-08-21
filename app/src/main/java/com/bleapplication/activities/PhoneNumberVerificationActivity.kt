package com.bleapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.bleapplication.R
import org.jetbrains.anko.toast

class PhoneNumberVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_num_verification)

        val editText = findViewById(R.id.editText) as EditText
        val inCurrect_phone = findViewById(R.id.textView3) as TextView
        val term0 = findViewById(R.id.textView4) as TextView
        val term1 = findViewById(R.id.textView5) as TextView
        val term2 = findViewById(R.id.textView6) as TextView

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length == 10 && isValidPhone(s)) {
                    inCurrect_phone.visibility = View.GONE
                    term1.visibility = View.VISIBLE
                    term2.visibility = View.VISIBLE
                    val param = term0.layoutParams as RelativeLayout.LayoutParams
                    param.setMargins(0,98,0,0)
                    term0.layoutParams = param
                    launchOTPVerificationActivity()

                } else if(s.length >10) {
                    toast("invalid Phone number")
                    inCurrect_phone.visibility = View.VISIBLE
                    term1.visibility = View.GONE
                    term2.visibility = View.GONE

                    val param = term0.layoutParams as RelativeLayout.LayoutParams
                    param.setMargins(0,60,0,89)
                    term0.layoutParams = param
                }

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })


        // get reference to textview
//        val scrollView = findViewById(R.id.scrollView) as ScrollView
//
//        val phone_num = findViewById(R.id.phone_num) as TextView
//        phone_num!!.setOnClickListener {
//        }
    }

    fun isValidPhone(phone: CharSequence): Boolean {
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phone).matches()
        }
    }

    private fun launchOTPVerificationActivity() {

        startActivity(Intent(this, OTPVerificationActivity::class.java))
        finish()
    }

}
