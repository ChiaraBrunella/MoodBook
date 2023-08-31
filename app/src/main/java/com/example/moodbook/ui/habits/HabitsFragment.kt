package com.example.moodbook.ui.habits

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodbook.R
import com.example.moodbook.databinding.FragmentHabitsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Observable
import java.util.Observer


class HabitsFragment : Fragment(), Observer, OnCompleteListener<QuerySnapshot> {

    private lateinit var newHabit: Button
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private val habitList = ArrayList<Habit>()
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*  val textView: TextView = binding.textMood
          moodViewModel.text.observe(viewLifecycleOwner) {
              textView.text = it
          }*/
        recyclerView = binding.habitlist

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        newHabit =binding.newHabit
        newHabit.setOnClickListener(View.OnClickListener {
            val i = Intent(newHabit.context, NewHabitActivity::class.java)
            startActivity(i)
        })
        db!!.collection("users").document(mAuth!!.currentUser!!.uid).collection("habits").get()
            .addOnCompleteListener(this)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun update(o: Observable, arg: Any) {
        val h = arg as Habit
        val progressUpdate: MutableMap<String, String> = HashMap()
        progressUpdate["Progress"] = Integer.toString(h.progress)
        db!!.collection("users").document(mAuth!!.currentUser!!.uid).collection("habits")
            .document(h.habitId)[progressUpdate] = SetOptions.merge()
    }

    override fun onComplete(task: Task<QuerySnapshot>) {
        if (task.isSuccessful) {
            for (document in task.result) {
                Log.d(TAG, document.id + "=>" + document.data)
                val endDate = Calendar.getInstance()
                val startDate = Calendar.getInstance()
                var date1: Date? = null
                var date2: Date? = null
                try {
                    date1 = SimpleDateFormat("dd/MM/yyyy").parse(document.getString("Start Date"))
                    date2 = SimpleDateFormat("dd/MM/yyyy").parse(document.getString("End Date"))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                startDate.time = date1
                if (date2 != null) {
                    endDate.time = date2
                }
                val habit = Habit(
                    document.id,
                    document.getString("Habit Name"),
                    startDate,
                    endDate,
                    document.getString("Frequency"),
                    document.getString("Progress")!!
                        .toInt()
                )
                habit.addObserver(this)
                habitList.add(habit)
            }
            initRecyclerView()
        } else {
            Log.d(TAG, "Error getting documents: ", task.exception)
        }
    }

    private fun initRecyclerView() {
        Log.d(TAG, "Initialize recycler view")
        val habitListView = binding.habitlist
        val adapter = HabitAdapter(requireContext(), habitList)
        habitListView.adapter = adapter
        habitListView.layoutManager = LinearLayoutManager(activity)
        adapter.notifyDataSetChanged()
    }
    companion object {
        private const val TAG = "HabitTrackerActivity"
    }
}