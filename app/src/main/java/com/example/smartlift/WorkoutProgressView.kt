package com.example.smartlift

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WorkoutProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Int = 0 // Represents the workout progress (in percentage)
    private val paint: Paint = Paint()

    init {
        paint.color = Color.BLUE
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    // This function sets the workout progress (from 0 to 100)
    fun setProgress(progress: Int) {
        if (progress in 0..100) {
            this.progress = progress
            invalidate() // Redraw the view when progress changes
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Draw the background (gray color)
        paint.color = Color.LTGRAY
        canvas.drawRect(0f, 0f, width, height, paint)

        // Draw the progress (blue color)
        paint.color = Color.BLUE
        val progressWidth = (width * progress / 100).toFloat()
        canvas.drawRect(0f, 0f, progressWidth, height, paint)
    }
}
