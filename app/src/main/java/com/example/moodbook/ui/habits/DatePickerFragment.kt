package com.example.moodbook.ui.mood

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar

class DatePickerFragment(private val dateString: EditText?) : DialogFragment(), OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(requireActivity(), this, year, month, day)
        dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        return dialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = year
        c[Calendar.MONTH] = month
        c[Calendar.DAY_OF_MONTH] = dayOfMonth
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat)
        dateString!!.setText(sdf.format(c.time))
    }
}