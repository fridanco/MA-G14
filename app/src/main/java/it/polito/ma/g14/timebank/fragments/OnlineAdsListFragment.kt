package it.polito.ma.g14.timebank.fragments


import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
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
import it.polito.ma.g14.timebank.utils.Utils
import java.text.SimpleDateFormat


class OnlineAdsListFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()

    lateinit var adapter: OnlineAdvertisementsAdapter

    var selectedSkill: String = ""

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_online_ads_list, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeLayoutAds)

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
        adapter = OnlineAdvertisementsAdapter(view, vm, requireContext())
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter


        selectedSkill = requireArguments().getString("selectedSkill").toString()

        vm.onlineAdvertisements.observe(viewLifecycleOwner){ advertisementsMap ->
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
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.setOnRefreshListener {
            vm.updateAdvertisementList()
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

//        val sortMenuItem: MenuItem = menu.findItem(R.id.app_bar_sort)
//
//        sortMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
//                sortMenuItem.isVisible = false
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
//                sortMenuItem.isVisible = true
//                return true
//            }
//        })

        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return when (id) {
            0 -> run {
                Toast.makeText(requireContext(), "Item 1 Selected", Toast.LENGTH_LONG).show()
                return true
            }
            1 -> run {
                Toast.makeText(requireContext(), "Item 2 Selected", Toast.LENGTH_LONG).show()
                return true
            }
            2 -> run {
                Toast.makeText(requireContext(), "Item 3 Selected", Toast.LENGTH_LONG).show()
                return true
            }
            else -> false
        }
    }

    fun updateAdsList(){
        vm.updateAdvertisementList()
    }

}