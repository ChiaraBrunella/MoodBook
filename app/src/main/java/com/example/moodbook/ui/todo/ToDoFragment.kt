package com.example.moodbook.ui.todo

import android.app.DatePickerDialog
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodbook.R
import com.example.moodbook.databinding.FragmentTodoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class ToDoFragment : Fragment() {



    private var _binding: FragmentTodoBinding?  = null
    private lateinit var todoAdapter: TodoAdapter


    private var db: FirebaseFirestore? = null
    private var uid: String? = null

    private val completedList: MutableList<Task> = ArrayList()
    private var dateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var enddateSetListener: DatePickerDialog.OnDateSetListener? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val toDoViewModel =
            ViewModelProvider(this).get(ToDoViewModel::class.java)

        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        val root: View = binding.root


        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        db!!.collection("users").document(uid!!).collection("habits").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val taskItem = document.data
                        val taskName = taskItem["taskname"] as String?
                        val startDate = taskItem["startDate"] as String?
                        val finishDate = taskItem["finishDate"] as String?
                        val complete = taskItem["completed"] as Boolean
                        val t = Task()
                        t.end_date = finishDate.toString()
                        t.start_date = startDate.toString()
                        t.taskName = taskName.toString()
                        t.taskId = document.id

                        Log.i("taskname da snapshot", taskName.toString())

                        completedList.add(t)
                        Log.i("taskname aggiunto a completed list", taskName.toString())
                        Log.i("completed list lenght", completedList.size.toString())
                        todoAdapter. notifyItemInserted(completedList.size - 1)
                    }

                }
            }
        val todoAdapter = TodoAdapter(completedList)
       binding.rvTodoItems.adapter = todoAdapter
        binding.rvTodoItems.layoutManager = LinearLayoutManager(context)





   /* binding.rvTodoItems.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val infoTaskDialogView = layoutInflater.inflate(R.layout.info_task_dialog, null)
            val taskLabel = infoTaskDialogView.findViewById<TextView>(R.id.taskLabel)
            val startDateLabel = infoTaskDialogView.findViewById<TextView>(R.id.startDateLabel)
            val endDateLabel = infoTaskDialogView.findViewById<TextView>(R.id.endDateLabel)

                val task = completedList[position]
                taskLabel.text = task.taskName
                startDateLabel.text = task.start_date
                endDateLabel.text = task.end_date
                val infoDialog = AlertDialog.Builder(requireActivity())
                    .setView(infoTaskDialogView)
                    .setPositiveButton("Mark As Completed") { dialog, which -> //mark task as completed
                        updateCompleteInDb(task.taskId)
                        task.completed = true
                        completedList.add(task)

                        todoAdapter!!.setData(completedList)
                    }
                    .setNegativeButton("Cancel Task") { dialog, which ->
                        removeTaskFromDb(task.taskId)
                        completedList.remove(task)
                        todoAdapter!!.setData(completedList)
                    }
                    .create()
                infoDialog.show()

        })*/
        binding.btnAddTodo.setOnClickListener(View.OnClickListener {
            val addTaskDialogView = layoutInflater.inflate(R.layout.add_new_task_dialog, null)
            val taskName = addTaskDialogView.findViewById<EditText>(R.id.task_name)
            val startDate = addTaskDialogView.findViewById<TextView>(R.id.start_date)
            val finishDate = addTaskDialogView.findViewById<TextView>(R.id.end_date)
            startDate.setOnClickListener {
                val cal = Calendar.getInstance()
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                val d = DatePickerDialog(
                    addTaskDialogView.context,  //android.R.style.Widget_Holo_ActionBar_Solid,
                    dateSetListener,
                    year, month, day
                )
                d.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                d.show()
            }
            finishDate.setOnClickListener {
                val cal = Calendar.getInstance()
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                val d = DatePickerDialog(
                    addTaskDialogView.context,  //android.R.style.Widget_Holo_ActionBar_Solid,
                    enddateSetListener,
                    year, month, day
                )
                d.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                d.show()
            }
            dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    var month = month
                    month = month + 1
                    startDate.text = "$dayOfMonth/$month/$year"
                    Log.d("task added", "date:$dayOfMonth/$month/$year")
                }
            enddateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    var month = month
                    month = month + 1
                    finishDate.text = "$dayOfMonth/$month/$year"
                    Log.d("task added", "date:$dayOfMonth/$month/$year")
                }
            val dialog = AlertDialog.Builder(requireContext())
                .setView(addTaskDialogView)
                .setPositiveButton(null) { dialog, which ->
                    if (taskName.text.toString().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "ERROR: Please specify task name",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Successfully Added Task",
                            Toast.LENGTH_SHORT
                        ).show()
                        val newTask = Task(
                            taskName.text.toString(),
                            startDate.text.toString(),
                            finishDate.text.toString()
                        )
                        completedList.add(newTask)
                        //add to database
                        addTaskToDatabase(
                            taskName.text.toString(),
                            startDate.text.toString(),
                            finishDate.text.toString(),
                            newTask.taskId,
                            false
                        )

                    }
                }
                .setPositiveButtonIcon(
                    AppCompatResources.getDrawable ( it.context,
                        R.drawable.complete_task  ))
                .create()
            dialog.show()
        })


        binding.btnDeleteDoneTodos.setOnClickListener {
            completedList.removeAll { todo ->
                todo.isChecked

            }
            for(todo: Task in completedList){
                if (todo.isChecked) {todo.setCompleted()
                    db!!.collection("users").document(uid!!).collection("taskLog").document(todo.taskId!!)
                        .update("completed", true)
                db!!.collection("users").document(uid!!).collection("taskLog").document(todo.taskId!!).delete()}
        }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal inner class MyListAdapter : BaseAdapter() {
        var taskList: MutableList<Task> = ArrayList()
        fun setData(mList: List<Task>?) {
            taskList.clear()
            taskList.addAll(mList!!)

           // Log.i("taskname in setdatalis", taskList[1].taskName.toString())
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return taskList.size
        }

        override fun getItem(position: Int): Any {
            return taskList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var taskRow = convertView
            val inflateLayout =
                getActivity()?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (taskRow == null)
                taskRow = inflateLayout.inflate(R.layout.task, parent, false)
            val taskObject = taskRow?.findViewById<TextView>(R.id.taskItem)
            taskObject?.text = taskList[position].taskName
            Log.i("taskname in row", taskList[position].taskName.toString())
            return taskRow
        }
    }

    fun addTaskToDatabase(
        taskName: String,
        startDate: String,
        finishDate: String,
        taskId: String?,
        completed: Boolean
    ) {
        val newTaskForUser: MutableMap<String, Any> = HashMap()
        newTaskForUser["taskname"] = taskName
        newTaskForUser["startDate"] = startDate
        newTaskForUser["finishDate"] = finishDate
        newTaskForUser["completed"] = completed
        db!!.collection("users").document(uid!!).collection("taskLog").document(taskId!!)
            .set(newTaskForUser)
    }

    val allTasksFromDB: Unit
        get() {
            db!!.collection("users").document(uid!!).collection("taskLog")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val taskItem = document.data
                            val taskName = taskItem["taskname"] as String?
                            val startDate = taskItem["startDate"] as String?
                            val finishDate = taskItem["finishDate"] as String?
                            val complete = taskItem["completed"] as Boolean
                            val t = Task()
                            t.end_date = finishDate.toString()
                            t.start_date = startDate.toString()
                            t.taskName = taskName.toString()
                            t.taskId = document.id

                            Log.i("taskname da snapshot", taskName.toString())

                                completedList.add(t)
                                Log.i("taskname aggiunto a completed list", taskName.toString())
                                Log.i("completed list lenght", completedList.size.toString())

                        }

                    }
                }
        }

    fun updateCompleteInDb(taskID: String?) {
        db!!.collection("users").document(uid!!).collection("taskLog").document(taskID!!)
            .update("completed", true)
    }

    fun removeTaskFromDb(taskID: String?) {
        db!!.collection("users").document(uid!!).collection("taskLog").document(taskID!!).delete()
    }

    companion object {
        private const val TAG = "ToDoListActivity"
    }


}