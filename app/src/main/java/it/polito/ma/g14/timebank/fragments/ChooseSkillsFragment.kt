package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Skill

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseSkillsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseSkillsFragment : Fragment() {

    val vm by viewModels<ProfileVM>()

    var searchText : String = ""

    val skillList = SkillList().skill_list

    lateinit var adapter: SkillAdapter

    var cancelOperation = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewSkills)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SkillAdapter(skillList,vm)
        rv.adapter = adapter
        vm.skills.observe(viewLifecycleOwner){
            adapter.updateSelectedSkills(it.map { it.skill } as MutableList<String>)
        }

        val searchView = view.findViewById<SearchView>(R.id.searchBar)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                if (qString == "") {
                    onQueryTextSubmit("")
                }
                searchText = qString
                return true
            }
            override fun onQueryTextSubmit(qString: String): Boolean {
                adapter.addFilter(qString)
                view.findViewById<SearchView>(R.id.searchBar)?.clearFocus()
                return true
            }

        })
    }

}