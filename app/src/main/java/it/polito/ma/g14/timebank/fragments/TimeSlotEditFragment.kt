package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import it.polito.ma.g14.timebank.R
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [TimeSlotEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeSlotEditFragment() : Fragment() {

    val vm by viewModels<TimeSlotVM>()

    lateinit var et_title : TextView
    lateinit var et_description : TextView
    lateinit var et_dateTime : TextView
    lateinit var et_duration : TextView
    lateinit var et_location : TextView

    var timeSlotID: Long = 0
    var operationType: String = ""

    var title = ""
    var description = ""
    var date = ""
    var duration = ""
    var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_slot_edit, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeSlotID = requireArguments().getLong("timeSlotID")
        operationType = requireArguments().getString("operationType").toString()

        et_title = view.findViewById<EditText>(R.id.textView51)
        et_description = view.findViewById<EditText>(R.id.textView53)
        et_dateTime = view.findViewById<EditText>(R.id.editTextDate)
        et_duration = view.findViewById<EditText>(R.id.textView56)
        et_location = view.findViewById<EditText>(R.id.textView58)

        if(operationType=="edit_time_slot") {
            vm.getTimeSlot(timeSlotID).observe(viewLifecycleOwner) {
                et_title.text = it.title.toEditable()
                et_description.text = it.description.toEditable()
                et_dateTime.text = it.dateTime.toString().toEditable()
                et_duration.text = it.duration.toString().toEditable()
                et_location.text = it.location.toString().toEditable()
            }
        }

        et_title.doOnTextChanged { text, start, before, count ->
            title = text.toString()
            if (title.trim().isEmpty()) {
                et_title.error = "Title cannot be empty"
            } else {
                et_title.error = null
            }
            if(operationType=="edit_time_slot") {
                vm.editTimeSlot(
                    timeSlotID,
                    title,
                    description,
                    date,
                    5,
                    location
                )
            }
        }


        et_description.doOnTextChanged { text, start, before, count ->
            description = text.toString()
            if (description.trim().isEmpty()) {
                et_description.error = "Description cannot be empty"
            } else {
                et_description.error = null
            }
            if(operationType=="edit_time_slot") {
                vm.editTimeSlot(
                    timeSlotID,
                    title,
                    description,
                    date,
                    5,
                    location
                )
            }
        }

        et_dateTime.doOnTextChanged { text, start, before, count ->
            date = text.toString()
            if (date.trim().isEmpty()) {
                et_dateTime.error = "Date and time cannot be empty"
            } else {
                et_dateTime.error = null
            }
            if(operationType=="edit_time_slot") {
                vm.editTimeSlot(
                    timeSlotID,
                    title,
                    description,
                    date,
                    5,
                    location
                )
            }
        }

        et_duration.doOnTextChanged { text, start, before, count ->
            duration = text.toString()
            if (duration.trim().isEmpty()) {
                et_duration.error = "Duration cannot be empty"
            } else {
                et_duration.error = null
            }
            if(operationType=="edit_time_slot") {
                vm.editTimeSlot(
                    timeSlotID,
                    title,
                    description,
                    date,
                    5,
                    location
                )
            }
        }

        et_location.doOnTextChanged { text, start, before, count ->
            location = text.toString()
            if (location.trim().isEmpty()) {
                et_location.error = "Location cannot be empty"
            } else {
                et_location.error = null
            }
            if(operationType=="edit_time_slot") {
                vm.editTimeSlot(
                    timeSlotID,
                    title,
                    description,
                    date,
                    5,
                    location
                )
            }
        }

    }

    override fun onStop() {
        vm.addTimeSlot(title, description, date, 5, location)
        println("SAVING time slot")
        super.onStop()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val pencilItem = menu.findItem(R.id.app_bar_pencil)
        pencilItem.isVisible = false
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}