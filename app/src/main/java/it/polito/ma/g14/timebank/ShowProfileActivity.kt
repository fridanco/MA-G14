package it.polito.ma.g14.timebank

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.apache.commons.io.IOUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileNotFoundException


class ShowProfileActivity : AppCompatActivity() {

    var fullName :  String = "Peter Parker";
    var email : String = "peter.parker@stark.us"
    var nickName : String = "Underoose"
    var location : String = "Queens, New York, NY, US"
    var skills = arrayListOf<String>();
    var description : String = "";
    var profilePicture : ByteArray? = null

    private var tv_fullname : TextView? = null
    private var tv_nickname : TextView? = null
    private var tv_email : TextView? = null
    private var tv_location : TextView? = null
    private var iv_profilePicture : ImageView? = null
    private var h_tv_fullname : TextView? = null
    private var h_tv_nickname : TextView? = null
    private var h_tv_email : TextView? = null
    private var h_tv_location : TextView? = null
    private var h_iv_profilePicture : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val jsonPreferences = sharedPref.getString("profile", "")
        if(jsonPreferences!=null && jsonPreferences.isNotEmpty()){
            val jsonObject = JSONObject(jsonPreferences)
            fullName = jsonObject.getString("fullName") ?: getString(R.string.profile_fullname_placeholder)
            nickName = jsonObject.getString("nickName") ?: getString(R.string.profile_nickname_placeholder)
            email = jsonObject.getString("email") ?: getString(R.string.profile_email_placeholder)
            location = jsonObject.getString("fullName") ?: getString(R.string.profile_location_placeholder)
            description = jsonObject.getString("description") ?: getString(R.string.profile_description_placeholder)
            val jsonSkills : JSONArray = jsonObject.getJSONArray("skills") ?: JSONArray()
            for (i in 0 until jsonSkills.length()) {
                skills.add(jsonSkills.getString(i))
            }
        }

        try {
            val inputStream : FileInputStream = applicationContext.openFileInput(getString(R.string.profile_picture_filename))
            profilePicture = IOUtils.toByteArray(inputStream)
        }
        catch (e: FileNotFoundException){
            profilePicture=null
        }

        setViewsReferences()
        populateViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navbar, menu)
        supportActionBar?.title = ""
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.pencil -> {
                //startActivityForResult is deprecated
                //this is the new way how to do it
                val i = Intent(this, EditProfileActivity::class.java)
                i.putExtra("fullName",fullName)
                i.putExtra("email",email)
                i.putExtra("nickName",nickName)
                i.putExtra("location",location)
                i.putExtra("skills",skills)
                i.putExtra("description",description)
                i.putExtra("profilePicture",profilePicture)
                startForResult.launch(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("fullName",fullName)
        outState.putString("email",email)
        outState.putString("nickName",nickName)
        outState.putString("location",location)
        outState.putStringArrayList("skills",skills)
        outState.putString("description",description)
        outState.putByteArray("profilePicture",profilePicture)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        fullName = savedInstanceState.getString("fullName","")
        email = savedInstanceState.getString("email","")
        nickName = savedInstanceState.getString("nickName","")
        location = savedInstanceState.getString("location","")
        skills = savedInstanceState.getStringArrayList("skills") ?: arrayListOf()
        description = savedInstanceState.getString("description","")
        profilePicture = savedInstanceState.getByteArray("profilePicture")

        populateViews()
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            fullName = intent?.getStringExtra("fullName") ?: ""
            email = intent?.getStringExtra("email") ?: ""
            nickName = intent?.getStringExtra("nickName") ?: ""
            location = intent?.getStringExtra("location") ?: ""
            skills = intent?.getStringArrayListExtra("skills") ?: arrayListOf()
            description = intent?.getStringExtra("description") ?: ""
            profilePicture = intent?.getByteArrayExtra("profilePicture")

            val jsonObject = JSONObject()
            val jsonSkills = JSONArray(skills)
            jsonObject.put("fullName", fullName)
            jsonObject.put("email", email)
            jsonObject.put("nickName", nickName)
            jsonObject.put("location", location)
            jsonObject.put("description", description)
            jsonObject.put("skills",jsonSkills)

            Log.d("Timebank",jsonObject.toString())

            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString("profile", jsonObject.toString())
                apply()
            }

            applicationContext.openFileOutput("profile_picture", Context.MODE_PRIVATE).use {
                it.write(profilePicture)
            }

            populateViews()
        }
    }

    fun setViewsReferences(){
        tv_fullname = findViewById<TextView>(R.id.textView4)
        tv_nickname = findViewById<TextView>(R.id.textView5)
        tv_email = findViewById<TextView>(R.id.textView6)
        tv_location = findViewById<TextView>(R.id.textView7)


        iv_profilePicture = findViewById<ImageView>(R.id.imageView3)


        h_tv_fullname = findViewById<TextView>(R.id.textView)
        h_tv_nickname = findViewById<TextView>(R.id.textView2)
        h_tv_email  = findViewById<TextView>(R.id.textView3)
        h_tv_location = findViewById<TextView>(R.id.textView8)
        h_iv_profilePicture = findViewById<ImageView>(R.id.imageView)
    }

    fun populateViews(){
        if (profilePicture != null) {
            val bmp = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture!!.size)
            iv_profilePicture?.setImageBitmap(bmp)
            h_iv_profilePicture?.setImageBitmap(bmp)
        }
        tv_fullname?.text = fullName
        tv_nickname?.text = nickName
        tv_email?.text = email
        tv_location?.text = location



        h_tv_fullname?.text = fullName
        h_tv_nickname?.text = nickName
        h_tv_email?.text = email
        h_tv_location?.text = location

    }

}