package com.example.moodbook.ui.mood

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moodbook.R
import com.example.moodbook.databinding.ActivityAddmoodBinding
import com.example.moodbook.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddMoodActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddmoodBinding
    private val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().uid

    var moodid: Int = MoodFragment.Companion.getmoodid() + 1
    var boolselect = -1
    var moodtype: String? = null
    private var dateSetListener: OnDateSetListener? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddmoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectdate.setOnClickListener(View.OnClickListener {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            val day = cal[Calendar.DAY_OF_MONTH]
            val d = DatePickerDialog(
                this@AddMoodActivity,  //android.R.style.Widget_Holo_ActionBar_Solid,
                dateSetListener,
                year, month, day
            )
            d.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            d.show()
        })
        dateSetListener = OnDateSetListener { view, year, month, dayOfMonth ->
            var month = month
            month = month + 1

        binding.selectdate.setText("$dayOfMonth/$month/$year")
            Log.d("AddMoodActivity", "date:$dayOfMonth/$month/$year")
        }
        binding.happy.setOnClickListener(View.OnClickListener {
            moodtype = "happy"
            Toast.makeText(this@AddMoodActivity, "I feel happy", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })
        binding.sad.setOnClickListener(View.OnClickListener {
            moodtype = "sad"
            Toast.makeText(this@AddMoodActivity, "I feel sad", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })


        binding.sick.setOnClickListener(View.OnClickListener {
            moodtype = "sick"
            Toast.makeText(this@AddMoodActivity, "I feel sick", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })


        binding.sleepy.setOnClickListener(View.OnClickListener {
            moodtype = "sleepy"
            Toast.makeText(this@AddMoodActivity, "I feel sleepy", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })
        binding.neutral.setOnClickListener(View.OnClickListener {
            moodtype = "neutral"
            Toast.makeText(this@AddMoodActivity, "I feel neutral", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })

        binding.flushed.setOnClickListener(View.OnClickListener {
            moodtype = "flushed"
            Toast.makeText(this@AddMoodActivity, "I feel embarrassed", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })
        binding.depressed.setOnClickListener(View.OnClickListener {
            moodtype = "depressed"
            Toast.makeText(this@AddMoodActivity, "I feel depressed", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })

        binding.angry.setOnClickListener(View.OnClickListener {
            moodtype = "angry"
            Toast.makeText(this@AddMoodActivity, "I feel angry", Toast.LENGTH_SHORT).show()
            if (boolselect == 1) {
                binding.addmood.setEnabled(true)
            }
        })
        binding.selectdate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                boolselect = 1
                if (moodtype != null) {
                    binding.addmood.setEnabled(true)
                }
            }
        })
        binding.addmood.setOnClickListener(View.OnClickListener { v ->
            val i = v.id
            if (i == R.id.addmood) {
                createMood(
                    user,
                    binding.selectdate.getText().toString(),
                    binding.descbox.getText().toString(),
                    moodtype,
                    moodid
                )
                moodid++
                val j = Intent(applicationContext, MoodFragment::class.java)
                startActivity(j)
            }
        })
    }

    fun createMood(UID: String?, date: String?, desc: String?, mood: String?, moodnum: Int?) {
        val user: MutableMap<String, Any?> = HashMap()
        user["id"] = moodnum
        user["date"] = date
        user["description"] = desc
        user["moodtype"] = mood
        db.collection("users").document(UID!!).collection("moodlog").document().set(user)
            .addOnSuccessListener(this) { Log.w("SUCCESS", "DocumentSnapshot added with ID:") }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "AddingMood Failed",
                    "Error adding document",
                    e
                )
            }
    }
}