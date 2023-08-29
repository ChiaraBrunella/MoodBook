package com.example.moodbook.ui.habits


import android.os.Build
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Observable

/**
 * Store information about a habit object
 */
class Habit(
    val habitId: String,
    val habitName: String?,
    private val startDate: Calendar,
    private val endDate: Calendar,
    private val frequency: String?,
    var progress: Int
) : Observable() {
    var maxProgress = 0
        private set

    init {
        setMaxProgress()
    }

    private fun setMaxProgress() {
        maxProgress = 0
        var daysBetween = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            daysBetween =
                ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant()).toInt()
        }
        if (frequency.equals("daily", ignoreCase = true)) {
            maxProgress = daysBetween
        } else if (frequency.equals("weekly", ignoreCase = true)) {
            maxProgress = daysBetween / 7
        } else {
            maxProgress = daysBetween / 28
        }
    }

    fun incrementProgress() {
        progress += 1
        setChanged()
        notifyObservers(this)
    }

    fun decrementProgress() {
        while (progress>0)
            progress -= 1
        setChanged()
        notifyObservers(this)
    }
}