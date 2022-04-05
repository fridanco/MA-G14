package it.polito.ma.g14.timebank

import android.app.ActionBar
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val fullName :  String = "Peter Parker";
    val email : String = "peter.parker@stark.us"
    val nickName : String = "Underoose"
    val location : String = "Queens, New York, NY, US"
    var skills = mutableListOf<String>();
    val description : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "User" //supportActionBar is the keyword for the toolbar
        supportActionBar
        //actionBar?.hide()
        findViewById<TextView>(R.id.textView4).text = fullName
        findViewById<TextView>(R.id.textView5).text = nickName
        findViewById<TextView>(R.id.textView6).text = email
        findViewById<TextView>(R.id.textView7).text = location
    }

}