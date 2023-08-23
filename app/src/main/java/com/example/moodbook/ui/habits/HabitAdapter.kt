package com.example.moodbook.ui.habits


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.moodbook.R


/**
 * A class that controls the habits that are displayed in the list as it scrolls
 */
internal class HabitAdapter(
    private val mContext: Context,
    private val habitList: ArrayList<Habit>
) : RecyclerView.Adapter<HabitAdapter.HabitView>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_habit, parent, false)
        Log.d(TAG, "New viewholder created")
        return HabitView(view)
    }

    override fun onBindViewHolder(holder: HabitView, @SuppressLint("RecyclerView") position: Int) {
        holder.habitName.text = habitList[position].habitName
        holder.upButton.setOnClickListener {
            habitList[position].incrementProgress()
            notifyItemChanged(position)
        }
        holder.downButton.setOnClickListener {
            habitList[position].decrementProgress()
            notifyItemChanged(position)
        }
        holder.progress.max = habitList[position].maxProgress
        holder.progress.setProgress(habitList[position].progress, true)
        Log.d(TAG, "Viewholder " + habitList[position].habitName + " bound.")
    }

    override fun getItemCount(): Int {
        return habitList.size
    }

    internal inner class HabitView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView
        val progress: ProgressBar
        val upButton: ImageButton
        val downButton: ImageButton

        init {
            habitName = itemView.findViewById(R.id.habitname)
            progress = itemView.findViewById(R.id.progressBar)
            upButton = itemView.findViewById(R.id.habityes)
            downButton = itemView.findViewById(R.id.habitno)
        }
    }

    companion object {
        private const val TAG = "HabitAdapter"
    }
}