package com.example.moodbook.ui.home


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.moodbook.R
import com.example.moodbook.databinding.FragmentHomeBinding
import com.example.moodbook.ui.habits.NewHabitActivity
import com.example.moodbook.ui.mood.AddMoodActivity
import com.example.moodbook.ui.todo.ToDoFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment(), OnCompleteListener<QuerySnapshot> {

    private var _binding: FragmentHomeBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    var date = ArrayList<String?>()
    var desc = ArrayList<String?>()
    var mood = ArrayList<String?>()
    var moodid = ArrayList<Int>()
    var habitsList =ArrayList<String?>()
    var tasksList =ArrayList<String?>()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

       /* val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        //make sure to step up for current user
            val user = FirebaseAuth.getInstance().uid

        // collect user data

           db!!.collection("users").document(user!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val docsnap = task.result
                    val name = docsnap.getString("Name")
                    binding.textHome?.text= "Ciao, " + name
                }
            }

        db.collection("users").document(user!!).collection("moodlog")
            .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(
                EventListener { docsnapshot, e ->

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
                        HomeFragment.curmoodid = moodid[0]
                    } else {
                        HomeFragment.curmoodid = 0
                    }

                    // set mood cardview data
                    val vdate = binding.moodDate
                    val vdesc = binding.description
                    val vmood =binding.emoji
                    if (!date.isEmpty()) {
                        vdate.text = date[0]
                    } else
                        vdate.text = "Non hai ancora inserito alcuna emozione"
                    if (!desc.isEmpty()) {
                        vdesc.text = desc[0]
                    }
                    else
                        vdesc.text = ""
                    if (!mood.isEmpty()) {
                    if (mood[0] != null) {
                        if (mood[0].equals("sad", ignoreCase = true)) {
                            vmood.setImageResource(R.drawable.sad)
                        }

                        if (mood[0].equals("depressed", ignoreCase = true)) {
                            vmood.setImageResource(R.drawable.depressed)
                        }

                        if (mood[0].equals("happy", ignoreCase = true)) {
                            vmood.setImageResource(com.example.moodbook.R.drawable.happy)
                        }

                        if (mood[0].equals("angry", ignoreCase = true)) {
                            vmood.setImageResource(R.drawable.angry)
                        }

                        if (mood[0].equals("anxious", ignoreCase = true)) {
                            vmood.setImageResource(R.drawable.anxious)
                        }
                        if (mood[0].equals("neutral", ignoreCase = true)) {
                            vmood.setImageResource(R.drawable.neutral)
                        }
                        if (mood[0].equals("sleepy", ignoreCase = true)) {
                            vmood.setImageResource(R.drawable.sleepy)
                        }

                    }}
                    else {
                        vmood.setImageResource(R.drawable.hands_up)
                    }
                    // set habits cardview data
                    val lvHabits = binding.todayHabitList
                    db!!.collection("users").document(mAuth!!.currentUser!!.uid).collection("habits").get()
                        .addOnCompleteListener(this)




                    // set tasks cardview data
                    val lvTasks = binding.todayTasksList
                    db!!.collection("users").document(mAuth!!.currentUser!!.uid).collection("taskLog")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (document in task.result) {
                                        val taskItem = document.data
                                        val taskName = taskItem["taskname"] as String?
                                        val complete = taskItem["completed"] as Boolean
                                        val t = com.example.moodbook.ui.todo.Task()
                                        t.setCompleted()
                                        t.taskName = taskName.toString()
                                        t.taskId = document.id
                                        Log.i("taskname", taskName.toString())
                                        if (!complete) {
                                            tasksList.add(taskName.toString())
                                            Log.i("taskname aggiunto a incompleted list", taskName.toString())
                                            Log.i("incompleted list lenght", tasksList.size.toString())
                                        }
                                    }
                                    var taskList: MutableList<String?> = ArrayList()
                                    taskList.clear()
                                    taskList.addAll(tasksList)
                                    val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                        lvTasks.getContext(), android.R.layout.simple_list_item_1, taskList)
                                    lvTasks.setAdapter(arrayAdapter)
                                    lvTasks.divider = null

                                    arrayAdapter.notifyDataSetChanged()


                                }
                            }




                })

        binding.addMood.setOnClickListener(View.OnClickListener {
            val i = Intent(getActivity(), AddMoodActivity::class.java)
            startActivity(i)
        })

        binding.addHabit.setOnClickListener(View.OnClickListener {
            val i = Intent(context, NewHabitActivity::class.java)
            startActivity(i)
        })

        binding.addToDo.setOnClickListener(View.OnClickListener {

            /*val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
            ft.replace(R.id.mobile_navigation, ToDoFragment(), "NewFragmentTag")
            ft.commit()*/
            findNavController(this).navigate(R.id.nav_home_to_nav_toDo);})
        return root
    }

    override fun onComplete(task: Task<QuerySnapshot>) {
        if (task.isSuccessful) {
            for (document in task.result) {
                val habitItem = document.data
                val habitName = habitItem["Habit Name"] as String?

                habitsList.add(habitName)
                Log.d( "habit added: ", habitName.toString())
            initHabitslistView()
            }

        } else {
            Log.d( "Error getting documents: ", task.exception.toString())
        }
    }
    private fun initHabitslistView() {
        val lvHabits = binding.todayHabitList
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            lvHabits.getContext(), android.R.layout.simple_list_item_1, habitsList)
        lvHabits.setAdapter(arrayAdapter)
        lvHabits.divider = null
        arrayAdapter.notifyDataSetChanged()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private var curmoodid = 0
        fun getmoodid(): Int {
            return curmoodid
        }
    }

}