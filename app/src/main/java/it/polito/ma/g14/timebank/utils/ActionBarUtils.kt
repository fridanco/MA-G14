package it.polito.ma.g14.timebank.utils

import android.app.Activity
import android.content.ClipData
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.fragments.EditProfileFragment
import it.polito.ma.g14.timebank.fragments.TimeSlotDetailsFragment
import it.polito.ma.g14.timebank.fragments.TimeSlotEditFragment

class Utils {

    companion object ActionBarUtils {
        fun manageActionBarItemsVisibility(activity: Activity, menu: Menu) {
            val navController = activity.findNavController(R.id.nav_host_fragment_content_main)
            val currDestinationID = navController.currentDestination?.id
            when (currDestinationID) {
                R.id.timeSlotListFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                }
                R.id.timeSlotEditFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                }
                R.id.timeSlotDetailsFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = true
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                }
                R.id.nav_profile -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                }
                R.id.edit_profile -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                }
            }
        }

        fun manageActionBarItemActions(activity: Activity, item: MenuItem){
            val navController = activity.findNavController(R.id.nav_host_fragment_content_main)
            val currDestinationID = navController.currentDestination?.id
            when(currDestinationID){
                R.id.timeSlotEditFragment -> {
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotEditFragment
                            fragment.cancelOperation = true
                            val bundle = bundleOf("timeSlotID" to fragment.timeSlotID)
                            navController.navigate(R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment, bundle)
                        }
                    }
                }
                R.id.timeSlotDetailsFragment -> {
                    when(item.itemId){
                        R.id.app_bar_pencil -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotDetailsFragment
                            val bundle = bundleOf("timeSlotID" to fragment.timeSlotID, "operationType" to "edit_time_slot")
                            navController.navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment, bundle)
                        }
                        R.id.app_bar_delete -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotDetailsFragment
                            fragment.deleteTimeSlot()
                            navController.navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotListFragment)
                        }
                    }
                }
                R.id.nav_profile -> {
                    when(item.itemId){
                        R.id.app_bar_pencil -> {
                            navController.navigate(R.id.action_nav_profile_to_edit_profile)
                        }
                    }
                }
                R.id.edit_profile -> {
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                            fragment.cancelOperation = true
                            navController.navigate(R.id.action_edit_profile_to_nav_profile)
                        }
                    }
                }
            }
        }
    }

}