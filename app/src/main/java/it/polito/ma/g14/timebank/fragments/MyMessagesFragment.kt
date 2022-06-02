package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.MyMessagesVM
import it.polito.ma.g14.timebank.utils.Utils

class MyMessagesFragment : Fragment() {

    lateinit var tabLayout : TabLayout
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
        val view = inflater.inflate(R.layout.fragment_my_messages, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.msgTabLayout)

        viewPager = view.findViewById(R.id.msgViewPager)
        pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

//        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when(tab?.id){
//                    R.id.tabReceivedMsg -> viewPager.currentItem = 0
//                    R.id.tabSentMsg -> viewPager.currentItem = 1
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//
//            }
//        })

        val tabText = listOf("Received", "Sent")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabText[position]
        }.attach()

    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> MyReceivedMessagesFragment()
                else -> MySentMessagesFragment()
            }
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

}