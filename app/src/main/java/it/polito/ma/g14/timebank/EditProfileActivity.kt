package it.polito.ma.g14.timebank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged

class EditProfileActivity : AppCompatActivity() {

    var fullName :  String = ""
    var email : String = ""
    var nickName : String = ""
    var location : String = ""
    var skills : ArrayList<String> = arrayListOf()
    var description : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        if(savedInstanceState!=null){
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



        var et_fullname = findViewById<EditText>(R.id.editTextTextPersonName2)
        et_fullname?.text = fullName.toEditable()
        var et_nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        et_nickname?.text = nickName.toEditable()
        var et_email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        et_email?.text = email.toEditable()
        var et_location = findViewById<EditText>(R.id.editTextTextPersonName4)
        et_location?.text = location.toEditable()

        var h_et_fullname = findViewById<EditText>(R.id.editTextTextPersonName)
        h_et_fullname?.text = fullName.toEditable()
        var h_et_nickname = findViewById<EditText>(R.id.editTextTextPersonName5)
        h_et_nickname?.text = nickName.toEditable()
        var h_et_email = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        h_et_email?.text = email.toEditable()
        var h_et_location = findViewById<EditText>(R.id.editTextTextPersonName6)
        h_et_location?.text = location.toEditable()

        et_fullname?.doOnTextChanged { text, start, before, count ->
            fullName = text.toString()
        }
        et_nickname?.doOnTextChanged { text, start, before, count ->
            nickName = text.toString()
        }
        et_email?.doOnTextChanged { text, start, before, count ->
            email = text.toString()
        }
        et_location?.doOnTextChanged { text, start, before, count ->
            location = text.toString()
        }

        h_et_fullname?.doOnTextChanged { text, start, before, count ->
            fullName = text.toString()
        }
        h_et_nickname?.doOnTextChanged { text, start, before, count ->
            nickName = text.toString()
        }
        h_et_email?.doOnTextChanged { text, start, before, count ->
            email = text.toString()
        }
        h_et_location?.doOnTextChanged { text, start, before, count ->
            location = text.toString()
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




    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}