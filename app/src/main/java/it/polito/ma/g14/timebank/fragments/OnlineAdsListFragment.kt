package it.polito.ma.g14.timebank.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.OnlineAdvertisementsAdapter
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.OnlineAdsListVM
import it.polito.ma.g14.timebank.utils.Utils


class OnlineAdsListFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()
    val onlineAdsListVM by viewModels<OnlineAdsListVM>()

    lateinit var adapter: OnlineAdvertisementsAdapter

    var selectedSkill: String = ""

    var sortByKey = "date_desc"

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm.updateAdvertisementList()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_online_ads_list, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedSkill = requireArguments().getString("selectedSkill").toString()

        swipeRefreshLayout = view.findViewById(R.id.swipeLayoutAds)

        val rv = view.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)
        val emptyRv = view.findViewById<TextView>(R.id.textView60)

        //noinspection ResourceType
        val colorList = listOf(
            resources.getString(R.color.purple),
            resources.getString(R.color.orange),
            resources.getString(R.color.red),
            resources.getString(R.color.green),
            resources.getString(R.color.eletric_blue),
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = OnlineAdvertisementsAdapter(view, vm, requireContext(), "online", selectedSkill)
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter

        vm.onlineAdvertisements.observe(viewLifecycleOwner){ advertisementsMap ->
            val ads: List<Advertisement> = advertisementsMap[selectedSkill] ?: listOf()
            if(ads.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
            }
            else {
                rv.isVisible = true
                emptyRv.isGone = true
//                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
//                val sdf_time = SimpleDateFormat("HH:mm")
//                val cmp = compareBy<Advertisement> { sdf_date.parse(it.date) }.thenByDescending { sdf_time.parse(it.from) }
                val sortBy = onlineAdsListVM.getSortBy()
                adapter.updateAdvertisements(ads, sortBy)
            }
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.setOnRefreshListener {
            vm.updateAdvertisementList()
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }


    fun updateAdsList(){
        vm.updateAdvertisementList()
    }

    fun sortAdvertisements(sortBy: String){
        sortByKey = sortBy
        onlineAdsListVM.setSortBy(sortBy)
        adapter.addSort(sortBy)
    }

    fun searchAdvertisements(query: String){
        if(adapter.addFilter(query)==0){
            view?.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)?.isGone = true
            view?.findViewById<TextView>(R.id.textView60)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView60)?.text = "No advertisements match your search"
        }
        else{
            view?.findViewById<RecyclerView>(R.id.timeSlotRecyclerView)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView60)?.isGone = true
            view?.findViewById<TextView>(R.id.textView60)?.text = "There are no advertisements in this category"
        }
    }

}