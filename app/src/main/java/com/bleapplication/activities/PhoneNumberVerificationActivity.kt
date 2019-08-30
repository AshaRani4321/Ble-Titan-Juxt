package com.bleapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
    var phoneNum: String = ""
    var delay: Long = 1000 // 1 seconds after user stops typing
    var last_text_edit: Long = 0
    var handler = Handler()

    lateinit var inCurrect_phone: TextView
    lateinit var term0: TextView
    lateinit var term1: TextView
    lateinit var term2: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_num_verification)

        val editText = findViewById(R.id.editText) as EditText
        inCurrect_phone = findViewById(R.id.textView3) as TextView
        term0 = findViewById(R.id.textView4) as TextView
        term1 = findViewById(R.id.textView5) as TextView
        term2 = findViewById(R.id.textView6) as TextView

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (s.length > 0) {
                    phoneNum = s.toString()
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                } else {

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
            ) { //You need to remove this to run only once
                handler.removeCallbacks(input_finish_checker);
            }
        })


        // get reference to textview
//        val scrollView = findViewById(R.id.scrollView) as ScrollView
//
//        val phone_num = findViewById(R.id.phone_num) as TextView
//        phone_num!!.setOnClickListener {
//        }
    }


    private val input_finish_checker = Runnable {
        if (System.currentTimeMillis() > last_text_edit + delay - 500) {
            // TODO: do what you need here
            // ............
            // ............
            if (phoneNum.length == 10 && isValidPhone(phoneNum)) {

                inCurrect_phone.visibility = View.GONE
                term1.visibility = View.VISIBLE
                term2.visibility = View.VISIBLE
                val param = term0.layoutParams as RelativeLayout.LayoutParams
                param.setMargins(0, 98, 0, 0)
                term0.layoutParams = param
                launchOTPVerificationActivity()

            } else if (phoneNum.length > 10 || phoneNum.length < 10) {
                toast("invalid Phone number")
                inCurrect_phone.visibility = View.VISIBLE
                term1.visibility = View.GONE
                term2.visibility = View.GONE

                val param = term0.layoutParams as RelativeLayout.LayoutParams
                param.setMargins(0, 60, 0, 89)
                term0.layoutParams = param
            }
        }
    }

    fun isValidPhone(phone: CharSequence): Boolean {
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phone).matches()
        }
    }

    private fun launchOTPVerificationActivity() {


        Handler().postDelayed({
            val intent = Intent(this@PhoneNumberVerificationActivity, OTPVerificationActivity::class.java)
            intent.putExtra("phoneNumber", phoneNum)
            startActivity(intent)
            finish()
        }, 1000)


    }

}
