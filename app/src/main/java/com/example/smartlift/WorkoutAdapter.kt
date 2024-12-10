package com.example.smartlift

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(
    private val context: Context,
    private var workoutList: MutableList<Workout>,
    private val dbHelper: WorkoutDatabaseHelper
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    // ViewHolder class for holding views for each item
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.workoutName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.workoutDescription)
        val durationTextView: TextView = itemView.findViewById(R.id.workoutDuration)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]

        holder.nameTextView.text = workout.name
        holder.descriptionTextView.text = workout.description
        holder.durationTextView.text = "${workout.duration} mins"

        // Handle Edit button click
        holder.editButton.setOnClickListener {
            showEditDialog(workout, position)
        }

        // Handle Delete button click
        holder.deleteButton.setOnClickListener {
            confirmDelete(workout, position)
        }
    }

    // Method to update workouts
    fun updateWorkouts(newWorkouts: List<Workout>) {
        workoutList.clear() // Clear the existing list
        workoutList.addAll(newWorkouts) // Add new data
        notifyDataSetChanged() // Notify adapter to refresh
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    // Function to show an edit dialog and update a workout
    private fun showEditDialog(workout: Workout, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_workout, null)
        val nameInput = dialogView.findViewById<TextView>(R.id.editWorkoutNameInput)
        val descriptionInput = dialogView.findViewById<TextView>(R.id.editWorkoutDescriptionInput)
        val durationInput = dialogView.findViewById<TextView>(R.id.editWorkoutDurationInput)

        nameInput.text = workout.name
        descriptionInput.text = workout.description
        durationInput.text = workout.duration.toString()

        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Workout")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedName = nameInput.text.toString()
                val updatedDescription = descriptionInput.text.toString()
                val updatedDuration = durationInput.text.toString().toIntOrNull() ?: workout.duration

                if (updatedName.isBlank()) {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val updatedWorkout = Workout(updatedName, updatedDescription, updatedDuration)
                    val result = dbHelper.updateWorkout(updatedWorkout, workout)

                    if (result) {
                        workoutList[position] = updatedWorkout
                        notifyItemChanged(position)
                        Toast.makeText(context, "Workout updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update workout", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Function to confirm delete action
    private fun confirmDelete(workout: Workout, position: Int) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete ${workout.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWorkout(workout, position)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Function to handle workout deletion
    private fun deleteWorkout(workout: Workout, position: Int) {
        val result = dbHelper.deleteWorkout(workout)
        if (result) {
            workoutList.removeAt(position) // Remove the workout from the list
            notifyItemRemoved(position) // Notify the adapter that an item was removed
            Toast.makeText(context, "Workout deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to delete workout", Toast.LENGTH_SHORT).show()
        }
    }
}
