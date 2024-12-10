package com.example.smartlift

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(private val workoutList: List<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    // ViewHolder class for holding views for each item
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.workoutName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.workoutDescription)
        val durationTextView: TextView = itemView.findViewById(R.id.workoutDuration)
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
        holder.durationTextView.text = workout.duration
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }
}
