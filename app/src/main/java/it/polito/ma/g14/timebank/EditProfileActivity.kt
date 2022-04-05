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

    private var et_fullname : EditText? = null
    private var et_nickname : EditText? = null
    private var et_email : EditText? = null
    private var et_location : EditText? = null
    private var h_et_fullname : EditText? = null
    private var h_et_nickname : EditText? = null
    private var h_et_email : EditText? = null
    private var h_et_location : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        fullName = intent.getStringExtra("fullName") ?: ""
        email = intent.getStringExtra("email") ?: ""
        nickName = intent.getStringExtra("nickName") ?: ""
        location = intent.getStringExtra("location") ?: ""
        skills = intent.getStringArrayListExtra("skills") ?: arrayListOf()
        description = intent.getStringExtra("description") ?: ""

        setEditTextReferences()
        populateEditText()
        attachTextChangedListeners()
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
        populateEditText()
    }

    fun setEditTextReferences(){
        et_fullname = findViewById<EditText>(R.id.editTextTextPersonName2)
        et_nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        et_email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        et_location = findViewById<EditText>(R.id.editTextTextPersonName4)

        h_et_fullname = findViewById<EditText>(R.id.editTextTextPersonName)
        h_et_nickname = findViewById<EditText>(R.id.editTextTextPersonName5)
        h_et_email = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        h_et_location = findViewById<EditText>(R.id.editTextTextPersonName6)
    }

    fun populateEditText(){
        et_fullname?.text = fullName.toEditable()
        et_nickname?.text = nickName.toEditable()
        et_email?.text = email.toEditable()
        et_location?.text = location.toEditable()

        h_et_fullname?.text = fullName.toEditable()
        h_et_nickname?.text = nickName.toEditable()
        h_et_email?.text = email.toEditable()
        h_et_location?.text = location.toEditable()
    }

    fun attachTextChangedListeners(){
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

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}