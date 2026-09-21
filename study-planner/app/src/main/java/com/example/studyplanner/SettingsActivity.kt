package com.example.studyplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Open SMS Notifications
        findViewById<Button>(R.id.manageSmsPermissionButton)
            .setOnClickListener {
                val intent = Intent(this, SMSNotificationsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

        // Return to Dashboard
        findViewById<Button>(R.id.backToDashboardButton)
            .setOnClickListener {
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
    }
}