package it.polito.ma.g14.timebank.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
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
import org.w3c.dom.Text

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeLayoutAdSkills)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewSkillAdvertisementList)
        val emptyRv = view.findViewById<TextView>(R.id.textView66)
        val subtitleName = view.findViewById<TextView>(R.id.textView82)
        val name = view.findViewById<TextView>(R.id.textView81)
        val closeButton = view.findViewById<Button>(R.id.button3)
        val welcomeCard = view.findViewById<CardView>(R.id.Welcome_Card)
        name.text = "Hello Customer!"

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

        vm.profile.observe(viewLifecycleOwner){
            name.text = "Hello "+it.fullname + "!"
        }

        vm.skills.observe(viewLifecycleOwner) { it ->
            if(it.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
                subtitleName.text = "There are currently no announcements available"
            }
            else {
                rv.isVisible = true
                emptyRv.isGone = true
                subtitleName.text = "Click on the ad for the skill you prefer"
                val cmp = compareBy<SkillAdvertisement> { it.skill }
                adapter.updateSkillAdvertisements(it.sortedWith(cmp))
            }
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.setOnRefreshListener {
            vm.updateAdvertisementSkillsList()
        }


        closeButton.setOnClickListener {
            welcomeCard.isVisible = false
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