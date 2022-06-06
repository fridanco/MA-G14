package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    val myMessagesVM by viewModels<MyMessagesVM>()

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

        val tabText = listOf("My ads", "My bookings")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabText[position]
        }.attach()

        myMessagesVM.sortBy.observe(viewLifecycleOwner){ sortBy ->
            println(viewPager.currentItem)
            if(viewPager.currentItem < 0){
                println("No index in ViewPager2")
                return@observe
            }

            val receivedMessagesFragment = requireActivity().supportFragmentManager.findFragmentByTag("f0")
            receivedMessagesFragment?.let {
                (it as MyReceivedMessagesFragment).receivedMessagesVM.setSortBy(sortBy)
            }
            val sentMessagesFragment = requireActivity().supportFragmentManager.findFragmentByTag("f1")
            sentMessagesFragment?.let {
                (it as MySentMessagesFragment).sentMessagesVM.setSortBy(sortBy)
            }
        }

        myMessagesVM.filterBy.observe(viewLifecycleOwner){ filterBy ->
            if(viewPager.currentItem < 0){
                println("No index in ViewPager2")
                return@observe
            }
            val receivedMessagesFragment = requireActivity().supportFragmentManager.findFragmentByTag("f0")
            receivedMessagesFragment?.let {
                (it as MyReceivedMessagesFragment).receivedMessagesVM.setFilterBy(filterBy)
            }
            val sentMessagesFragment = requireActivity().supportFragmentManager.findFragmentByTag("f1")
            sentMessagesFragment?.let {
                (it as MySentMessagesFragment).sentMessagesVM.setFilterBy(filterBy)
            }
        }
    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        val fragmentList = listOf<Fragment>(
            MyReceivedMessagesFragment(myMessagesVM.getSortBy(), myMessagesVM.getFilterBy()),
            MySentMessagesFragment(myMessagesVM.getSortBy(), myMessagesVM.getFilterBy())
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    fun sortMessages(sortKey: String){
        myMessagesVM.setSortBy(sortKey)
    }

    fun addFilter(filterBy: String){
        myMessagesVM.setFilterBy(filterBy)
    }

}