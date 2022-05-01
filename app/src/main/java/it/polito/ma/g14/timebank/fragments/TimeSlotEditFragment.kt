package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.fragments.dialogs.DatePickerFragment
import it.polito.ma.g14.timebank.fragments.dialogs.TimePickerFragment
import it.polito.ma.g14.timebank.utils.Utils


class TimeSlotEditFragment() : Fragment() {

    val vm by viewModels<TimeSlotVM>()

    private var et_title : TextView? = null
    private var et_description : TextView? = null
    private var et_date : TextView? = null
    private var et_from : TextView? = null
    private var et_to : TextView? = null
    private var et_location : TextView? = null

    private var h_et_title : TextView? = null
    private var h_et_description : TextView? = null
    private var h_et_date : TextView? = null
    private var h_et_from : TextView? = null
    private var h_et_to : TextView? = null
    private var h_et_location : TextView? = null

    var timeSlotID: Long = 0
    var operationType: String = ""

    var title = ""
    var description = ""
    var date = ""
    var from = ""
    var to = ""
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
        et_date = view.findViewById<EditText>(R.id.textView56)
        et_from = view.findViewById<EditText>(R.id.textView62)
        et_to = view.findViewById<EditText>(R.id.textView63)
        et_location = view.findViewById<EditText>(R.id.textView58)

        h_et_title = view.findViewById<EditText>(R.id.textView42)
        h_et_description = view.findViewById<EditText>(R.id.textView44)
        h_et_date = view.findViewById<EditText>(R.id.textView46)
        h_et_from = view.findViewById<EditText>(R.id.textView67)
        h_et_to = view.findViewById<EditText>(R.id.textView68)
        h_et_location = view.findViewById<EditText>(R.id.textView49)

        if(operationType=="edit_time_slot") {
            vm.getTimeSlot(timeSlotID).observe(viewLifecycleOwner) {
                et_title?.text = it.title.toEditable()
                et_description?.text = it.description.toEditable()
                et_date?.text = it.date.toEditable()
                et_from?.text = it.from.toEditable()
                et_to?.text = it.to.toEditable()
                et_location?.text = it.location.toEditable()
                h_et_title?.text = it.title.toEditable()
                h_et_description?.text = it.description.toEditable()
                h_et_date?.text = it.date.toEditable()
                h_et_from?.text = it.from.toEditable()
                h_et_to?.text = it.to.toEditable()
                h_et_location?.text = it.location.toEditable()
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

        et_date?.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }
        h_et_date?.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }

        et_from?.setOnClickListener {
            TimePickerFragment().show(requireActivity().supportFragmentManager, "timePicker")
        }
        h_et_from?.setOnClickListener {
            TimePickerFragment().show(requireActivity().supportFragmentManager, "timePicker")
        }

        et_to?.setOnClickListener {
            TimePickerFragment().show(requireActivity().supportFragmentManager, "timePicker")
        }
        h_et_to?.setOnClickListener {
            TimePickerFragment().show(requireActivity().supportFragmentManager, "timePicker")
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
                vm.editTimeSlot(timeSlotID, title, description, date, from, to, location)
            }
            else if(operationType=="add_time_slot"){
                vm.addTimeSlot(title, description, date, from, to, location)
            }
        }
        super.onDestroy()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun addTimeSlot(){
        vm.addTimeSlot(title, description, date, from, to, location)
    }

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
            et_date?.error = "Date must be selected"
            h_et_date?.error = "Date must be selected"
        }
        if(from.trim().isEmpty()){
            et_from?.error = "Start time must be selected"
            h_et_from?.error = "Start time must be selected"
        }
        if(to.trim().isEmpty()){
            et_to?.error = "End time must be selected"
            h_et_to?.error = "End time must be selected"
        }
        if(location.trim().isEmpty()){
            et_location?.error = "Location cannot be empty"
            h_et_location?.error = "Location cannot be empty"
        }

        if(et_title?.error != null || et_description?.error != null || et_date?.error != null || et_from?.error != null || et_to?.error != null || et_location?.error != null ||
            h_et_title?.error != null || h_et_description?.error != null || h_et_date?.error != null || h_et_to?.error != null || h_et_to?.error != null || h_et_location?.error != null){
            return false
        }
        return true
    }



}