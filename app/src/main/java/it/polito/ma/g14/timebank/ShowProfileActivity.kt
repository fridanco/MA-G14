package it.polito.ma.g14.timebank

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class ShowProfileActivity : AppCompatActivity() {

    var fullName :  String = "Peter Parker";
    var email : String = "peter.parker@stark.us"
    var nickName : String = "Underoose"
    var location : String = "Queens, New York, NY, US"
    var skills = arrayListOf<String>();
    var description : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        findViewById<TextView>(R.id.textView4)?.text = fullName
        findViewById<TextView>(R.id.textView5)?.text = nickName
        findViewById<TextView>(R.id.textView6)?.text = email
        findViewById<TextView>(R.id.textView7)?.text = location
        findViewById<TextView>(R.id.textView)?.text = fullName
        findViewById<TextView>(R.id.textView2)?.text = nickName
        findViewById<TextView>(R.id.textView3)?.text = email
        findViewById<TextView>(R.id.textView8)?.text = location
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
                val i = Intent(this, EditProfileActivity::class.java)
                i.putExtra("fullName",fullName)
                i.putExtra("email",email)
                i.putExtra("nickName",nickName)
                i.putExtra("location",location)
                i.putExtra("skills",skills)
                i.putExtra("description",description)
                startActivity(i)
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
        if(savedInstanceState.getStringArrayList("skills")==null) {
            skills = arrayListOf()
        }
        else{
            skills = savedInstanceState.getStringArrayList("skills")!!
        }
        description = savedInstanceState.getString("description","")
    }

}