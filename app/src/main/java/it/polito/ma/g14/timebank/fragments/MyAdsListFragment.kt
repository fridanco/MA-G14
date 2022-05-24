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
import it.polito.ma.g14.timebank.RVadapters.MyAdvertisementsAdapter
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.MyAdsListVM
import it.polito.ma.g14.timebank.utils.Utils

class MyAdsListFragment : Fragment() {


    val vm by viewModels<FirebaseVM>()
    val myAdsListVM by viewModels<MyAdsListVM>()

    lateinit var adapter: MyAdvertisementsAdapter

    var operationType: String = ""
    var selectedSkill: String = ""

    var sortByKey = "date_desc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_ads_list, container, false)

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
        adapter = MyAdvertisementsAdapter(view, vm, requireContext())
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter


        vm.myAdvertisements.observe(viewLifecycleOwner) { it ->
            if(it.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
            }
            else {
                rv.isVisible = true
                emptyRv.isGone = true
//                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
//                val sdf_time = SimpleDateFormat("HH:mm")
//                val cmp = compareBy<Advertisement> { sdf_date.parse(it.date) }.thenByDescending { sdf_time.parse(it.from) }
                val sortBy = myAdsListVM.getSortBy()
                adapter.updateAdvertisements(it, sortBy)
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab?.let {
            it.setOnClickListener {
                val bundle = bundleOf("timeSlotID" to 0, "operationType" to "add_time_slot")
                view.findNavController()
                    .navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    fun sortAdvertisements(sortBy: String){
        sortByKey = sortBy
        myAdsListVM.setSortBy(sortBy)
        adapter.addSort(sortBy)
    }


    fun searchAdvertisements(query: String){
        if(adapter.addFilter(query)==0){
            view?.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)?.isGone = true
            view?.findViewById<TextView>(R.id.textView60)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView60)?.text = "No advertisements matche your search"
        }
        else{
            view?.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView60)?.isGone = true
            view?.findViewById<TextView>(R.id.textView60)?.text = "You have not posted any advertisment yet"
        }
    }
}