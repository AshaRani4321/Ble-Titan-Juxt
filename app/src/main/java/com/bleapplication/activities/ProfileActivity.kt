package com.bleapplication.activities

import addFragment
import android.app.PendingIntent.getActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.bleapplication.R
import com.bleapplication.fragments.NameFragment
import com.bleapplication.fragments.StepGoalFragment
import com.bleapplication.interfaces.ReplaceFragmentListener
import replaceFragment

class ProfileActivity : AppCompatActivity(), ReplaceFragmentListener {
    override fun showMainFragment(newInstance: Fragment) {
        replaceFragment(newInstance, R.id.fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (savedInstanceState == null) {
            addFragment(StepGoalFragment.newInstance(), R.id.fragment_container)
        }

        val activity_back = findViewById(R.id.back_btn) as ImageButton

        activity_back.setOnClickListener {
            val fm = getSupportFragmentManager()
           if(fm.backStackEntryCount > 1){
            fm.popBackStack()}else{
               finish()
           }

        }
    }

}
