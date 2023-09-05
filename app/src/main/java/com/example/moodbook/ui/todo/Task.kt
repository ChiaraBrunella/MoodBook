package com.example.moodbook.ui.todo


class Task {
    lateinit var taskName //mandatory
            : String
    lateinit var start_date //optional
            : String
    lateinit var end_date //optional
            : String
    var taskId: String? = null
    var completed = false

    var isChecked: Boolean = false




    constructor(name: String?, start_date: String?, end_date: String?) {
        if (name != null) {
            taskName = name
        }
        if (start_date != null) {
            this.start_date = start_date
        }
        if (end_date != null) {
            this.end_date = end_date
        }

        completed = false

    }

    constructor() {}

    fun setCompleted() {
        completed = true
    }
    fun setChecked() {
        isChecked = true
    }
    fun setUnChecked() {
        isChecked = false
    }

}