package com.example.smartlift

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApiExerciseAdapter(
    private var exercises: MutableList<Exercise>,
    private val onAddToWorkouts: (Exercise) -> Unit
) : RecyclerView.Adapter<ApiExerciseAdapter.ExerciseViewHolder>() {

    // ViewHolder for each exercise item
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.exerciseName)
        val muscleGroupTextView: TextView = itemView.findViewById(R.id.exerciseMuscleGroup)
        val difficultyTextView: TextView = itemView.findViewById(R.id.exerciseDifficulty)
        val addToWorkoutsButton: Button = itemView.findViewById(R.id.addToWorkoutsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_api_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.nameTextView.text = exercise.name
        holder.muscleGroupTextView.text = exercise.muscleGroup
        holder.difficultyTextView.text = exercise.difficulty

        // Handle "Add to Workouts" button click
        holder.addToWorkoutsButton.setOnClickListener {
            onAddToWorkouts(exercise)
        }
    }

    override fun getItemCount(): Int = exercises.size

    // Update the list of exercises
    fun updateExercises(newExercises: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newExercises)
        notifyDataSetChanged()
    }
}
