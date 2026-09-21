package com.example.studyplanner

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

/**
 * Represents an assignment stored in the Study Planner database.
 */
data class Assignment(
    val id: Long,
    val userId: Long,
    val course: String,
    val title: String,
    val dueDate: String,
    val priority: String,
    val notes: String
)

/**
 * Represents a registered Study Planner user.
 */
data class User(
    val id: Long,
    val username: String,
    val phone: String
)

/**
 * Handles creation and management of the application's SQLite database.
 *
 * The database contains:
 * - users: stores account information.
 * - assignments: stores assignments belonging to individual users.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "studyplanner.db", null, 2) {

    companion object {
        private const val USERS_TABLE = "users"
        private const val ASSIGNMENTS_TABLE = "assignments"
    }

    override fun onCreate(db: SQLiteDatabase) {

        // Store login information separately from assignment information.
        db.execSQL(
            """
            CREATE TABLE $USERS_TABLE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                phone TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Every assignment belongs to the user who created it.
        db.execSQL(
            """
            CREATE TABLE $ASSIGNMENTS_TABLE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                course TEXT NOT NULL,
                title TEXT NOT NULL,
                dueDate TEXT NOT NULL,
                priority TEXT NOT NULL,
                notes TEXT DEFAULT '',
                FOREIGN KEY(user_id) REFERENCES $USERS_TABLE(id)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        /*
         * This project is still in development, so the database is
         * recreated when the database structure changes.
         */
        db.execSQL("DROP TABLE IF EXISTS $ASSIGNMENTS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $USERS_TABLE")
        onCreate(db)
    }

    /**
     * Creates a new user account.
     *
     * @return the user's ID or -1 if the username already exists.
     */
    fun createUser(
        username: String,
        password: String,
        phone: String
    ): Long {

        val values = ContentValues().apply {
            put("username", username)
            put("password", hashPassword(password))
            put("phone", phone)
        }

        return try {
            writableDatabase.insertOrThrow(
                USERS_TABLE,
                null,
                values
            )
        } catch (exception: Exception) {
            -1L
        }
    }

    /**
     * Verifies a username and password.
     *
     * @return the user's ID when authentication succeeds.
     *         Returns -1 when the credentials are incorrect.
     */
    fun authenticateUser(
        username: String,
        password: String
    ): Long {

        val cursor = readableDatabase.query(
            USERS_TABLE,
            arrayOf("id"),
            "username = ? AND password = ?",
            arrayOf(username, hashPassword(password)),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return it.getLong(
                    it.getColumnIndexOrThrow("id")
                )
            }
        }

        return -1L
    }

    /**
     * Retrieves a user's account information.
     */
    fun getUser(userId: Long): User? {

        val cursor = readableDatabase.query(
            USERS_TABLE,
            arrayOf("id", "username", "phone"),
            "id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return User(
                    id = it.getLong(
                        it.getColumnIndexOrThrow("id")
                    ),
                    username = it.getString(
                        it.getColumnIndexOrThrow("username")
                    ),
                    phone = it.getString(
                        it.getColumnIndexOrThrow("phone")
                    )
                )
            }
        }

        return null
    }

    /**
     * Adds a new assignment for a specific user.
     */
    fun addAssignment(
        userId: Long,
        course: String,
        title: String,
        dueDate: String,
        priority: String,
        notes: String
    ): Long {

        val values = ContentValues().apply {
            put("user_id", userId)
            put("course", course)
            put("title", title)
            put("dueDate", dueDate)
            put("priority", priority)
            put("notes", notes)
        }

        return writableDatabase.insert(
            ASSIGNMENTS_TABLE,
            null,
            values
        )
    }

    /**
     * Retrieves all assignments belonging to a specific user.
     */
    fun getAssignments(userId: Long): List<Assignment> {

        val assignments = mutableListOf<Assignment>()

        val cursor = readableDatabase.query(
            ASSIGNMENTS_TABLE,
            arrayOf(
                "id",
                "user_id",
                "course",
                "title",
                "dueDate",
                "priority",
                "notes"
            ),
            "user_id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "dueDate ASC, id ASC"
        )

        cursor.use {
            while (it.moveToNext()) {

                assignments.add(
                    Assignment(
                        id = it.getLong(
                            it.getColumnIndexOrThrow("id")
                        ),
                        userId = it.getLong(
                            it.getColumnIndexOrThrow("user_id")
                        ),
                        course = it.getString(
                            it.getColumnIndexOrThrow("course")
                        ),
                        title = it.getString(
                            it.getColumnIndexOrThrow("title")
                        ),
                        dueDate = it.getString(
                            it.getColumnIndexOrThrow("dueDate")
                        ),
                        priority = it.getString(
                            it.getColumnIndexOrThrow("priority")
                        ),
                        notes = it.getString(
                            it.getColumnIndexOrThrow("notes")
                        )
                    )
                )
            }
        }

        return assignments
    }

    /**
     * Retrieves one assignment belonging to the logged-in user.
     */
    fun getAssignment(
        userId: Long,
        assignmentId: Long
    ): Assignment? {

        val cursor = readableDatabase.query(
            ASSIGNMENTS_TABLE,
            arrayOf(
                "id",
                "user_id",
                "course",
                "title",
                "dueDate",
                "priority",
                "notes"
            ),
            "id = ? AND user_id = ?",
            arrayOf(
                assignmentId.toString(),
                userId.toString()
            ),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Assignment(
                    id = it.getLong(
                        it.getColumnIndexOrThrow("id")
                    ),
                    userId = it.getLong(
                        it.getColumnIndexOrThrow("user_id")
                    ),
                    course = it.getString(
                        it.getColumnIndexOrThrow("course")
                    ),
                    title = it.getString(
                        it.getColumnIndexOrThrow("title")
                    ),
                    dueDate = it.getString(
                        it.getColumnIndexOrThrow("dueDate")
                    ),
                    priority = it.getString(
                        it.getColumnIndexOrThrow("priority")
                    ),
                    notes = it.getString(
                        it.getColumnIndexOrThrow("notes")
                    )
                )
            }
        }

        return null
    }

    /**
     * Updates an existing assignment.
     */
    fun updateAssignment(
        userId: Long,
        assignmentId: Long,
        course: String,
        title: String,
        dueDate: String,
        priority: String,
        notes: String
    ): Int {

        val values = ContentValues().apply {
            put("course", course)
            put("title", title)
            put("dueDate", dueDate)
            put("priority", priority)
            put("notes", notes)
        }

        return writableDatabase.update(
            ASSIGNMENTS_TABLE,
            values,
            "id = ? AND user_id = ?",
            arrayOf(
                assignmentId.toString(),
                userId.toString()
            )
        )
    }

    /**
     * Deletes an assignment belonging to a specific user.
     */
    fun deleteAssignment(
        userId: Long,
        assignmentId: Long
    ): Int {

        return writableDatabase.delete(
            ASSIGNMENTS_TABLE,
            "id = ? AND user_id = ?",
            arrayOf(
                assignmentId.toString(),
                userId.toString()
            )
        )
    }

    /**
     * Returns assignments due on a specified date.
     * This will be used by the SMS notification feature.
     */
    fun getAssignmentsDueToday(
        userId: Long,
        today: String
    ): List<Assignment> {

        val assignments = mutableListOf<Assignment>()

        val cursor = readableDatabase.query(
            ASSIGNMENTS_TABLE,
            arrayOf(
                "id",
                "user_id",
                "course",
                "title",
                "dueDate",
                "priority",
                "notes"
            ),
            "user_id = ? AND dueDate = ?",
            arrayOf(
                userId.toString(),
                today
            ),
            null,
            null,
            "id ASC"
        )

        cursor.use {
            while (it.moveToNext()) {

                assignments.add(
                    Assignment(
                        id = it.getLong(
                            it.getColumnIndexOrThrow("id")
                        ),
                        userId = it.getLong(
                            it.getColumnIndexOrThrow("user_id")
                        ),
                        course = it.getString(
                            it.getColumnIndexOrThrow("course")
                        ),
                        title = it.getString(
                            it.getColumnIndexOrThrow("title")
                        ),
                        dueDate = it.getString(
                            it.getColumnIndexOrThrow("dueDate")
                        ),
                        priority = it.getString(
                            it.getColumnIndexOrThrow("priority")
                        ),
                        notes = it.getString(
                            it.getColumnIndexOrThrow("notes")
                        )
                    )
                )
            }
        }

        return assignments
    }

    /**
     * Hashes passwords before storing them in SQLite.
     */
    private fun hashPassword(password: String): String {

        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(
            password.toByteArray()
        )

        return hash.joinToString("") {
            "%02x".format(it)
        }
    }
}