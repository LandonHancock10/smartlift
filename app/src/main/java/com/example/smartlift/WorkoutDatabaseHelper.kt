package com.example.smartlift

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class WorkoutDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "workout_tracker.db"
        private const val DATABASE_VERSION = 1

        // Workouts Table
        const val TABLE_WORKOUTS = "workouts"
        const val COLUMN_WORKOUT_ID = "id"
        const val COLUMN_WORKOUT_NAME = "name"
        const val COLUMN_WORKOUT_DESCRIPTION = "description"
        const val COLUMN_WORKOUT_DURATION = "duration"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create workouts table
        val createWorkoutsTable = """
            CREATE TABLE $TABLE_WORKOUTS (
                $COLUMN_WORKOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORKOUT_NAME TEXT NOT NULL,
                $COLUMN_WORKOUT_DESCRIPTION TEXT NOT NULL,
                $COLUMN_WORKOUT_DURATION TEXT NOT NULL
            )
        """
        db.execSQL(createWorkoutsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        onCreate(db)
    }

    // Insert a new workout
    fun insertWorkout(workout: Workout): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WORKOUT_NAME, workout.name)
            put(COLUMN_WORKOUT_DESCRIPTION, workout.description)
            put(COLUMN_WORKOUT_DURATION, workout.duration)
        }
        return db.insert(TABLE_WORKOUTS, null, values)
    }

    // Retrieve all workouts
    fun getAllWorkouts(): List<Workout> {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_WORKOUTS, null, null, null, null, null, null)
        val workouts = mutableListOf<Workout>()
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DESCRIPTION))
                val duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DURATION))
                workouts.add(Workout(name, description, duration))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return workouts
    }

    // Delete all workouts
    fun clearWorkouts() {
        val db = this.writableDatabase
        db.delete(TABLE_WORKOUTS, null, null)
    }
}
