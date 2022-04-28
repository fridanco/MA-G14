package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.TimeSlotAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [TimeSlotListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeSlotListFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)

        requireActivity().invalidateOptionsMenu()

        val rv = view?.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)
        rv?.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            val adapter = TimeSlotAdapter()
            it.adapter = adapter

            vm.timeSlots.observe(viewLifecycleOwner) {
                adapter.updateTimeSlots(it)
            }
        }

        val fab = view?.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab?.let {
            it.setOnClickListener {
                val bundle = bundleOf("timeSlotID" to 0, "operationType" to "add_time_slot")
                view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
            }
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val pencilItem = menu.findItem(R.id.app_bar_pencil)
        pencilItem.isVisible = false
    }
}