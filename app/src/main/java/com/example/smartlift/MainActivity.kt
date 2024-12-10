package com.example.smartlift

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addWorkoutButton: FloatingActionButton
    private lateinit var databaseHelper: WorkoutDatabaseHelper
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database
        databaseHelper = WorkoutDatabaseHelper(this)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.workoutRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadWorkouts()

        // Add workout button
        addWorkoutButton = findViewById(R.id.addWorkoutButton)
        addWorkoutButton.setOnClickListener {
            val intent = Intent(this, AddWorkoutActivity::class.java)
            startActivity(intent)
        }

        // Clear and populate buttons
        val clearButton: Button = findViewById(R.id.clearDatabaseButton)
        val populateButton: Button = findViewById(R.id.populateDatabaseButton)

        clearButton.setOnClickListener {
            databaseHelper.clearWorkouts()
            loadWorkouts()
        }

        populateButton.setOnClickListener {
            val sampleWorkouts = listOf(
                Workout("Push Day", "Chest, shoulders, triceps", 45),
                Workout("Pull Day", "Back, biceps", 50),
                Workout("Leg Day", "Quads, hamstrings, glutes", 60),
                Workout("Cardio Day", "Treadmill, cycling", 30),
                Workout("Rest Day", "Light stretching and recovery", 15)
            )
            sampleWorkouts.forEach { databaseHelper.insertWorkout(it) }
            loadWorkouts()
        }

        // Set up muscle group filter spinner dynamically
        val muscleGroupSpinner: Spinner = findViewById(R.id.muscleGroupSpinner)
        val muscleGroups = mutableListOf("None")  // Start with "None" for no filter
        muscleGroups.addAll(databaseHelper.getDistinctMuscleGroups()) // Get unique muscle groups from the database
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, muscleGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleGroupSpinner.adapter = adapter

        // Filter workouts by muscle group dynamically using OnItemSelectedListener
        muscleGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMuscleGroup = parent?.getItemAtPosition(position).toString()
                val filteredWorkouts = if (selectedMuscleGroup == "None") {
                    databaseHelper.getAllWorkouts() // Show all workouts if "None" is selected
                } else {
                    databaseHelper.getWorkoutsByMuscleGroup(selectedMuscleGroup)
                }
                workoutAdapter.updateWorkouts(filteredWorkouts)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle when nothing is selected (could show all workouts)
                workoutAdapter.updateWorkouts(databaseHelper.getAllWorkouts())
            }
        }

        // Navigate to API Exercises
        val apiButton: Button = findViewById(R.id.apiExercisesButton)
        apiButton.setOnClickListener {
            val intent = Intent(this, ApiExerciseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadWorkouts() {
        val workouts = databaseHelper.getAllWorkouts()
        workoutAdapter = WorkoutAdapter(this, workouts.toMutableList(), databaseHelper)
        recyclerView.adapter = workoutAdapter
    }

    override fun onResume() {
        super.onResume()
        loadWorkouts() // Reload workouts when returning to this activity
    }
}
