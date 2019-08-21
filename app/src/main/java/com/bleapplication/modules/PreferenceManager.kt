package com.bleapplication.modules

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var IS_LAUNCHED = "IsLaunched"

    init {
        pref = context.getSharedPreferences("Intro-Slider", Context.MODE_PRIVATE)
        editor = pref!!.edit()
    }

    fun setLaunched(isFirstTime: Boolean) {
        editor!!.putBoolean(IS_LAUNCHED, isFirstTime)
        editor!!.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref!!.getBoolean(IS_LAUNCHED, true)
    }
}