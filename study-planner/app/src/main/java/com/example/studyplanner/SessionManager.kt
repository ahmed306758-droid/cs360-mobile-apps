package com.example.studyplanner

import android.content.Context

/**
 * Stores information about the currently authenticated user.
 *
 * SharedPreferences allows the application to remember the
 * logged-in user while the app is running and after activities
 * are recreated.
 */
class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "study_planner_session",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val USER_ID = "user_id"
        private const val USERNAME = "username"
        private const val PHONE = "phone"
        private const val LOGGED_IN = "logged_in"
    }

    fun createSession(
        userId: Long,
        username: String,
        phone: String
    ) {
        preferences.edit()
            .putLong(USER_ID, userId)
            .putString(USERNAME, username)
            .putString(PHONE, phone)
            .putBoolean(LOGGED_IN, true)
            .apply()
    }

    fun getUserId(): Long {
        return preferences.getLong(USER_ID, -1L)
    }

    fun getUsername(): String {
        return preferences.getString(USERNAME, "") ?: ""
    }

    fun getPhone(): String {
        return preferences.getString(PHONE, "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(LOGGED_IN, false)
    }

    fun clearSession() {
        preferences.edit().clear().apply()
    }
}