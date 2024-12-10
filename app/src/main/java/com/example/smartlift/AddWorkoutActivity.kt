package com.example.smartlift

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddWorkoutActivity : AppCompatActivity() {

    private lateinit var databaseHelper: WorkoutDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_workout)

        // Initialize database helper
        databaseHelper = WorkoutDatabaseHelper(this)

        // UI elements
        val nameInput = findViewById<EditText>(R.id.workoutNameInput)
        val descriptionInput = findViewById<EditText>(R.id.workoutDescriptionInput)
        val durationInput = findViewById<EditText>(R.id.workoutDurationInput)
        val saveButton = findViewById<Button>(R.id.saveWorkoutButton)

        // Save workout button click listener
        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val description = descriptionInput.text.toString()
            val duration = durationInput.text.toString().toIntOrNull()

            if (name.isBlank() || duration == null) {
                Toast.makeText(this, "Please enter valid workout details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val workout = Workout(name, description, duration)
            val result = databaseHelper.insertWorkout(workout)

            if (result != -1L) {
                Toast.makeText(this, "Workout added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding workout", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
