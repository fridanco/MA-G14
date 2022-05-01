package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.utils.Utils


class TimeSlotDetailsFragment() : Fragment() {

    val vm by viewModels<TimeSlotVM>()

    var timeSlotID : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_slot_details, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeSlotID = requireArguments().getLong("timeSlotID")

        val tv_title = view.findViewById<TextView>(R.id.textView4)
        val tv_description = view.findViewById<TextView>(R.id.textView5)
        val tv_date = view.findViewById<TextView>(R.id.textView64)
        val tv_from = view.findViewById<TextView>(R.id.textView62)
        val tv_to = view.findViewById<TextView>(R.id.textView63)
        val tv_location = view.findViewById<TextView>(R.id.textView19)

        vm.getTimeSlot(timeSlotID).observe(viewLifecycleOwner) {
            tv_title.text = it.title
            tv_description.text = it.description
            tv_date.text = it.date
            tv_from.text = it.from
            tv_to.text = it.to
            tv_location.text = it.location
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    fun deleteTimeSlot() {
        vm.deleteTimeSlot(timeSlotID)
    }
}