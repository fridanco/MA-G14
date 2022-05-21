package it.polito.ma.g14.timebank.activities

import android.R.attr.src
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.databinding.ActivityMainBinding
import it.polito.ma.g14.timebank.fragments.EditProfileFragment
import it.polito.ma.g14.timebank.fragments.MyAdEditFragment
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.utils.Utils.ActionBarUtils.manageActionBarItemActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


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

        vm.profile.observe(this){
            if(it==null){
                return@observe;
            }
            navViewHeaderFullname.text = it.fullname
            navViewHeaderEmail.text = it.email
            if(it.skills.isEmpty()){
                navViewHeaderNumSkills.text = "No skills selected"
            }
            else{
                navViewHeaderNumSkills.text = "${it.skills.size} skills"
            }
        }

        lifecycleScope.launch {
            vm.storageRef.child(Firebase.auth.currentUser!!.uid).downloadUrl.addOnSuccessListener {
                val url = URL(it.toString())
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                println(url)
                val input: InputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(input)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                vm.setProfileImageUpdated(stream.toByteArray())
            }


            val id = resources.getIdentifier("$packageName:drawable/user", null, null)
            navViewHeaderImage.setImageResource(id)
        }
            vm.profileImage.observe(this) {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                navViewHeaderImage.setImageBitmap(bmp)
            }

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        NavigationUI.setupActionBarWithNavController(this, navController)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.advertisement_skills, R.id.advertisements, R.id.nav_profile, R.id.nav_logout
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
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
                if(!fragment.isFormValid() && !fragment.cancelOperation){
                    val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return;
                }
            }
            R.id.timeSlotDetailsFragment -> {
                navController.popBackStack(R.id.advertisements, true)
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
                val fragment = navHostFragment!!.childFragmentManager.fragments[0] as MyAdEditFragment
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