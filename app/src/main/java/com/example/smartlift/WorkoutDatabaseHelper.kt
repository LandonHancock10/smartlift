package com.example.smartlift

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WorkoutDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "workouts.db"
        const val DATABASE_VERSION = 2

        // Table: Workouts
        const val TABLE_WORKOUTS = "Workouts"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DURATION = "duration"

        // Table: API_Exercises
        const val TABLE_API_EXERCISES = "API_Exercises"
        const val COLUMN_API_ID = "id"
        const val COLUMN_API_NAME = "name"
        const val COLUMN_API_MUSCLE_GROUP = "muscle_group"
        const val COLUMN_API_DIFFICULTY = "difficulty"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Workouts table
        val createWorkoutsTable = """
            CREATE TABLE $TABLE_WORKOUTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DURATION INTEGER
            )
        """.trimIndent()

        // Create API_Exercises table
        val createAPIExercisesTable = """
            CREATE TABLE $TABLE_API_EXERCISES (
                $COLUMN_API_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_API_NAME TEXT NOT NULL,
                $COLUMN_API_MUSCLE_GROUP TEXT,
                $COLUMN_API_DIFFICULTY TEXT
            )
        """.trimIndent()

        db?.execSQL(createWorkoutsTable)
        db?.execSQL(createAPIExercisesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_API_EXERCISES")
        onCreate(db)
    }

    fun getAllWorkouts(): List<Workout> {
        val workouts = mutableListOf<Workout>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_WORKOUTS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
                workouts.add(Workout(name, description, duration))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return workouts
    }

    fun updateWorkout(newWorkout: Workout, oldWorkout: Workout): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, newWorkout.name)
            put(COLUMN_DESCRIPTION, newWorkout.description)
            put(COLUMN_DURATION, newWorkout.duration)
        }

        val result = db.update(
            TABLE_WORKOUTS,
            values,
            "$COLUMN_NAME = ? AND $COLUMN_DESCRIPTION = ? AND $COLUMN_DURATION = ?",
            arrayOf(oldWorkout.name, oldWorkout.description, oldWorkout.duration.toString())
        )

        db.close()
        return result > 0
    }

    fun deleteWorkout(workout: Workout): Boolean {
        val db = writableDatabase

        // Delete query using workout's attributes
        val result = db.delete(
            TABLE_WORKOUTS,
            "$COLUMN_NAME = ? AND $COLUMN_DESCRIPTION = ? AND $COLUMN_DURATION = ?",
            arrayOf(workout.name, workout.description, workout.duration.toString())
        )

        db.close()
        return result > 0
    }

    fun insertWorkout(workout: Workout): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, workout.name)
            put(COLUMN_DESCRIPTION, workout.description)
            put(COLUMN_DURATION, workout.duration)
        }
        val result = db.insert(TABLE_WORKOUTS, null, contentValues)
        db.close()
        return result
    }


    // Method to insert data fetched from API into the database
    fun insertAPIExercise(name: String, muscleGroup: String, difficulty: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_API_NAME, name)
            put(COLUMN_API_MUSCLE_GROUP, muscleGroup)
            put(COLUMN_API_DIFFICULTY, difficulty)
        }
        val result = db.insert(TABLE_API_EXERCISES, null, values)
        db.close()
        return result
    }

    fun clearWorkouts() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_WORKOUTS")
        db.close()
    }

    // Fetch workouts by muscle group
    fun getWorkoutsByMuscleGroup(muscleGroup: String): List<Workout> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_WORKOUTS,
            null,
            "$COLUMN_DESCRIPTION = ?", // WHERE clause
            arrayOf(muscleGroup),
            null,
            null,
            null
        )

        val workouts = mutableListOf<Workout>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
            workouts.add(Workout(name, description, duration))
        }
        cursor.close()
        db.close()
        return workouts
    }

    // Fetch all workouts sorted by name (or any other column like duration)
    fun getAllWorkoutsSortedByName(): List<Workout> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_WORKOUTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_NAME ASC" // ORDER BY clause
        )

        val workouts = mutableListOf<Workout>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
            workouts.add(Workout(name, description, duration))
        }
        cursor.close()
        db.close()
        return workouts
    }

    // Add a function to get unique muscle groups from the database
    fun getDistinctMuscleGroups(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_DESCRIPTION FROM $TABLE_WORKOUTS", null)

        val muscleGroups = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            muscleGroups.add(muscleGroup)
        }
        cursor.close()
        db.close()

        return muscleGroups
    }


    // Method to fetch API exercises from the database
    fun getAPIExercises(): List<Exercise> {
        val exercises = mutableListOf<Exercise>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_API_EXERCISES"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_API_NAME))
                val muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_API_MUSCLE_GROUP))
                val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_API_DIFFICULTY))
                exercises.add(Exercise(name, muscleGroup, difficulty))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return exercises
    }
}

// Data model for exercises fetched from the API
data class Exercise(val name: String, val muscleGroup: String, val difficulty: String)
