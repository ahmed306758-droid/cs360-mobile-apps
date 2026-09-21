package com.example.studyplanner

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Controls SMS notification settings for the Study Planner app.
 *
 * SMS notifications are optional. If the user denies permission,
 * all other application features continue to work normally.
 */
class SMSNotificationsActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var statusIndicator: TextView
    private lateinit var responseText: TextView
    private lateinit var reminderSwitch: SwitchCompat

    private lateinit var smsManager: SmsNotificationManager
    private lateinit var sessionManager: SessionManager
    private lateinit var database: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smsnotifications)

        statusText = findViewById(R.id.smsStatusText)
        statusIndicator = findViewById(R.id.statusIndicator)
        responseText = findViewById(R.id.smsResponseText)
        reminderSwitch = findViewById(R.id.assignmentReminderSwitch)

        smsManager = SmsNotificationManager(this)
        sessionManager = SessionManager(this)
        database = DatabaseHelper(this)

        updatePermissionUI()

        val reminderPreferences = getSharedPreferences(
            "study_planner_notifications",
            MODE_PRIVATE
        )
        reminderSwitch.isChecked = reminderPreferences.getBoolean(
            "sms_reminders_enabled",
            false
        ) && SmsNotificationManager.hasSmsPermission(this)

        findViewById<Button>(
            R.id.requestSmsPermissionButton
        ).setOnClickListener {

            requestSmsPermission()
        }

        reminderSwitch.setOnCheckedChangeListener { _, checked ->

            if (checked) {

                if (!SmsNotificationManager.hasSmsPermission(this)) {

                    reminderSwitch.isChecked = false

                    requestSmsPermission()

                } else {

                    getSharedPreferences(
                        "study_planner_notifications",
                        MODE_PRIVATE
                    ).edit()
                        .putBoolean("sms_reminders_enabled", true)
                        .apply()

                    responseText.text =
                        getString(R.string.reminders_enabled_msg)
                }

            } else {

                getSharedPreferences(
                    "study_planner_notifications",
                    MODE_PRIVATE
                ).edit()
                    .putBoolean("sms_reminders_enabled", false)
                    .apply()

                responseText.text =
                    getString(R.string.reminders_paused_msg)
            }
        }

        findViewById<Button>(
            R.id.backToSettingsButton
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Requests permission to send SMS messages.
     */
    private fun requestSmsPermission() {

        if (
            SmsNotificationManager.hasSmsPermission(this)
        ) {

            showPermissionResult(
                true,
                "SMS permission is already granted."
            )

        } else {

            smsManager.requestSmsPermission()
        }
    }

    /**
     * Receives the result of the SMS permission request.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        smsManager.handlePermissionResult(
            requestCode,
            grantResults
        )

        if (
            requestCode ==
            SmsNotificationManager.SMS_PERMISSION_REQUEST_CODE
        ) {

            val granted =
                SmsNotificationManager.hasSmsPermission(this)

            if (granted) {

                getSharedPreferences(
                    "study_planner_notifications",
                    MODE_PRIVATE
                ).edit()
                    .putBoolean("sms_reminders_enabled", true)
                    .apply()

                showPermissionResult(
                    true,
                    "Permission granted. Study Planner can now send SMS assignment reminders."
                )

            } else {

                getSharedPreferences(
                    "study_planner_notifications",
                    MODE_PRIVATE
                ).edit()
                    .putBoolean("sms_reminders_enabled", false)
                    .apply()

                showPermissionResult(
                    false,
                    "Permission denied. Study Planner will continue working, but SMS reminders are unavailable."
                )
            }
        }
    }

    /**
     * Updates the screen based on the current SMS permission state.
     */
    private fun updatePermissionUI() {

        if (
            SmsNotificationManager.hasSmsPermission(this)
        ) {

            showPermissionResult(
                true,
                "SMS permission is currently enabled for this app."
            )

        } else {

            showPermissionResult(
                false,
                "SMS permission has not been granted. Tap ALLOW SMS NOTIFICATIONS to respond."
            )
        }
    }

    /**
     * Updates the permission status displayed to the user.
     */
    private fun showPermissionResult(
        granted: Boolean,
        message: String
    ) {

        statusText.text =
            if (granted) {
                "SMS Permission: Granted"
            } else {
                "SMS Permission: Not Granted"
            }

        statusIndicator.text =
            if (granted) {
                "ON"
            } else {
                "OFF"
            }

        responseText.text = message

        /*
         * Temporarily prevent the listener from requesting
         * permission again while the UI is being updated.
         */
        reminderSwitch.setOnCheckedChangeListener(null)

        reminderSwitch.isChecked = granted

        reminderSwitch.setOnCheckedChangeListener { _, checked ->

            if (checked) {

                if (
                    !SmsNotificationManager.hasSmsPermission(
                        this
                    )
                ) {

                    reminderSwitch.isChecked = false
                    requestSmsPermission()

                } else {

                    getSharedPreferences(
                        "study_planner_notifications",
                        MODE_PRIVATE
                    ).edit()
                        .putBoolean("sms_reminders_enabled", true)
                        .apply()

                    responseText.text =
                        getString(R.string.reminders_enabled_msg)
                }

            } else {

                getSharedPreferences(
                    "study_planner_notifications",
                    MODE_PRIVATE
                ).edit()
                    .putBoolean("sms_reminders_enabled", false)
                    .apply()

                responseText.text =
                    getString(R.string.reminders_paused_msg)
            }
        }
    }
}