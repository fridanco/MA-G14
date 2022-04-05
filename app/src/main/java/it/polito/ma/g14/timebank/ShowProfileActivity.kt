package it.polito.ma.g14.timebank

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class ShowProfileActivity : AppCompatActivity() {

    var fullName :  String = "Peter Parker";
    var email : String = "peter.parker@stark.us"
    var nickName : String = "Underoose"
    var location : String = "Queens, New York, NY, US"
    var skills = arrayListOf<String>();
    var description : String = "";

    private var tv_fullname : TextView? = null
    private var tv_nickname : TextView? = null
    private var tv_email : TextView? = null
    private var tv_location : TextView? = null
    private var h_tv_fullname : TextView? = null
    private var h_tv_nickname : TextView? = null
    private var h_tv_email : TextView? = null
    private var h_tv_location : TextView? = null

    private final val EDIT_PROFILE_ACTIVITY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        setTextViewReferences()
        populateTextView()
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        fullName = savedInstanceState.getString("fullName","")
        email = savedInstanceState.getString("email","")
        nickName = savedInstanceState.getString("nickName","")
        location = savedInstanceState.getString("location","")
        skills = savedInstanceState.getStringArrayList("skills") ?: arrayListOf()
        description = savedInstanceState.getString("description","")

        populateTextView()
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        println(result)
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            fullName = intent?.getStringExtra("fullName") ?: ""
            email = intent?.getStringExtra("email") ?: ""
            nickName = intent?.getStringExtra("nickName") ?: ""
            location = intent?.getStringExtra("location") ?: ""
            skills = intent?.getStringArrayListExtra("skills") ?: arrayListOf()
            description = intent?.getStringExtra("description") ?: ""
            populateTextView()
        }
    }

    fun setTextViewReferences(){
        tv_fullname = findViewById<TextView>(R.id.textView4)
        tv_nickname = findViewById<TextView>(R.id.textView5)
        tv_email = findViewById<TextView>(R.id.textView6)
        tv_location = findViewById<TextView>(R.id.textView7)
        h_tv_fullname = findViewById<TextView>(R.id.textView)
        h_tv_nickname = findViewById<TextView>(R.id.textView2)
        h_tv_email  = findViewById<TextView>(R.id.textView3)
        h_tv_location = findViewById<TextView>(R.id.textView8)
    }

    fun populateTextView(){
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