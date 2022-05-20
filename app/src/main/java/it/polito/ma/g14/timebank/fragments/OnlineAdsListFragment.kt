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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
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
        adapter = OnlineAdvertisementsAdapter(view, vm)
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter


        selectedSkill = requireArguments().getString("selectedSkill").toString()

        vm.onlineAdvertisement.observe(viewLifecycleOwner){ advertisementsMap ->
            val loggedInUserUID = Firebase.auth.currentUser!!.uid
            val ads: List<Advertisement> = advertisementsMap[selectedSkill]?.filter { it.uid!=loggedInUserUID } ?: listOf()
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
        }


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }
}