package it.polito.ma.g14.timebank.utils

import android.app.Activity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            when (navController.currentDestination?.id) {
                R.id.onlineAdvertisementSkills -> {
                    menu.findItem(R.id.app_bar_search).isVisible = true
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = true
                    menu.findItem(R.id.app_bar_refresh).isVisible = true
                    
                    menu.findItem(R.id.title_asc).isVisible = false
                    menu.findItem(R.id.title_desc).isVisible = false
                    menu.findItem(R.id.creator_asc).isVisible = false
                    menu.findItem(R.id.creator_desc).isVisible = false
                    menu.findItem(R.id.location_asc).isVisible = false
                    menu.findItem(R.id.location_desc).isVisible = false
                    menu.findItem(R.id.date_asc).isVisible = false
                    menu.findItem(R.id.date_desc).isVisible = false
                    menu.findItem(R.id.skill_asc).isVisible = true
                    menu.findItem(R.id.skill_desc).isVisible = true
                    menu.findItem(R.id.numAd_asc).isVisible = true
                    menu.findItem(R.id.numAd_desc).isVisible = true
                    
                    menu.findItem(R.id.myMsg_msg_desc).isVisible = false
                    menu.findItem(R.id.myMsg_title_asc).isVisible = false
                    menu.findItem(R.id.myMsg_title_desc).isVisible = false
                    menu.findItem(R.id.myMsg_creator_asc).isVisible = false
                    menu.findItem(R.id.myMsg_creator_desc).isVisible = false
                    menu.findItem(R.id.myMsg_date_asc).isVisible = false
                    menu.findItem(R.id.myMsg_date_desc).isVisible = false
                }
                R.id.myAdvertisements -> {
                    menu.findItem(R.id.app_bar_search).isVisible = true
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = true
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                    
                    menu.findItem(R.id.title_asc).isVisible = true
                    menu.findItem(R.id.title_desc).isVisible = true
                    menu.findItem(R.id.creator_asc).isVisible = false
                    menu.findItem(R.id.creator_desc).isVisible = false
                    menu.findItem(R.id.location_asc).isVisible = true
                    menu.findItem(R.id.location_desc).isVisible = true
                    menu.findItem(R.id.date_asc).isVisible = true
                    menu.findItem(R.id.date_desc).isVisible = true
                    menu.findItem(R.id.skill_asc).isVisible = false
                    menu.findItem(R.id.skill_desc).isVisible = false
                    menu.findItem(R.id.numAd_asc).isVisible = false
                    menu.findItem(R.id.numAd_desc).isVisible = false

                    menu.findItem(R.id.myMsg_msg_desc).isVisible = false
                    menu.findItem(R.id.myMsg_title_asc).isVisible = false
                    menu.findItem(R.id.myMsg_title_desc).isVisible = false
                    menu.findItem(R.id.myMsg_creator_asc).isVisible = false
                    menu.findItem(R.id.myMsg_creator_desc).isVisible = false
                    menu.findItem(R.id.myMsg_date_asc).isVisible = false
                    menu.findItem(R.id.myMsg_date_desc).isVisible = false
                }
                R.id.myAdvertisementEdit -> {
                    menu.findItem(R.id.app_bar_search).isVisible = false
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
                R.id.myAdvertisementDetails -> {
                    menu.findItem(R.id.app_bar_search).isVisible = false
                    menu.findItem(R.id.app_bar_pencil).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = true
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                    
                }
                R.id.onlineAdsListFragment -> {
                    menu.findItem(R.id.app_bar_search).isVisible = true
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = true
                    menu.findItem(R.id.app_bar_refresh).isVisible = true

                    menu.findItem(R.id.title_asc).isVisible = true
                    menu.findItem(R.id.title_desc).isVisible = true
                    menu.findItem(R.id.creator_asc).isVisible = true
                    menu.findItem(R.id.creator_desc).isVisible = true
                    menu.findItem(R.id.location_asc).isVisible = true
                    menu.findItem(R.id.location_desc).isVisible = true
                    menu.findItem(R.id.date_asc).isVisible = true
                    menu.findItem(R.id.date_desc).isVisible = true
                    menu.findItem(R.id.skill_asc).isVisible = false
                    menu.findItem(R.id.skill_desc).isVisible = false
                    menu.findItem(R.id.numAd_asc).isVisible = false
                    menu.findItem(R.id.numAd_desc).isVisible = false

                    menu.findItem(R.id.myMsg_msg_desc).isVisible = false
                    menu.findItem(R.id.myMsg_title_asc).isVisible = false
                    menu.findItem(R.id.myMsg_title_desc).isVisible = false
                    menu.findItem(R.id.myMsg_creator_asc).isVisible = false
                    menu.findItem(R.id.myMsg_creator_desc).isVisible = false
                    menu.findItem(R.id.myMsg_date_asc).isVisible = false
                    menu.findItem(R.id.myMsg_date_desc).isVisible = false
                }
                R.id.onlineAdDetailsFragment -> {
                    menu.findItem(R.id.app_bar_search).isVisible = false
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.chatFragment -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChatFragment
                    (activity as AppCompatActivity).supportActionBar!!.title = fragment.advertiser_name

                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_search).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.myMessages -> {
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_search).isVisible = true
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = true
                    menu.findItem(R.id.app_bar_refresh).isVisible = false

                    menu.findItem(R.id.title_asc).isVisible = false
                    menu.findItem(R.id.title_desc).isVisible = false
                    menu.findItem(R.id.creator_asc).isVisible = false
                    menu.findItem(R.id.creator_desc).isVisible = false
                    menu.findItem(R.id.location_asc).isVisible = false
                    menu.findItem(R.id.location_desc).isVisible = false
                    menu.findItem(R.id.date_asc).isVisible = false
                    menu.findItem(R.id.date_desc).isVisible = false
                    menu.findItem(R.id.skill_asc).isVisible = false
                    menu.findItem(R.id.skill_desc).isVisible = false
                    menu.findItem(R.id.numAd_asc).isVisible = false
                    menu.findItem(R.id.numAd_desc).isVisible = false

                    menu.findItem(R.id.myMsg_msg_desc).isVisible = true
                    menu.findItem(R.id.myMsg_title_asc).isVisible = true
                    menu.findItem(R.id.myMsg_title_desc).isVisible = true
                    menu.findItem(R.id.myMsg_creator_asc).isVisible = true
                    menu.findItem(R.id.myMsg_creator_desc).isVisible = true
                    menu.findItem(R.id.myMsg_date_asc).isVisible = true
                    menu.findItem(R.id.myMsg_date_desc).isVisible = true
                }
                R.id.myProfile -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ShowProfileFragment
                    menu.findItem(R.id.app_bar_pencil).isVisible = fragment.isImageDownloaded
                    menu.findItem(R.id.app_bar_search).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = false
                    menu.findItem(R.id.app_bar_add).isVisible = false
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.myProfileEdit -> {
                    menu.findItem(R.id.app_bar_search).isVisible = false
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = true
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
                R.id.myProfileSkills -> {
                    menu.findItem(R.id.app_bar_search).isVisible = true
                    menu.findItem(R.id.app_bar_pencil).isVisible = false
                    menu.findItem(R.id.app_bar_delete).isVisible = false
                    menu.findItem(R.id.app_bar_cancel).isVisible = true
                    menu.findItem(R.id.app_bar_add).isVisible = true
                    menu.findItem(R.id.app_bar_sort).isVisible = false
                    menu.findItem(R.id.app_bar_refresh).isVisible = false
                }
            }
        }

        fun manageActionBarItemActions(activity: Activity, item: MenuItem){
            val navController = activity.findNavController(R.id.nav_host_fragment_content_main)
            when(navController.currentDestination?.id){
                R.id.myAdvertisementEdit -> {
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                            fragment.cancelOperation = true
                            fragment.saveData()
                            if(fragment.operationType=="edit_time_slot") {
                                if(fragment.originFragment=="list_time_slot"){
                                    navController.popBackStack(R.id.myAdvertisements, false)
                                }
                                else{
                                    navController.popBackStack(R.id.myAdvertisementDetails, false)
                                }
                            }
                            else{
                                navController.popBackStack(R.id.myAdvertisements, false)
                            }
                        }
                        R.id.app_bar_add -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                            if(!fragment.isFormValid() && !fragment.cancelOperation){
                                val toast = Toast.makeText(activity.applicationContext, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            else{
                                fragment.saveData()
                                navController.popBackStack(R.id.myAdvertisements, false)
                            }
                        }
                    }
                }
                R.id.myAdvertisementDetails -> {
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
                            navController.popBackStack(R.id.myAdvertisements, false)
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
                R.id.myAdvertisements -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdsListFragment
                    when(item.itemId) {
                        R.id.title_asc -> {
                            fragment.sortAdvertisements("title_asc")
                        }
                        R.id.title_desc -> {
                            fragment.sortAdvertisements("title_desc")
                        }
                        R.id.location_asc -> {
                            fragment.sortAdvertisements("location_asc")
                        }
                        R.id.location_desc -> {
                            fragment.sortAdvertisements("location_desc")
                        }
                        R.id.creator_asc -> {
                            fragment.sortAdvertisements("creator_asc")
                        }
                        R.id.creator_desc -> {
                            fragment.sortAdvertisements("creator_desc")
                        }
                        R.id.date_asc -> {
                            fragment.sortAdvertisements("date_asc")
                        }
                        R.id.date_desc -> {
                            fragment.sortAdvertisements("date_desc")
                        }
                    }
                }
                R.id.myProfile -> {
                    when(item.itemId){
                        R.id.app_bar_pencil -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ShowProfileFragment
                            val profileBackup = fragment.performProfileBackup()
                            val bundle = bundleOf("createAdSrc" to false)
                            navController.navigate(R.id.action_nav_profile_to_edit_profile, bundle)
                        }
                    }
                }
                R.id.myProfileEdit -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                    when(item.itemId){
                        R.id.app_bar_cancel -> {
                            fragment.cancelOperation = true
                            fragment.saveData()
                            if(fragment.createAdSrc){
                                navController.popBackStack(R.id.myAdvertisementEdit, false)
                            }
                            else{
                                navController.popBackStack(R.id.myProfile, false)
                            }
                        }
                        R.id.app_bar_add -> {
                            if(!fragment.isFormValid() && !fragment.cancelOperation){
                                val toast = Toast.makeText(activity.applicationContext, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            else{
                                fragment.saveData()
                                if(fragment.createAdSrc){
                                    navController.popBackStack(R.id.myAdvertisementEdit, false)
                                }
                                else{
                                    navController.popBackStack(R.id.myProfile, false)
                                }
                            }
                        }
                    }
                }
                R.id.myProfileSkills -> {
                    when(item.itemId){
                        R.id.app_bar_add -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChooseSkillsFragment
                            fragment.saveData()
                            println(fragment.adapter.checked_skills.size)
                            navController.previousBackStackEntry?.savedStateHandle?.set("newSkills", fragment.adapter.checked_skills.toList())
                            navController.popBackStack(R.id.myProfileEdit, false)
                        }
                        R.id.app_bar_cancel -> {
                            val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                            val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChooseSkillsFragment
                            fragment.cancelOperation = true
                            fragment.saveData()
                            navController.popBackStack(R.id.myProfileEdit, false)
                        }
                    }
                }
                R.id.onlineAdvertisementSkills -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as SkillAdvertisementListFragment
                    when(item.itemId){
                        R.id.app_bar_refresh -> {
                            fragment.swipeRefreshLayout.isRefreshing = true
                            fragment.updateAdsSkillsList()
                        }
                        R.id.skill_asc -> {
                            fragment.sortAdvertisements("skill_asc")
                        }
                        R.id.skill_desc -> {
                            fragment.sortAdvertisements("skill_desc")
                        }
                        R.id.numAd_asc -> {
                            fragment.sortAdvertisements("numAd_asc")
                        }
                        R.id.numAd_desc -> {
                            fragment.sortAdvertisements("numAd_desc")
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
                            fragment.swipeRefreshLayout.isRefreshing = true
                            fragment.updateAdsList()
                        }
                        R.id.title_asc -> {
                            fragment.sortAdvertisements("title_asc")
                        }
                        R.id.title_desc -> {
                            fragment.sortAdvertisements("title_desc")
                        }
                        R.id.location_asc -> {
                            fragment.sortAdvertisements("location_asc")
                        }
                        R.id.location_desc -> {
                            fragment.sortAdvertisements("location_desc")
                        }
                        R.id.creator_asc -> {
                            fragment.sortAdvertisements("creator_asc")
                        }
                        R.id.creator_desc -> {
                            fragment.sortAdvertisements("creator_desc")
                        }
                        R.id.date_asc -> {
                            fragment.sortAdvertisements("date_asc")
                        }
                        R.id.date_desc -> {
                            fragment.sortAdvertisements("date_desc")
                        }
                    }
                }
                R.id.myMessages -> {
                    val navHostFragment = (activity as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                    val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyMessagesFragment
                    when(item.itemId) {
                        R.id.myMsg_msg_desc -> {
                            fragment.sortMessages("msg_desc")
                        }
                        R.id.myMsg_title_asc -> {
                            fragment.sortMessages("title_asc")
                        }
                        R.id.myMsg_title_desc -> {
                            fragment.sortMessages("title_desc")
                        }
                        R.id.myMsg_creator_asc -> {
                            fragment.sortMessages("creator_asc")
                        }
                        R.id.myMsg_creator_desc -> {
                            fragment.sortMessages("creator_desc")
                        }
                        R.id.myMsg_date_asc -> {
                            fragment.sortMessages("date_asc")
                        }
                        R.id.myMsg_date_desc -> {
                            fragment.sortMessages("date_desc")
                        }
                    }
                }
            }
        }
    }

}