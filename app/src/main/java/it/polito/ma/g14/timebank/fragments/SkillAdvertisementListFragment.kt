package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.SkillAdvertisement
import it.polito.ma.g14.timebank.utils.Utils

class SkillAdvertisementListFragment : Fragment() {


    val vm by viewModels<FirebaseVM>()

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var adapter : SkillAdvertisementAdapter

    var sortByKey = "skill_asc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm.updateAdvertisementSkillsList()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_skill_advertisement_list, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeLayoutAdSkills)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewSkillAdvertisementList)
        val emptyRv = view.findViewById<TextView>(R.id.textView66)

        //noinspection ResourceType
        val colorList = listOf<String>(
            resources.getString(R.color.purple),
            resources.getString(R.color.orange),
            resources.getString(R.color.red),
            resources.getString(R.color.green),
            resources.getString(R.color.eletric_blue),
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SkillAdvertisementAdapter(view, requireContext(), sortByKey)
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter

        vm.skills.observe(viewLifecycleOwner) { it ->
            if(it.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
            }
            else {
                rv.isVisible = true
                emptyRv.isGone = true
                val cmp = compareBy<SkillAdvertisement> { it.skill }
                adapter.updateSkillAdvertisements(it.sortedWith(cmp))
            }
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.setOnRefreshListener {
            vm.updateAdvertisementSkillsList()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    fun updateAdsSkillsList(){
        vm.updateAdvertisementSkillsList()
    }

    fun sortAdvertisements(sortBy: String){
        sortByKey = sortBy
        adapter.addSort(sortBy)
    }


    fun searchSkills(query: String){
        adapter.addFilter(query)
    }

}