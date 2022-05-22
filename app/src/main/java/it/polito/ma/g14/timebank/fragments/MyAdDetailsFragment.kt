package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.utils.Utils


class MyAdDetailsFragment() : Fragment() {

    val vm by viewModels<FirebaseVM>()

    lateinit var tv_title : TextView
    lateinit var tv_description : TextView
    lateinit var tv_date : TextView
    lateinit var tv_from : TextView
    lateinit var tv_to : TextView
    lateinit var tv_location : TextView
    lateinit var skillContainer : ChipGroup

    var advertisementID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_ad_details, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        advertisementID = requireArguments().getString("advertisementID").toString()

        tv_title = view.findViewById<TextView>(R.id.textView4)
        tv_description = view.findViewById<TextView>(R.id.textView5)
        tv_date = view.findViewById<TextView>(R.id.textView64)
        tv_from = view.findViewById<TextView>(R.id.textView62)
        tv_to = view.findViewById<TextView>(R.id.textView63)
        tv_location = view.findViewById<TextView>(R.id.textView19)
        skillContainer = view.findViewById<ChipGroup>(R.id.chipGroupMyAd)

        vm.myAdvertisements.observe(viewLifecycleOwner){ myAdvertisements ->
            val ad = myAdvertisements.find { it.id==advertisementID }
            ad?.let {
                tv_title.text = it.title
                tv_description.text = it.description
                tv_date.text = it.date
                tv_from.text = it.from
                tv_to.text = it.to
                tv_location.text = it.location

                skillContainer.removeAllViews()
                it.skills.forEach { skillName ->
                    val inflater: LayoutInflater = layoutInflater
                    val skill: Chip = inflater.inflate(R.layout.skill_chip, null) as Chip
                    skill.text = skillName
                    skill.isCloseIconVisible = false
                    skillContainer.addView(skill)
                }

            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    fun deleteAdvertisement() {
        vm.deleteAdvertisement(advertisementID)
    }
}