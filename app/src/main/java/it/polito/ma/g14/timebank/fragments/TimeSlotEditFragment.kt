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
import it.polito.ma.g14.timebank.utils.Utils
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [TimeSlotEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeSlotEditFragment() : Fragment() {

    val vm by viewModels<TimeSlotVM>()

    private var et_title : TextView? = null
    private var et_description : TextView? = null
    private var et_dateTime : TextView? = null
    private var et_duration : TextView? = null
    private var et_location : TextView? = null
    private var h_et_title : TextView? = null
    private var h_et_description : TextView? = null
    private var h_et_dateTime : TextView? = null
    private var h_et_duration : TextView? = null
    private var h_et_location : TextView? = null

    var timeSlotID: Long = 0
    var operationType: String = ""

    var title = ""
    var description = ""
    var date = ""
    var duration = ""
    var location = ""

    var cancelOperation = false

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

        cancelOperation = false

        timeSlotID = requireArguments().getLong("timeSlotID")
        operationType = requireArguments().getString("operationType").toString()

        et_title = view.findViewById<EditText>(R.id.textView51)
        et_description = view.findViewById<EditText>(R.id.textView53)
        et_dateTime = view.findViewById<EditText>(R.id.editTextDate)
        et_duration = view.findViewById<EditText>(R.id.textView56)
        et_location = view.findViewById<EditText>(R.id.textView58)

        h_et_title = view.findViewById<EditText>(R.id.textView42)
        h_et_description = view.findViewById<EditText>(R.id.textView44)
        h_et_dateTime = view.findViewById<EditText>(R.id.editTextDate2)
        h_et_duration = view.findViewById<EditText>(R.id.textView47)
        h_et_location = view.findViewById<EditText>(R.id.textView49)

        if(operationType=="edit_time_slot") {
            vm.getTimeSlot(timeSlotID).observe(viewLifecycleOwner) {
                et_title?.text = it.title.toEditable()
                et_description?.text = it.description.toEditable()
                et_dateTime?.text = it.dateTime.toString().toEditable()
                et_duration?.text = it.duration.toString().toEditable()
                et_location?.text = it.location.toString().toEditable()
                h_et_title?.text = it.title.toEditable()
                h_et_description?.text = it.description.toEditable()
                h_et_dateTime?.text = it.dateTime.toString().toEditable()
                h_et_duration?.text = it.duration.toString().toEditable()
                h_et_location?.text = it.location.toString().toEditable()
            }
        }

        et_title?.doOnTextChanged { text, start, before, count ->
            title = text.toString()
            if (title.trim().isEmpty()) {
                et_title?.error = "Title cannot be empty"
            } else {
                et_title?.error = null
            }
        }
        h_et_title?.doOnTextChanged { text, start, before, count ->
            title = text.toString()
            if (title.trim().isEmpty()) {
                h_et_title?.error = "Title cannot be empty"
            } else {
                h_et_title?.error = null
            }
        }


        et_description?.doOnTextChanged { text, start, before, count ->
            description = text.toString()
            if (description.trim().isEmpty()) {
                et_description?.error = "Description cannot be empty"
            } else {
                et_description?.error = null
            }
        }
        h_et_description?.doOnTextChanged { text, start, before, count ->
            description = text.toString()
            if (description.trim().isEmpty()) {
                h_et_description?.error = "Description cannot be empty"
            } else {
                h_et_description?.error = null
            }
        }

        et_dateTime?.doOnTextChanged { text, start, before, count ->
            date = text.toString()
            if (date.trim().isEmpty()) {
                et_dateTime?.error = "Date and time cannot be empty"
            } else {
                et_dateTime?.error = null
            }
        }
        h_et_dateTime?.doOnTextChanged { text, start, before, count ->
            date = text.toString()
            if (date.trim().isEmpty()) {
                h_et_dateTime?.error = "Date and time cannot be empty"
            } else {
                h_et_dateTime?.error = null
            }
        }

        et_duration?.doOnTextChanged { text, start, before, count ->
            duration = text.toString()
            if (duration.trim().isEmpty()) {
                et_duration?.error = "Duration cannot be empty"
            } else {
                et_duration?.error = null
            }
        }
        h_et_duration?.doOnTextChanged { text, start, before, count ->
            duration = text.toString()
            if (duration.trim().isEmpty()) {
                h_et_duration?.error = "Duration cannot be empty"
            } else {
                h_et_duration?.error = null
            }
        }

        et_location?.doOnTextChanged { text, start, before, count ->
            location = text.toString()
            if (location.trim().isEmpty()) {
                et_location?.error = "Location cannot be empty"
            } else {
                et_location?.error = null
            }
        }
        h_et_location?.doOnTextChanged { text, start, before, count ->
            location = text.toString()
            if (location.trim().isEmpty()) {
                h_et_location?.error = "Location cannot be empty"
            } else {
                h_et_location?.error = null
            }
        }

    }

    override fun onDestroy() {
        if(!cancelOperation){
            if(operationType=="edit_time_slot"){
                vm.editTimeSlot(timeSlotID, title, description, date, 5, location)
            }
            else if(operationType=="add_time_slot"){
                vm.addTimeSlot(title, description, date, 5, location)
            }
        }
        super.onDestroy()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun isFormValid() : Boolean {
        if(title.trim().isEmpty()){
            et_title?.error = "Title cannot be empty"
            h_et_title?.error = "Title cannot be empty"
        }
        if(description.trim().isEmpty()){
            et_description?.error = "Description cannot be empty"
            h_et_description?.error = "Description cannot be empty"
        }
        if(date.trim().isEmpty()){
            et_dateTime?.error = "Date and time cannot be empty"
            h_et_dateTime?.error = "Date and time cannot be empty"
        }
        if(duration.trim().isEmpty()){
            et_duration?.error = "Duration cannot be empty"
            h_et_duration?.error = "Duration cannot be empty"
        }
        if(location.trim().isEmpty()){
            et_location?.error = "Location cannot be empty"
            h_et_location?.error = "Location cannot be empty"
        }

        if(et_title?.error != null || et_description?.error != null || et_dateTime?.error != null || et_duration?.error != null || et_location?.error != null ||
            h_et_title?.error != null || h_et_description?.error != null || h_et_dateTime?.error != null || h_et_duration?.error != null || h_et_location?.error != null){
            return false
        }
        return true
    }
    
}