package com.example.studyplanner

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CompletedTasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_completed_task)

        // Return to Dashboard
        findViewById<Button>(R.id.backToDashboardButton)
            .setOnClickListener {
                finish()
            }
    }
}