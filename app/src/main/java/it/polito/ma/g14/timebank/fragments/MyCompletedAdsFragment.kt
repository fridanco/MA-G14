package it.polito.ma.g14.timebank.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.OnlineAdvertisementsAdapter
import it.polito.ma.g14.timebank.models.FirebaseVM


class MyCompletedAdsFragment : Fragment() {

   val vm by viewModels<FirebaseVM>()

    lateinit var  adapter: OnlineAdvertisementsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_completed_ads, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorList = listOf<String>(
            resources.getString(R.color.purple),
            resources.getString(R.color.orange),
            resources.getString(R.color.red),
            resources.getString(R.color.green),
            resources.getString(R.color.eletric_blue),
        )
        val rv = view.findViewById<RecyclerView>(R.id.rv_completedAds)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = OnlineAdvertisementsAdapter(view,vm,requireContext(),"linked")
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter

        vm.completedAdvertisements.observe (viewLifecycleOwner){  completedAdsList->
            if(completedAdsList.isEmpty()){
                rv.isGone = true
            }
            else{
                rv.isVisible = true
                adapter.updateAdvertisements(completedAdsList,"data_desc")
            }
        }

    }


}