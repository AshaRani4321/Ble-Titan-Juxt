package com.bleapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.bleapplication.R
import com.bleapplication.helpers.ExpandableCardView

class PermissionAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_acess)
        val contact: ExpandableCardView = findViewById(R.id.contact)
        val backButton: ImageButton = findViewById(R.id.back_btn)
        val permissionAcceptButton: Button = findViewById(R.id.button1)


        backButton.visibility = View.GONE
        contact.setOnExpandedListener { view, isExpanded ->
            //  Toast.makeText(applicationContext, if(isExpanded) "Expanded!" else "Collapsed!", Toast.LENGTH_SHORT).show()
        }

        permissionAcceptButton.setOnClickListener {
            // your code to perform when the user clicks on the button
            val intent = Intent(this@PermissionAccessActivity,DeviceConnectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
