package it.polito.ma.g14.timebank.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.allViews
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.dialogs.DatePickerFragment
import it.polito.ma.g14.timebank.dialogs.TimePickerFragment
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.EditAdVM
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


class MyAdEditFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()
    val editAdVM by viewModels<EditAdVM>()

    private var et_title : TextView? = null
    private var et_description : TextView? = null
    private var et_date : TextView? = null
    private var et_from : TextView? = null
    private var et_to : TextView? = null
    private var et_location : TextView? = null
    private var et_skills : LinearLayout? = null

    private var h_et_title : TextView? = null
    private var h_et_description : TextView? = null
    private var h_et_date : TextView? = null
    private var h_et_from : TextView? = null
    private var h_et_to : TextView? = null
    private var h_et_location : TextView? = null
    private var h_et_skills : LinearLayout? = null

    var advertisementID: String = ""
    var operationType: String = ""
    var originFragment: String = ""

    var title = ""
    var description = ""
    var date = ""
    var from = ""
    var to = ""
    var location = ""
    var advertisementSkills = mutableListOf<String>()

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
        val view = inflater.inflate(R.layout.fragment_my_ad_edit, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelOperation = false

        advertisementID = requireArguments().getString("advertisementID").toString()
        operationType = requireArguments().getString("operationType").toString()
        originFragment = requireArguments().getString("originFragment") ?: "time_slot_details"

        view.findViewById<TextView>(R.id.textView78).setOnClickListener {
            editAdVM.setNewAdvertisement(
                title,
                description,
                date,
                from,
                to,
                location,
                advertisementSkills
            )
            val bundle = bundleOf("createAdSrc" to true)
            findNavController().navigate(R.id.myProfileEdit, bundle)
        }

        et_title = view.findViewById<EditText>(R.id.textView51)
        et_description = view.findViewById<EditText>(R.id.textView53)
        et_date = view.findViewById<EditText>(R.id.textView56)
        et_from = view.findViewById<EditText>(R.id.textView62)
        et_to = view.findViewById<EditText>(R.id.textView63)
        et_location = view.findViewById<EditText>(R.id.textView58)
        et_skills = view.findViewById(R.id.editTimeslotSkillsContainer)

        h_et_title = view.findViewById<EditText>(R.id.textView42)
        h_et_description = view.findViewById<EditText>(R.id.textView44)
        h_et_date = view.findViewById<EditText>(R.id.textView46)
        h_et_from = view.findViewById<EditText>(R.id.textView67)
        h_et_to = view.findViewById<EditText>(R.id.textView68)
        h_et_location = view.findViewById<EditText>(R.id.textView49)
        h_et_skills = view.findViewById(R.id.editTimeslotSkillsContainer)

        if(operationType=="add_time_slot") {

            editAdVM.newAdvertisement.observe(viewLifecycleOwner){
                title = it.title
                description = it.description
                date = it.date
                from = it.from
                to = it.to
                location = it.location

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

            vm.profile.observe(viewLifecycleOwner) {
                val newAdSkills = editAdVM.getNewAdvertisementSkills()
                val inflater: LayoutInflater = layoutInflater
                et_skills?.removeAllViews()
                if(it.skills.isNotEmpty()){
                    view.findViewById<TextView>(R.id.textView79).isGone = true
                    view.findViewById<TextView>(R.id.textView78).isGone = true
                    it.skills.forEach { skill ->
                        val skillCard = inflater.inflate(R.layout.my_ad_skill_card, null)
                        skillCard.findViewById<TextView>(R.id.textView72).text = skill
                        skillCard.findViewById<CheckBox>(R.id.checkBox).isChecked = newAdSkills.isEmpty() || newAdSkills.contains(skill)
                        advertisementSkills.add(skill)
                        skillCard.findViewById<CheckBox>(R.id.checkBox).setOnCheckedChangeListener { _, isChecked  ->
                            if(isChecked){
                                advertisementSkills.add(skill)
                            }
                            else{
                                if(advertisementSkills.size>1) {
                                    advertisementSkills.remove(skill)
                                }
                                else{
                                    skillCard.findViewById<CheckBox>(R.id.checkBox).isChecked = true
                                    Toast.makeText(requireContext(), "At least 1 skill must be selected", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        et_skills?.addView(skillCard)
                    }
                }
                else{
                    view.findViewById<TextView>(R.id.textView79).isVisible = true
                    view.findViewById<TextView>(R.id.textView78).isVisible = true
                }
            }
        }
        //Edit advertisement (time slot)
        else{
            view.findViewById<TextView>(R.id.textView79).isGone = true
            view.findViewById<TextView>(R.id.textView78).isGone = true
            editAdVM.myAdvertisements.observe(viewLifecycleOwner) { myAdvertisementsList ->
                val ad = myAdvertisementsList.find { it.id==advertisementID }
                ad?.let {
                    title = it.title
                    description = it.description
                    date = it.date
                    from = it.from
                    to = it.to
                    location = it.location

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

                    val adSkills = it.skills

                    val displayedSkills = mutableListOf<String>()
                    et_skills?.allViews?.forEach { skillView ->
                        if(skillView is CardView) {
                            displayedSkills.add(skillView.findViewById<TextView>(R.id.textView72).text.toString())
                        }
                    }

                    val inflater: LayoutInflater = layoutInflater
                    adSkills.forEach { skill ->
                        if(!displayedSkills.contains(skill)){
                            val skillCard = inflater.inflate(R.layout.my_ad_skill_card, null)
                            skillCard.findViewById<TextView>(R.id.textView72).text = skill
                            skillCard.findViewById<CheckBox>(R.id.checkBox).isChecked = true
                            advertisementSkills.add(skill)
                            skillCard.findViewById<CheckBox>(R.id.checkBox).setOnCheckedChangeListener { _, isChecked  ->
                                if(isChecked){
                                    advertisementSkills.add(skill)
                                }
                                else{
                                    if(advertisementSkills.size>1) {
                                        advertisementSkills.remove(skill)
                                    }
                                    else{
                                        skillCard.findViewById<CheckBox>(R.id.checkBox).isChecked = true
                                        Toast.makeText(requireContext(), "At least 1 skill must be selected", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            et_skills?.addView(skillCard)
                        }
                    }

                }
            }

            vm.profile.observe(viewLifecycleOwner){ userProfile ->
                val displayedSkills = mutableListOf<String>()
                et_skills?.allViews?.forEach { skillView ->
                    if(skillView is CardView) {
                        displayedSkills.add(skillView.findViewById<TextView>(R.id.textView72).text.toString())
                    }
                }

                val inflater: LayoutInflater = layoutInflater
                userProfile.skills.forEach { skill ->
                    if(!displayedSkills.contains(skill)){
                        val skillCard = inflater.inflate(R.layout.my_ad_skill_card, null)
                        skillCard.findViewById<TextView>(R.id.textView72).text = skill
                        skillCard.findViewById<CheckBox>(R.id.checkBox).isChecked = false
                        skillCard.findViewById<CheckBox>(R.id.checkBox).setOnCheckedChangeListener { _, isChecked  ->
                            if(isChecked){
                                advertisementSkills.add(skill)
                            }
                            else{
                                if(advertisementSkills.size>1) {
                                    advertisementSkills.remove(skill)
                                }
                                else{
                                    skillCard.findViewById<CheckBox>(R.id.checkBox).isChecked = true
                                    Toast.makeText(requireContext(), "At least 1 skill must be selected", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        et_skills?.addView(skillCard)
                    }
                }
            }

        }

        et_title?.doOnTextChanged { text, _, _, _ ->
            title = text.toString()
            if (title.trim().isEmpty()) {
                et_title?.error = "Title cannot be empty"
            } else {
                et_title?.error = null
            }
        }
        h_et_title?.doOnTextChanged { text, _, _, _ ->
            title = text.toString()
            if (title.trim().isEmpty()) {
                h_et_title?.error = "Title cannot be empty"
            } else {
                h_et_title?.error = null
            }
        }


        et_description?.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
            if (description.trim().isEmpty()) {
                et_description?.error = "Description cannot be empty"
            } else {
                et_description?.error = null
            }
        }
        h_et_description?.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
            if (description.trim().isEmpty()) {
                h_et_description?.error = "Description cannot be empty"
            } else {
                h_et_description?.error = null
            }
        }

        et_date?.setOnClickListener {
            DatePickerFragment(dateSetListener, date).show(requireActivity().supportFragmentManager, "datePicker")
        }
        h_et_date?.setOnClickListener {
            DatePickerFragment(dateSetListener, date).show(requireActivity().supportFragmentManager, "datePicker")
        }

        et_from?.setOnClickListener {
            TimePickerFragment(fromTimeSetListener, from).show(requireActivity().supportFragmentManager, "fromTimePicker")
        }
        h_et_from?.setOnClickListener {
            TimePickerFragment(fromTimeSetListener, from).show(requireActivity().supportFragmentManager, "fromTimePicker")
        }

        et_to?.setOnClickListener {
            TimePickerFragment(toTimeSetListener, from).show(requireActivity().supportFragmentManager, "toTimePicker")
        }
        h_et_to?.setOnClickListener {
            TimePickerFragment(toTimeSetListener, from).show(requireActivity().supportFragmentManager, "toTimePicker")
        }

        et_location?.doOnTextChanged { text, _, _, _ ->
            location = text.toString()
            if (location.trim().isEmpty()) {
                et_location?.error = "Location cannot be empty"
            } else {
                et_location?.error = null
            }
        }
        h_et_location?.doOnTextChanged { text, _, _, _ ->
            location = text.toString()
            if (location.trim().isEmpty()) {
                h_et_location?.error = "Location cannot be empty"
            } else {
                h_et_location?.error = null
            }
        }

    }

    override fun onDestroy() {

        if(operationType=="add_time_slot") {
            editAdVM.setNewAdvertisement(
                title,
                description,
                date,
                from,
                to,
                location,
                advertisementSkills
            )
        }
        else {
            editAdVM.setAdvertisement(
                advertisementID,
                title,
                description,
                date,
                from,
                to,
                location,
                advertisementSkills
            )
        }

        super.onDestroy()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
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
        if(from.trim().isNotEmpty() && to.trim().isNotEmpty()){
            val calFrom = Calendar.getInstance()
            calFrom.time = SimpleDateFormat("HH:mm").parse(from)
            val calTo = Calendar.getInstance()
            calTo.time = SimpleDateFormat("HH:mm").parse(to)
            if(calFrom >= calTo){
                et_to?.error = "End time must be later than start time"
                h_et_to?.error = "End time must be later than start time"
            }
        }
        if(location.trim().isEmpty()){
            et_location?.error = "Location cannot be empty"
            h_et_location?.error = "Location cannot be empty"
        }

        if(advertisementSkills.isEmpty()){
            return false
        }
        if(et_title?.error != null || et_description?.error != null || et_date?.error != null || et_from?.error != null || et_to?.error != null || et_location?.error != null ||
            h_et_title?.error != null || h_et_description?.error != null || h_et_date?.error != null || h_et_to?.error != null || h_et_to?.error != null || h_et_location?.error != null){
            return false
        }
        return true
    }

    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val formattedDate = SimpleDateFormat("EEE, d MMM yyyy").format(cal.time)
            date = formattedDate
            et_date?.text = formattedDate
            et_date?.error = null
            h_et_date?.text = formattedDate
            h_et_date?.error = null
        }

    val fromTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val calFrom = Calendar.getInstance()

            calFrom.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calFrom.set(Calendar.MINUTE, minute)

            val formattedTime = SimpleDateFormat("HH:mm").format(calFrom.time)
            from = formattedTime
            et_from?.text = formattedTime
            et_from?.error = null
            h_et_from?.text = formattedTime
            h_et_from?.error = null

            if(to.isNotEmpty()) {
                val calTo = Calendar.getInstance()
                calTo.time = SimpleDateFormat("HH:mm").parse(to)
                calFrom.time = SimpleDateFormat("HH:mm").parse(from)
                //if FROM later than TO
                if(calFrom >= calTo){
                    to = formattedTime
                    et_to?.text = formattedTime
                    et_to?.error = null
                    h_et_to?.text = formattedTime
                    h_et_to?.error = null
                }
            }
        }

    val toTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        val calTo = Calendar.getInstance()

        calTo.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calTo.set(Calendar.MINUTE, minute)

        val formattedDate = SimpleDateFormat("HH:mm").format(calTo.time)
        to = formattedDate
        et_to?.text = formattedDate
        et_to?.error = null
        h_et_to?.text = formattedDate
        h_et_to?.error = null

        if(from.isNotEmpty()) {
            val calFrom = Calendar.getInstance()
            calTo.time = SimpleDateFormat("HH:mm").parse(to)
            calFrom.time = SimpleDateFormat("HH:mm").parse(from)
            //if TO earlier than FROM
            if(calFrom >= calTo){
                from = formattedDate
                et_from?.text = formattedDate
                et_from?.error = null
                h_et_from?.text = formattedDate
                h_et_from?.error = null
            }
        }
    }

    fun saveData(){
        if(!cancelOperation){
            val advertisement = Advertisement().also {
                it.title = title.capitalized()
                it.description = description.capitalized()
                it.date = date
                it.from = from
                it.to = to
                it.location = location.capitalized()
                it.skills = advertisementSkills
            }
            if(operationType=="edit_time_slot"){
                advertisement.id = advertisementID
                vm.updateAdvertisement(advertisement)
                Toast.makeText(requireContext(), "Advertisement correctly edited", Toast.LENGTH_SHORT).show()
            }
            else if(operationType=="add_time_slot"){
                vm.addAdvertisement(advertisement)
                Toast.makeText(requireContext(), "Advertisement posted", Toast.LENGTH_SHORT).show()
            }
            return
        }

        Toast.makeText(requireContext(), "Changes discarded", Toast.LENGTH_SHORT).show()
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun String.capitalized(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else it.toString()
        }
    }

}