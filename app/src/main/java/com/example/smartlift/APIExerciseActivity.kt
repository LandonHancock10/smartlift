package com.example.smartlift

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApiExerciseActivity : AppCompatActivity() {

    private lateinit var exerciseAdapter: ApiExerciseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: WorkoutDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_exercise)

        databaseHelper = WorkoutDatabaseHelper(this)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.apiExerciseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        exerciseAdapter = ApiExerciseAdapter(mutableListOf()) { exercise ->
            addExerciseToDatabase(exercise)
        }
        recyclerView.adapter = exerciseAdapter

        // Fetch exercises from the API
        fetchExercises()
    }

    private fun fetchExercises() {
        APIManager(this).fetchExercises(
            onSuccess = { exercises ->
                exerciseAdapter.updateExercises(exercises)
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun addExerciseToDatabase(exercise: Exercise) {
        databaseHelper.insertWorkout(
            Workout(
                name = exercise.name,
                description = exercise.muscleGroup,
                duration = 0 // Default to 0 minutes
            )
        )
        Toast.makeText(this, "${exercise.name} added to workouts!", Toast.LENGTH_SHORT).show()
    }
}
