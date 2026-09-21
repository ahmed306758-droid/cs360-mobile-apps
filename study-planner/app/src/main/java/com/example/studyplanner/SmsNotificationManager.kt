package com.example.studyplanner

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Manages SMS permission requests and SMS notifications.
 *
 * SMS functionality is optional. If the user denies permission,
 * the rest of the Study Planner application continues to work.
 */
class SmsNotificationManager(
    private val activity: Activity,
) {

    companion object {

        const val SMS_PERMISSION_REQUEST_CODE = 100

        /**
         * Determines whether SMS permission has been granted.
         */
        fun hasSmsPermission(activity: Activity): Boolean {

            return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Requests permission to send SMS messages.
     */
    fun requestSmsPermission() {

        if (!hasSmsPermission(activity)) {

            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Sends an SMS notification if permission has been granted.
     *
     * @return true when the message was sent successfully,
     *         otherwise false.
     */
    fun sendSms(
        phoneNumber: String,
        message: String
    ): Boolean {

        // Never attempt to send SMS without permission.
        if (!hasSmsPermission(activity)) {

            Toast.makeText(
                activity,
                "SMS permission was not granted.",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }

        if (phoneNumber.isBlank()) {

            Toast.makeText(
                activity,
                "No phone number is available.",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }

        return try {

            val smsManager = if (android.os.Build.VERSION.SDK_INT >= 31) {
                activity.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            smsManager?.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )

            true

        } catch (e: Exception) {

            Toast.makeText(
                activity,
                "Unable to send SMS notification.",
                Toast.LENGTH_SHORT
            ).show()

            false
        }
    }

    /**
     * Handles the result of the SMS permission request.
     */
    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray
    ) {

        if (
            requestCode ==
            SMS_PERMISSION_REQUEST_CODE
        ) {

            if (
                (grantResults.isNotEmpty() &&
                        grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)
            ) {

                Toast.makeText(
                    activity,
                    "SMS notifications enabled.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                /*
                 * Permission denial is not treated as an error.
                 * The rest of the application remains available.
                 */
                Toast.makeText(
                    activity,
                    "SMS notifications disabled. The app will continue normally.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}