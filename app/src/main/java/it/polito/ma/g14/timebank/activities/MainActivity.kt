package it.polito.ma.g14.timebank.activities

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.databinding.ActivityMainBinding
import it.polito.ma.g14.timebank.fragments.EditProfileFragment


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
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
                if(!fragment.isFormValid()){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return;
                }
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
                if(!fragment.isFormValid()){
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
        when(item.itemId){
            R.id.app_bar_pencil -> Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_profile_to_edit_profile)
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceFragment(fragment : Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .commit()
    }
}