package com.example.smartlift

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.smartlift.R
import android.widget.Button



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
        // Add these to your onCreate function
        val clearButton: Button = findViewById(R.id.clearDatabaseButton)
        val populateButton: Button = findViewById(R.id.populateDatabaseButton)

        clearButton.setOnClickListener {
            databaseHelper.clearWorkouts()
            loadWorkouts()
        }

        populateButton.setOnClickListener {
            val sampleWorkouts = listOf(
                Workout("Push Day", "Chest, shoulders, triceps", "45 mins"),
                Workout("Pull Day", "Back, biceps", "50 mins"),
                Workout("Leg Day", "Quads, hamstrings, glutes", "60 mins")
            )
            sampleWorkouts.forEach { databaseHelper.insertWorkout(it) }
            loadWorkouts()
        }
    }

    private fun loadWorkouts() {
        val workouts = databaseHelper.getAllWorkouts()
        workoutAdapter = WorkoutAdapter(workouts)
        recyclerView.adapter = workoutAdapter
    }

    override fun onResume() {
        super.onResume()
        loadWorkouts() // Reload workouts when returning to this activity
    }
}
