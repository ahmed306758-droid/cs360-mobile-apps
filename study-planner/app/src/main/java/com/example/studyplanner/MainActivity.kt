package com.example.studyplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        database = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        /*
         * If a previous login session exists, the user can
         * continue directly to the Dashboard.
         */
        if (sessionManager.isLoggedIn()) {
            openDashboard()
            return
        }

        val usernameInput =
            findViewById<EditText>(R.id.usernameInput)

        val passwordInput =
            findViewById<EditText>(R.id.passwordInput)

        findViewById<Button>(
            R.id.loginButton,
        ).setOnClickListener {

            val username =
                usernameInput.text.toString().trim()

            val password =
                passwordInput.text.toString()

            if (
                username.isEmpty() ||
                password.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    R.string.login_required,
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val userId =
                database.authenticateUser(
                    username,
                    password
                )

            if (userId != -1L) {

                val user =
                    database.getUser(userId)

                if (user != null) {

                    sessionManager.createSession(
                        user.id,
                        user.username,
                        user.phone
                    )

                    Toast.makeText(
                        this,
                        R.string.login_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    openDashboard()
                }

            } else {

                Toast.makeText(
                    this,
                    R.string.login_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        findViewById<Button>(
            R.id.createAccountButton
        ).setOnClickListener {

            showCreateAccountDialog()
        }
    }

    /**
     * Displays the account creation form.
     */
    private fun showCreateAccountDialog() {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_create_account,
            null
        )

        val usernameInput =
            dialogView.findViewById<EditText>(
                R.id.newUsernameInput
            )

        val passwordInput =
            dialogView.findViewById<EditText>(
                R.id.newPasswordInput
            )

        val phoneInput =
            dialogView.findViewById<EditText>(
                R.id.phoneInput
            )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Create Account")
                .setView(dialogView)
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "CREATE",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val username =
                    usernameInput.text.toString().trim()

                val password =
                    passwordInput.text.toString()

                val phone =
                    phoneInput.text.toString().trim()

                if (
                    username.isEmpty() ||
                    password.isEmpty() ||
                    phone.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Please complete all fields.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (password.length < 4) {

                    Toast.makeText(
                        this,
                        "Password must contain at least 4 characters.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val userId =
                    database.createUser(
                        username,
                        password,
                        phone
                    )

                if (userId != -1L) {

                    sessionManager.createSession(
                        userId,
                        username,
                        phone
                    )

                    Toast.makeText(
                        this,
                        "Account created successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                    openDashboard()

                } else {

                    Toast.makeText(
                        this,
                        "That username is already in use.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

    /**
     * Opens the main application dashboard.
     */
    private fun openDashboard() {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        startActivity(intent)
        finish()
    }
}