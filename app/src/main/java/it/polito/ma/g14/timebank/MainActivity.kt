package it.polito.ma.g14.timebank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

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
        findViewById<TextView>(R.id.textView4).text = fullName
        findViewById<TextView>(R.id.textView5).text = nickName
        findViewById<TextView>(R.id.textView6).text = email
        findViewById<TextView>(R.id.textView7).text = location
    }
}