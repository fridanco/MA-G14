package it.polito.ma.g14.timebank.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.databinding.ActivityMainBinding
import it.polito.ma.g14.timebank.fragments.EditProfileFragment
import it.polito.ma.g14.timebank.fragments.ProfileVM
import it.polito.ma.g14.timebank.fragments.TimeSlotEditFragment
import it.polito.ma.g14.timebank.utils.Utils.ActionBarUtils.manageActionBarItemActions
import org.apache.commons.io.IOUtils
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    val vm by viewModels<ProfileVM>()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.isProfileInitalized.observe(this){
            if(it==0){
                vm.initProfile()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navViewHeaderImage = navView.getHeaderView(0).findViewById<ImageView>(R.id.nav_drawer_profileimage)
        val navViewHeaderFullname = navView.getHeaderView(0).findViewById<TextView>(R.id.nav_drawer_name)
        val navViewHeaderEmail = navView.getHeaderView(0).findViewById<TextView>(R.id.nav_drawer_email)
        val navViewHeaderNumSkills = navView.getHeaderView(0).findViewById<TextView>(R.id.nav_drawer_numskills)
        vm.profile.observe(this){
            navViewHeaderFullname.text = it.fullname
            navViewHeaderEmail.text = it.email
        }
        vm.skills.observe(this){
            if(it.isEmpty()){
                navViewHeaderNumSkills.text = R.string.nav_drawer_numskills_placeholder.toString()
            }
            else{
                navViewHeaderNumSkills.text = "${it.size} skills"
            }
        }

        try {
            val inputStream : FileInputStream = openFileInput(getString(R.string.profile_picture_filename))
            val profilePicture = IOUtils.toByteArray(inputStream)
            if(profilePicture?.isNotEmpty() == true){
                val bmp = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.size)
                navViewHeaderImage.setImageBitmap(bmp)
            }
        }
        catch (e: Exception){
            val id = resources.getIdentifier("$packageName:drawable/user", null, null)
            navViewHeaderImage.setImageResource(id)
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        NavigationUI.setupActionBarWithNavController(this, navController)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.timeSlotListFragment, R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        val navController = this.findNavController(R.id.nav_host_fragment_content_main)
        when(navController.currentDestination?.id) {
            R.id.edit_profile -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return;
                }
            }
            R.id.timeSlotEditFragment -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotEditFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return;
                }
            }
            R.id.timeSlotDetailsFragment -> {
                navController.popBackStack(R.id.timeSlotListFragment, true)
            }
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        when(navController.currentDestination?.id) {
            R.id.edit_profile -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return false;
                }
            }
            R.id.timeSlotEditFragment -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as TimeSlotEditFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return false;
                }
            }
        }

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.app_bar_pencil).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        manageActionBarItemActions(this, item)
        return super.onOptionsItemSelected(item)
    }
}