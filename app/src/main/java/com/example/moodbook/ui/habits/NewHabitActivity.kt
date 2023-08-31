package com.example.moodbook.ui.habits

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moodbook.MainActivity
import com.example.moodbook.R
import com.example.moodbook.databinding.ActivityMainBinding
import com.example.moodbook.databinding.ActivityNewHabitBinding
import com.example.moodbook.ui.mood.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar

class NewHabitActivity : AppCompatActivity() {
    private lateinit var habitName: EditText
    private lateinit var habitEndDate: EditText
    private lateinit var trackingFrequency: Spinner
    private lateinit var newHabit: Button
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private lateinit var binding: ActivityNewHabitBinding
    private lateinit var constraintLayout: ConstraintLayout
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        habitName = findViewById(R.id.habit_name)
        habitEndDate = findViewById(R.id.habit_end_date)
        trackingFrequency = findViewById(R.id.habit_frequency)
        newHabit = findViewById(R.id.add_new_habit)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.freq,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trackingFrequency.setAdapter(adapter)
        habitEndDate.setOnClickListener(View.OnClickListener {
            val newFragment = DatePickerFragment(habitEndDate)
            newFragment.show(supportFragmentManager, "datePicker")
        })
        newHabit.setOnClickListener(View.OnClickListener {
            addHabit()
            val i = Intent(applicationContext, MainActivity::class.java)
            i.putExtra("num_fragment", 3);
            startActivity(i)

        })
    }

    private fun addHabit() {
        val habit: MutableMap<String, String> = HashMap()
        habit["Habit Name"] = habitName!!.text.toString()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val start = sdf.format(Calendar.getInstance().time)
        habit["Start Date"] = start
        habit["End Date"] = habitEndDate!!.text.toString()
        habit["Frequency"] = trackingFrequency!!.selectedItem.toString()
        habit["Progress"] = "0"
        db!!.collection("users").document(mAuth!!.currentUser!!.uid).collection("habits").document()
            .set(habit)
    }
}