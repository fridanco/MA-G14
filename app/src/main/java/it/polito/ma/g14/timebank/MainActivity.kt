package it.polito.ma.g14.timebank

import android.app.ActionBar
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val fullName :  String = "Mario";
    val email : String = "m@gmail.com"
    val nickName : String = "mariodedapolito"
    val location : String = "location"
    var skills = mutableListOf<String>();
    val description : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "New Title Here" //supportActionBar is the keyword for the toolbar
        actionBar?.hide()
        findViewById<TextView>(R.id.textView4).text = fullName
        findViewById<TextView>(R.id.textView5).text = nickName
        findViewById<TextView>(R.id.textView6).text = email
        findViewById<TextView>(R.id.textView7).text = location
    }

}