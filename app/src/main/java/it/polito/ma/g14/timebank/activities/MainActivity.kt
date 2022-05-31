package it.polito.ma.g14.timebank.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.databinding.ActivityMainBinding
import it.polito.ma.g14.timebank.fragments.*
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.utils.Utils.ActionBarUtils.manageActionBarItemActions


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val vm by viewModels<FirebaseVM>()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navViewHeaderImage =
            navView.getHeaderView(0).findViewById<ImageView>(R.id.nav_drawer_profileimage)
        val navViewHeaderFullname =
            navView.getHeaderView(0).findViewById<TextView>(R.id.nav_drawer_name)
        val navViewHeaderEmail =
            navView.getHeaderView(0).findViewById<TextView>(R.id.nav_drawer_email)
        val navViewHeaderNumSkills =
            navView.getHeaderView(0).findViewById<TextView>(R.id.nav_drawer_numskills)

        val id = resources.getIdentifier("$packageName:drawable/user", null, null)
        navViewHeaderImage.setImageResource(id)

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.user)
            .error(R.drawable.user)

        vm.profile.observe(this){
            navViewHeaderFullname.text = it.fullname
            navViewHeaderEmail.text = it.email
            if(it.skills.isEmpty()){
                navViewHeaderNumSkills.text = "No skills selected"
            }
            else{
                navViewHeaderNumSkills.text = "${it.skills.size} skills"
            }
            Glide.with(this)
                .load(vm.storageRef.child(Firebase.auth.currentUser!!.uid))
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(navViewHeaderImage)

        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        NavigationUI.setupActionBarWithNavController(this, navController)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.onlineAdvertisementSkills, R.id.myAdvertisements, R.id.myProfile, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    startActivity(Intent(this, LoginActivity::class.java))
                    this.finish()
                }
            return@setOnMenuItemClickListener true
        }

    }

    override fun onBackPressed() {
        val navController = this.findNavController(R.id.nav_host_fragment_content_main)
        when(navController.currentDestination?.id) {
            R.id.myProfileEdit -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return;
                }
                else{
                    fragment.saveData()
                }
            }
            R.id.myAdvertisementEdit -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return;
                }
                else{
                    fragment.saveData()
                }
            }
            R.id.myAdvertisementDetails -> {
                navController.popBackStack(R.id.myAdvertisements, true)
            }
            R.id.myProfileSkills -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChooseSkillsFragment
                fragment.saveData()
                navController.previousBackStackEntry?.savedStateHandle?.set("newSkills", fragment.adapter.checked_skills.toList())
                navController.popBackStack(R.id.myAdvertisements, true)
            }
            R.id.myMessages -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyMessagesFragment
                if (fragment.viewPager.currentItem == 0) {
                    // If the user is currently looking at the first step, allow the system to handle the
                    // Back button. This calls finish() on this activity and pops the back stack.
                    super.onBackPressed()
                } else {
                    // Otherwise, select the previous step.
                    fragment.viewPager.currentItem = fragment.viewPager.currentItem - 1
                }
            }
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        when(navController.currentDestination?.id) {
            R.id.myProfileEdit -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as EditProfileFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return false;
                }
                else{
                    fragment.saveData()
                }
            }
            R.id.myAdvertisementEdit -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return false;
                }
                else{
                    fragment.saveData()
                }
            }
            R.id.myAdvertisementDetails -> {
                navController.popBackStack(R.id.myAdvertisements, true)
            }
            R.id.myProfileSkills -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChooseSkillsFragment
                fragment.saveData()
                navController.previousBackStackEntry?.savedStateHandle?.set("newSkills", fragment.adapter.checked_skills.toList())
                navController.popBackStack(R.id.myAdvertisements, true)
            }
            R.id.myMessages -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyMessagesFragment
                if (fragment.viewPager.currentItem == 0) {
                    // If the user is currently looking at the first step, allow the system to handle the
                    // Back button. This calls finish() on this activity and pops the back stack.
                    super.onBackPressed()
                } else {
                    // Otherwise, select the previous step.
                    fragment.viewPager.currentItem = fragment.viewPager.currentItem - 1
                }
            }
        }

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val searchView = menu.findItem(R.id.app_bar_search).actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "Tap to search"
        searchView.setOnQueryTextListener(object :  androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    dispatchSearchbarQuery(it)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText?.isEmpty() == true){
                    dispatchSearchbarQuery("")
                    return false
                }
                return true
            }
        })

        menu.findItem(R.id.app_bar_pencil).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        manageActionBarItemActions(this, item)
        return super.onOptionsItemSelected(item)
    }

    fun dispatchSearchbarQuery(query: String){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
        when(navController.currentDestination?.id){
            R.id.onlineAdvertisementSkills ->{
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as SkillAdvertisementListFragment
                fragment.searchSkills(query)
            }
            R.id.onlineAdsListFragment -> {
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as OnlineAdsListFragment
                fragment.searchAdvertisements(query)
            }
            R.id.myAdvertisements -> {
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdsListFragment
                fragment.searchAdvertisements(query)
            }
            R.id.myProfileSkills -> {
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as ChooseSkillsFragment
                fragment.searchSkills(query)
            }
        }
    }
}