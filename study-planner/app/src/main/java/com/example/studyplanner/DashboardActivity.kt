package com.example.studyplanner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var assignmentGrid: LinearLayout
    private lateinit var smsManager: SmsNotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        database = DatabaseHelper(this)
        sessionManager = SessionManager(this)
        smsManager = SmsNotificationManager(this)

        assignmentGrid = findViewById(
            R.id.assignmentGrid
        )

        findViewById<Button>(
            R.id.addAssignmentButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddAssignmentActivity::class.java
                )
            )
        }

        findViewById<Button>(
            R.id.calendarButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CalendarActivity::class.java
                )
            )
        }

        findViewById<Button>(
            R.id.completedButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CompletedTasksActivity::class.java
                )
            )
        }

        findViewById<Button>(
            R.id.settingsNavButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        findViewById<Button>(
            R.id.settingsButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized) {
            loadAssignments()
            sendDueAssignmentReminderIfEnabled()
        }
    }

    /**
     * Sends one daily SMS reminder when the user has enabled reminders,
     * granted SMS permission, and has assignments due today.
     * The date is stored so returning to the dashboard does not send
     * the same reminder repeatedly on the same day.
     */
    private fun sendDueAssignmentReminderIfEnabled() {

        if (!SmsNotificationManager.hasSmsPermission(this)) {
            return
        }

        val reminderPreferences = getSharedPreferences(
            "study_planner_notifications",
            MODE_PRIVATE
        )

        if (!reminderPreferences.getBoolean("sms_reminders_enabled", false)) {
            return
        }

        val today = SimpleDateFormat(
            "MMM dd",
            Locale.US
        ).format(Date())

        val lastReminderDate = reminderPreferences.getString(
            "last_sms_reminder_date",
            ""
        ) ?: ""

        if (lastReminderDate == today) {
            return
        }

        val userId = sessionManager.getUserId()
        if (userId == -1L) {
            return
        }

        val assignments = database.getAssignmentsDueToday(userId, today)
        if (assignments.isEmpty()) {
            return
        }

        val phoneNumber = sessionManager.getPhone()
        if (phoneNumber.isBlank()) {
            return
        }

        val message = buildString {
            append(getString(R.string.sms_reminder_prefix))
            append(if (assignments.size == 1) getString(R.string.sms_reminder_single) else getString(R.string.sms_reminder_multiple))
            assignments.forEachIndexed { index, assignment ->
                if (index > 0) append("; ")
                append(assignment.course)
                append(" - ")
                append(assignment.title)
            }
        }

        if (smsManager.sendSms(phoneNumber, message)) {
            reminderPreferences.edit()
                .putString("last_sms_reminder_date", today)
                .apply()
        }
    }

    /**
     * Reads assignments belonging to the logged-in user
     * and displays them in the dashboard grid.
     */
    private fun loadAssignments() {

        assignmentGrid.removeAllViews()

        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            Toast.makeText(
                this,
                R.string.error_no_session,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val assignments =
            database.getAssignments(userId)

        assignments.forEach { assignment ->

            val row = LayoutInflater.from(this)
                .inflate(
                    R.layout.assignment_row,
                    assignmentGrid,
                    false
                )

            row.findViewById<TextView>(
                R.id.courseText
            ).text = assignment.course

            row.findViewById<TextView>(
                R.id.titleText
            ).text = assignment.title

            row.findViewById<TextView>(
                R.id.dueDateText
            ).text = assignment.dueDate

            val priority =
                row.findViewById<TextView>(
                    R.id.priorityText
                )

            priority.text = assignment.priority

            priority.setTextColor(
                when (assignment.priority.uppercase()) {

                    "HIGH" ->
                        getColor(
                            android.R.color.holo_red_light
                        )

                    "MED" ->
                        getColor(
                            android.R.color.holo_orange_light
                        )

                    else ->
                        getColor(
                            android.R.color.holo_green_light
                        )
                }
            )

            /*
             * The delete button removes the selected assignment
             * from the current user's database records.
             */
            row.findViewById<Button>(
                R.id.deleteButton
            ).setOnClickListener {

                database.deleteAssignment(
                    userId,
                    assignment.id
                )

                loadAssignments()
            }

            /*
             * Clicking an assignment row opens the same form used
             * to create an assignment, but in edit mode.
             */
            row.setOnClickListener {

                val intent = Intent(
                    this,
                    AddAssignmentActivity::class.java
                )

                intent.putExtra(
                    "assignment_id",
                    assignment.id
                )

                startActivity(intent)
            }

            assignmentGrid.addView(row)
        }

        if (assignments.isEmpty()) {

            val empty = TextView(this).apply {

                text = getString(R.string.no_assignments_yet)

                setTextColor(
                    getColor(
                        android.R.color.darker_gray
                    )
                )

                textSize = 15f

                setPadding(
                    16,
                    24,
                    16,
                    24
                )
            }

            assignmentGrid.addView(empty)
        }
    }
}