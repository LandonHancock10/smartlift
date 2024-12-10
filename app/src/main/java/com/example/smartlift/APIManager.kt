package com.example.smartlift

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class APIManager(context: Context) {

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        const val BASE_URL = "https://exercisedb.p.rapidapi.com/exercises"
        const val API_KEY = "c3abd52927mshd68fadcde418549p1d3c27jsn0e6607a831da"
    }

    fun fetchExercises(onSuccess: (List<Exercise>) -> Unit, onError: (String) -> Unit) {
        val url = BASE_URL // Fetch all exercises

        val request = object : JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    Log.d("API Response", response.toString())
                    val exercises = parseExercises(response)
                    onSuccess(exercises)
                } catch (e: Exception) {
                    Log.e("API Parsing Error", e.toString())
                    onError("Error parsing response: ${e.message}")
                }
            },
            { error ->
                Log.e("API Error", error.toString())
                onError("Error fetching data: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-RapidAPI-Key"] = API_KEY
                headers["X-RapidAPI-Host"] = "exercisedb.p.rapidapi.com"
                return headers
            }
        }

        requestQueue.add(request)
    }

    private fun parseExercises(jsonArray: JSONArray): List<Exercise> {
        val exercises = mutableListOf<Exercise>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val muscleGroup = jsonObject.optString("target", "Unknown")
            val difficulty = "Unknown" // Placeholder if difficulty is not available
            exercises.add(Exercise(name, muscleGroup, difficulty))
        }
        return exercises
    }
}
