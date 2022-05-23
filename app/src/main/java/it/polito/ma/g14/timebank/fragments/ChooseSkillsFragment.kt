package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.SkillAdapter
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.utils.SkillList
import it.polito.ma.g14.timebank.utils.Utils

class ChooseSkillsFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()

    var searchText : String = ""

    val skillList = SkillList().skill_list
    var vmSkills = listOf<String>()

    lateinit var adapter: SkillAdapter

    var cancelOperation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().invalidateOptionsMenu()

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewSkills)
        val emptyRv = view.findViewById<TextView>(R.id.textView59)

        if(skillList.isEmpty()){
            rv.isGone = true
            emptyRv.isVisible = true
        }
        else {
            emptyRv.isGone = true
            rv.isVisible = true
            rv.layoutManager = LinearLayoutManager(requireContext())
            adapter = SkillAdapter(skillList)
            rv.adapter = adapter

            vm.profile.observe(viewLifecycleOwner){
                vmSkills = it.skills
                adapter.updateSelectedSkills(it.skills as MutableList<String>)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    override fun onDestroy() {
        if(cancelOperation){
            super.onDestroy()
            return
        }

        vm.updateProfileSkills(adapter.checked_skills)

        Toast.makeText(requireContext(), "Skills updated", Toast.LENGTH_SHORT).show()

        super.onDestroy()
    }

    fun searchSkills(query: String){
        adapter.addFilter(query)
    }

}