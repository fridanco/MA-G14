package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import it.polito.ma.g14.timebank.R
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass.
 * Use the [TimeSlotDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeSlotDetailsFragment(val timeSlotID : Long) : Fragment() {

    val vm by viewModels<TimeSlotVM>()

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

        val tv_title = view?.findViewById<TextView>(R.id.textView4)
        val tv_description = view?.findViewById<TextView>(R.id.textView5)
        val tv_dateTime = view?.findViewById<TextView>(R.id.textView6)
        val tv_duration = view?.findViewById<TextView>(R.id.textView7)
        val tv_location = view?.findViewById<TextView>(R.id.textView19)

        vm.getTimeSlot(timeSlotID).observe(viewLifecycleOwner) {
            tv_title?.text = it.title
            tv_description?.text = it.description
            tv_dateTime?.text = it.dateTime.toString()
            tv_duration?.text = it.duration.toString()
            tv_location.text = it.location
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val pencilItem = menu.findItem(R.id.app_bar_pencil)
        pencilItem.isVisible = true
    }
}