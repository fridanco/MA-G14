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
import it.polito.ma.g14.timebank.fragments.ChooseSkillsFragment
import it.polito.ma.g14.timebank.fragments.EditProfileFragment
import it.polito.ma.g14.timebank.fragments.TimeSlotDetailsFragment
import it.polito.ma.g14.timebank.fragments.TimeSlotEditFragment

class Utils {

    companion object ActionBarUtils {

        fun manageActionBarItemsVisibility(activity: Activity, menu: Menu) {
            val navController = activity.findNavController(R.id.nav_host_fragment_content_main)
            val currDestinationID = navController.currentDestination?.id
            when (currDestinationID) {
                R.id.skillAdvertisementListFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                }
                R.id.timeSlotListFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                }
                R.id.timeSlotEditFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = true
//                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
//                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotEditFragment
//                    menu.findItem(R.id.app_bar_add).isVisible = fragment.operationType=="add_time_slot"
                }
                R.id.timeSlotDetailsFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = true
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                }
                R.id.nav_profile -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                }
                R.id.edit_profile -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = true
                }
                R.id.chooseSkillsFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = false
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
                            if(fragment.operationType=="edit_time_slot") {
                                if(fragment.originFragment=="list_time_slot"){
                                    navController.popBackStack(R.id.timeSlotListFragment, false)
                                }
                                else{
                                    navController.popBackStack(R.id.timeSlotDetailsFragment, false)
                                }
                            }
                            else{
                                navController.popBackStack(R.id.timeSlotListFragment, false)
                            }
                        }
                        R.id.app_bar_add -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotEditFragment
                            if(fragment.isFormValid()) {
                                navController.popBackStack(R.id.timeSlotListFragment, false)
                            }
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
                            navController.popBackStack(R.id.timeSlotListFragment, false)
                        }
                    }
                }
                R.id.nav_profile -> {
                    when(item.itemId){
                        R.id.app_bar_pencil -> {
                            val bundle = bundleOf("performProfileBackup" to true, "performSkillsBackup" to true)
                            navController.navigate(R.id.action_nav_profile_to_edit_profile, bundle)
                        }
                    }
                }
                R.id.edit_profile -> {
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                            fragment.cancelOperation = true
                            navController.popBackStack(R.id.nav_profile, false)
                        }
                        R.id.app_bar_add -> {
                            navController.popBackStack(R.id.nav_profile, false)
                        }
                    }
                }
                R.id.chooseSkillsFragment -> {
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChooseSkillsFragment
                            fragment.cancelOperation = true
                            navController.popBackStack(R.id.edit_profile, false)
                        }
                    }
                }
            }
        }
    }

    fun manageActionBarNavigateUp(activity: Activity){

    }

}