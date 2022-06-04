package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.utils.Utils


class MyAdsLinkedFragment : Fragment() {

     lateinit var tabLayoutAds : TabLayout
     lateinit var viewPager : ViewPager2



    lateinit var pagerAdapter: FragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_ads_linked, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayoutAds = view.findViewById(R.id.adsTabLayout)
        viewPager = view.findViewById(R.id.adsViewPager)
        pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

//        tabLayoutAds.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//               when(tab?.id){
//                   R.id.tabFollowedAds -> viewPager.currentItem = 0
//                   R.id.tabReceivedAds -> viewPager.currentItem = 1
//               }
//            }
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//
//            }
//
//        })
        val tabText = listOf("Booked","Received")
        TabLayoutMediator(tabLayoutAds,viewPager){tab,position->
            tab.text = tabText[position]
        }.attach()




    }
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> FollowedAdsFragment()
                else -> MyCompletedAdsFragment()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

}