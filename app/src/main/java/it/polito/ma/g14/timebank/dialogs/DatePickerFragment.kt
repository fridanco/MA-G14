package it.polito.ma.g14.timebank.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment(private val dateSetListener: DatePickerDialog.OnDateSetListener, val date: String) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker

        val c = Calendar.getInstance()

        if(date.isNotEmpty()){
            c.time = SimpleDateFormat("EEE, d MMM yyyy").parse(date)
        }

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        val datePickerDialog = DatePickerDialog(requireActivity(), dateSetListener, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()-1000
        return datePickerDialog
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        //implemented by calling fragment - dateSetListener
    }
}