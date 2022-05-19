package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.utils.Utils
import java.text.SimpleDateFormat

class TimeSlotListFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()

    lateinit var adapter: AdvertisementAdapter

    var operationType: String = ""
    var selectedSkill: String = ""

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        operationType = requireArguments().getString("operationType").toString()

        val rv = view.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)
        val emptyRv = view.findViewById<TextView>(R.id.textView60)

        //noinspection ResourceType
        val colorList = listOf<String>(
            resources.getString(R.color.purple),
            resources.getString(R.color.orange),
            resources.getString(R.color.red),
            resources.getString(R.color.green),
            resources.getString(R.color.eletric_blue),
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AdvertisementAdapter(view, vm)
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter


        if(operationType=="online_advertisements"){
            selectedSkill = requireArguments().getString("selectedSkill").toString()
            vm.onlineAdvertisement.observe(viewLifecycleOwner){ advertisementsMap ->
                val ads: List<Advertisement> = advertisementsMap[selectedSkill] ?: listOf()
                if(ads.isEmpty()){
                    rv.isGone = true
                    emptyRv.isVisible = true
                }
                else {
                    rv.isVisible = true
                    emptyRv.isGone = true
                    val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                    val sdf_time = SimpleDateFormat("HH:mm")
                    val cmp = compareBy<Advertisement> { sdf_date.parse(it.date) }.thenByDescending { sdf_time.parse(it.from) }
                    adapter.updateAdvertisements(ads.sortedWith(cmp))
                }
            }
        }
        else{
            vm.myAdvertisements.observe(viewLifecycleOwner) { it ->
                if(it.isEmpty()){
                    rv.isGone = true
                    emptyRv.isVisible = true
                }
                else {
                    rv.isVisible = true
                    emptyRv.isGone = true
                    val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                    val sdf_time = SimpleDateFormat("HH:mm")
                    val cmp = compareBy<Advertisement> { sdf_date.parse(it.date) }.thenByDescending { sdf_time.parse(it.from) }
                    adapter.updateAdvertisements(it.sortedWith(cmp))
                }
            }
        }

        if(operationType=="my_advertisements") {
            val fab = view?.findViewById<FloatingActionButton>(R.id.floatingActionButton)
            fab?.let {
                it.setOnClickListener {
                    val bundle = bundleOf("timeSlotID" to 0, "operationType" to "add_time_slot")
                    view.findNavController()
                        .navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }
}