package com.example.moodbook.ui.mood

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moodbook.R
import com.example.moodbook.databinding.FragmentMoodtrackerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class MoodFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentMoodtrackerBinding? = null
    var date = ArrayList<String?>()
    var desc = ArrayList<String?>()
    var mood = ArrayList<String?>()
    var moodid = ArrayList<Int>()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentMoodtrackerBinding.inflate(inflater, container, false)
        val root: View = binding.root

      /*  val textView: TextView = binding.textMood
        moodViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        //make sure to step up for current user
        val user = FirebaseAuth.getInstance().uid
        binding.addmood.setOnClickListener(View.OnClickListener {
            val i = Intent(getActivity(), AddMoodActivity::class.java)
            startActivity(i)
        })
        db.collection("users").document(user!!).collection("moodlog")
            .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(
                EventListener { docsnapshot, e ->
                    date.clear()
                    for (snapshot in docsnapshot!!) {
                        if (e != null) {
                            Log.w("Listen failed.", e)
                            return@EventListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            var mooddate = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(snapshot.getDate("date"))
                            date.add(mooddate)
                            desc.add(snapshot.getString("description"))
                            mood.add(snapshot.getString("moodtype"))
                            moodid.add(Integer.valueOf(snapshot["id"].toString()))
                            Log.d("", "Current data: " + snapshot.data)
                        } else {
                            Log.d("", "Current data: null")
                        }
                    }
                    if (moodid.size != 0) {
                        curmoodid = moodid[0]
                    } else {
                        curmoodid = 0
                    }
                    val adapcontext = container!!.context
                    val adapter: Adapter = Adapter(
                       adapcontext,date, desc, mood
                    )
                    adapter.notifyDataSetChanged()
                   binding.moodlist.setAdapter(adapter)
                })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    internal inner class Adapter(
        @get:JvmName("getAdapterContext") var context: Context,
        var date: ArrayList<String?>,
        var desc: ArrayList<String?>,
        var mood: ArrayList<String?>
    ) : ArrayAdapter<String?>(
        context, R.layout.activity_moodlog, R.id.date, date as List<String?>
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layout =
                activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = layout.inflate(R.layout.activity_moodlog, parent, false)
            val vdate = row.findViewById<TextView>(R.id.date)
            val vdesc = row.findViewById<TextView>(R.id.description)
            val vmood = row.findViewById<ImageView>(R.id.emoji)
            vdate.text = date[position]
            vdesc.text = desc[position]
            if (mood[position] != null) {
                if (mood[position].equals("sad", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.sad)
                }

                if (mood[position].equals("depressed", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.depressed)
                }

                if (mood[position].equals("happy", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.happy)
                }

                if (mood[position].equals("angry", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.angry)
                }

                if (mood[position].equals("anxious", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.anxious)
                }
                if (mood[position].equals("neutral", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.neutral)
                }
                if (mood[position].equals("sleepy", ignoreCase = true)) {
                    vmood.setImageResource(R.drawable.sleepy)
                }

            }
            return row
        }
    }

    companion object {
        private var curmoodid = 0
        fun getmoodid(): Int {
            return curmoodid
        }
    }
}