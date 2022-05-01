package it.polito.ma.g14.timebank.fragments.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment(private val timeSetListener: TimePickerDialog.OnTimeSetListener, val time: String) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()

        if(time.isNotEmpty()){
            c.time = SimpleDateFormat("HH:mm").parse(time)
        }

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(requireActivity(), timeSetListener, hour, minute, DateFormat.is24HourFormat(requireContext()))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        //implemented by calling fragment - dateSetListener
    }
}