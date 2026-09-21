package com.example.studyplanner

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAssignmentActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private var selectedDate = ""

    private var editingAssignmentId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_assignment)

        database = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        /*
         * The assignment ID is supplied when the user is editing
         * an existing assignment. A value of -1 means this is a
         * new assignment.
         */
        editingAssignmentId = intent.getLongExtra(
            "assignment_id",
            -1L
        )

        val prioritySpinner =
            findViewById<Spinner>(R.id.prioritySpinner)

        prioritySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("HIGH", "MED", "LOW")
        )

        /*
         * If an existing assignment was selected, load its current
         * information into the form so the user can edit it.
         */
        if (editingAssignmentId != -1L) {
            loadAssignmentForEditing(prioritySpinner)
        }

        findViewById<Button>(R.id.dueDateButton).setOnClickListener {

            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    calendar.set(
                        year,
                        month,
                        day
                    )

                    selectedDate =
                        SimpleDateFormat(
                            "MMM dd",
                            Locale.US
                        ).format(calendar.time)

                    findViewById<Button>(
                        R.id.dueDateButton
                    ).text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        findViewById<Button>(
            R.id.saveAssignmentButton
        ).setOnClickListener {

            saveAssignment(prioritySpinner)
        }

        /*
         * The existing button on this screen is used as a
         * clear-form button rather than deleting a database item.
         */
        findViewById<Button>(
            R.id.deleteAssignmentButton
        ).setOnClickListener {

            clearForm(prioritySpinner)
        }

        findViewById<Button>(
            R.id.backButton
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Loads an existing assignment into the edit form.
     */
    private fun loadAssignmentForEditing(
        prioritySpinner: Spinner
    ) {

        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            finish()
            return
        }

        val assignment = database.getAssignment(
            userId,
            editingAssignmentId
        )

        if (assignment == null) {
            Toast.makeText(
                this,
                R.string.error_assignment_not_found,
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        findViewById<EditText>(
            R.id.courseInput
        ).setText(assignment.course)

        findViewById<EditText>(
            R.id.assignmentTitleInput
        ).setText(assignment.title)

        findViewById<EditText>(
            R.id.notesInput
        ).setText(assignment.notes)

        selectedDate = assignment.dueDate

        findViewById<Button>(
            R.id.dueDateButton
        ).text = assignment.dueDate

        val priorityPosition =
            (prioritySpinner.adapter as ArrayAdapter<String>).getPosition(
                assignment.priority
            )

        if (priorityPosition >= 0) {
            prioritySpinner.setSelection(priorityPosition)
        }

        findViewById<Button>(
            R.id.saveAssignmentButton
        ).text = getString(R.string.update_assignment)
    }

    /**
     * Creates a new assignment or updates an existing one.
     */
    private fun saveAssignment(
        prioritySpinner: Spinner
    ) {

        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            Toast.makeText(
                this,
                R.string.error_login_required_assignment,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val course = findViewById<EditText>(
            R.id.courseInput
        ).text.toString().trim()

        val title = findViewById<EditText>(
            R.id.assignmentTitleInput
        ).text.toString().trim()

        val notes = findViewById<EditText>(
            R.id.notesInput
        ).text.toString().trim()

        val priority =
            prioritySpinner.selectedItem.toString()

        if (
            course.isEmpty() ||
            title.isEmpty() ||
            selectedDate.isEmpty()
        ) {

            Toast.makeText(
                this,
                R.string.error_fields_required,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (editingAssignmentId == -1L) {

            database.addAssignment(
                userId = userId,
                course = course,
                title = title,
                dueDate = selectedDate,
                priority = priority,
                notes = notes
            )

            Toast.makeText(
                this,
                R.string.msg_assignment_added,
                Toast.LENGTH_SHORT
            ).show()

        } else {

            database.updateAssignment(
                userId = userId,
                assignmentId = editingAssignmentId,
                course = course,
                title = title,
                dueDate = selectedDate,
                priority = priority,
                notes = notes
            )

            Toast.makeText(
                this,
                R.string.msg_assignment_updated,
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }

    /**
     * Clears all fields in the assignment form.
     */
    private fun clearForm(
        prioritySpinner: Spinner
    ) {

        findViewById<EditText>(
            R.id.courseInput
        ).text.clear()

        findViewById<EditText>(
            R.id.assignmentTitleInput
        ).text.clear()

        findViewById<EditText>(
            R.id.notesInput
        ).text.clear()

        selectedDate = ""

        findViewById<Button>(
            R.id.dueDateButton
        ).text = getString(R.string.select_due_date)

        prioritySpinner.setSelection(0)

        Toast.makeText(
            this,
            R.string.msg_form_cleared,
            Toast.LENGTH_SHORT
        ).show()
    }
}