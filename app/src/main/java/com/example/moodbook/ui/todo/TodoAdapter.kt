package com.example.moodbook.ui.todo

    import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.CheckBox
    import android.widget.CompoundButton
    import android.widget.EditText
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.example.moodbook.R
    import javax.security.auth.callback.Callback



    class TodoAdapter(
        private val todos: MutableList<Task>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


        private lateinit var mlistener: OnItemClickListener

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo, parent,
                false
            )
            return TodoViewHolder(itemView, mlistener)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            mlistener = listener
        }

        fun addTodo(t: Task) {
            todos.add(t)
            notifyDataSetChanged()
        }
        fun deleteTodo(t: Task) {
            todos.remove(t)
            notifyDataSetChanged()
        }

        private fun toggleStrikeThrough(tvTodoTitle: TextView, completed: Boolean) {
            if (completed) {
                tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG

            } else {
                tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

            val curTodo = todos[position]
            holder.itemView.apply {
                val tvTodoTitle = findViewById<TextView>(R.id.tvTodoTitle)
                val cbDone = findViewById<CheckBox>(R.id.cbDone)
                val tvStartDate = findViewById<TextView>(R.id.editStartDate)
                val tvEndDate = findViewById<TextView>(R.id.editFinishDate)
                tvTodoTitle.text = curTodo.taskName
                cbDone.isChecked = curTodo.isChecked
                tvStartDate.text = curTodo.start_date
                tvEndDate.text = curTodo.end_date
                toggleStrikeThrough(tvTodoTitle, curTodo.completed)

                Log.i("curtodo: " + curTodo.taskName, "is barrato: " + curTodo.completed)
                cbDone.setOnCheckedChangeListener { _, isChecked ->
                    curTodo.setChecked()
                    Log.i("curtodo: " + curTodo.taskName, "is checked: " + curTodo.isChecked)


                }
            }
        }

        override fun getItemCount(): Int {
            return todos.size
        }

        class TodoViewHolder(itemView: View, listener: OnItemClickListener) :
            RecyclerView.ViewHolder(itemView) {



            init {
                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition)
                }

            }
        }
    }

