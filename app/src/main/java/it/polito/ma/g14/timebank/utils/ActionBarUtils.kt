package it.polito.ma.g14.timebank.utils

import android.app.Activity
import android.view.Menu
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.fragments.*

class Utils {

    companion object ActionBarUtils {

        fun manageActionBarItemsVisibility(activity: Activity, menu: Menu) {
            val navController = activity.findNavController(R.id.nav_host_fragment_content_main)
            val currDestinationID = navController.currentDestination?.id
            when (currDestinationID) {
                R.id.advertisement_skills -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = true
                }
                R.id.advertisements -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = true
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.timeSlotEditFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = true
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
//                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
//                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotEditFragment
//                    menu.findItem(R.id.app_bar_add).isVisible = fragment.operationType=="add_time_slot"
                }
                R.id.timeSlotDetailsFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = true
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.onlineAdsListFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = true
                    menu.findItem(R.id.app_bar_refresh).isVisible = true
                }
                R.id.onlineAdDetailsFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.nav_profile -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ShowProfileFragment
                    menu.findItem(R.id.app_bar_pencil).isVisible = fragment.isImageDownloaded
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.edit_profile -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = true
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.chooseSkillsFragment -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
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
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                            fragment.cancelOperation = true
                            if(fragment.operationType=="edit_time_slot") {
                                if(fragment.originFragment=="list_time_slot"){
                                    navController.popBackStack(R.id.advertisements, false)
                                }
                                else{
                                    navController.popBackStack(R.id.timeSlotDetailsFragment, false)
                                }
                            }
                            else{
                                navController.popBackStack(R.id.advertisements, false)
                            }
                        }
                        R.id.app_bar_add -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                            if(fragment.isFormValid()) {
                                navController.popBackStack(R.id.advertisements, false)
                            }
                        }
                    }
                }
                R.id.timeSlotDetailsFragment -> {
                    when(item.itemId){
                        R.id.app_bar_pencil -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdDetailsFragment
                            val bundle = bundleOf("advertisementID" to fragment.advertisementID, "operationType" to "edit_time_slot")
                            navController.navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment, bundle)
                        }
                        R.id.app_bar_delete -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdDetailsFragment
                            fragment.deleteAdvertisement()
                            navController.popBackStack(R.id.advertisements, false)
                        }
                    }
                }
                R.id.onlineAdDetailsFragment -> {
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            navController.popBackStack(R.id.onlineAdsListFragment, false)
                        }
                    }
                }
                R.id.nav_profile -> {
                    when(item.itemId){
                        R.id.app_bar_pencil -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ShowProfileFragment
                            val profileBackup = fragment.performProfileBackup()
                            val bundle = bundleOf("profileBackup" to profileBackup)
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
                R.id.advertisement_skills -> {
                    when(item.itemId){
                        R.id.app_bar_refresh -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as SkillAdvertisementListFragment
                            fragment.updateAdsSkillsList()
                            fragment.swipeRefreshLayout.isRefreshing = true
                        }
                    }
                }
                R.id.onlineAdsListFragment -> {
                    val navHostFragment =
                        (activity as FragmentActivity).supportFragmentManager.findFragmentById(
                            R.id.nav_host_fragment_content_main
                        ) as NavHostFragment?
                    val fragment =
                        navHostFragment!!.childFragmentManager.fragments[0] as OnlineAdsListFragment
                    when(item.itemId) {
                        R.id.app_bar_refresh -> {
                            fragment.updateAdsList()
                            fragment.swipeRefreshLayout.isRefreshing = true
                        }
                        R.id.id0 -> {
                            fragment.sortAdvertisements("title")
                        }
                        R.id.id1 -> {
                            fragment.sortAdvertisements("creator")
                        }
                        R.id.id2 -> {
                            fragment.sortAdvertisements("date")
                        }
                    }
                }
            }
        }
    }

    fun manageActionBarNavigateUp(activity: Activity){

    }

}