package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.SkillAdapter
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.ProfileSkillsVM
import it.polito.ma.g14.timebank.utils.SkillList
import it.polito.ma.g14.timebank.utils.Utils

class ChooseSkillsFragment : Fragment() {

    val skillsVM by viewModels<ProfileSkillsVM>()

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
        val view = inflater.inflate(R.layout.fragment_choose_skills, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val skills = arguments?.getStringArrayList("skills")
        if(skills!=null && skills.isNotEmpty()){
            skillsVM.setSkillsFromArgument(skills.toList())
        }

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

            skillsVM.profileSkills.observe(viewLifecycleOwner){
                vmSkills = it
                adapter.updateSelectedSkills(it.toMutableList())
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    override fun onDestroy() {
        skillsVM.setSkills(adapter.checked_skills)
        super.onDestroy()
    }

    fun searchSkills(query: String){
        adapter.addFilter(query)
    }

    fun saveData(){
        if(cancelOperation){
            Toast.makeText(requireContext(), "Changes discarded", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Skills updated", Toast.LENGTH_SHORT).show()
    }

}