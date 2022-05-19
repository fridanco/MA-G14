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
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.SkillAdvertisement
import it.polito.ma.g14.timebank.utils.Utils

class SkillAdvertisementListFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_skill_advertisement_list, container, false)

        requireActivity().invalidateOptionsMenu()

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
        val adapter = SkillAdvertisementAdapter(view)
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
        }

        return view
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

}