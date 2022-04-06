package it.polito.ma.g14.timebank

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {

    var fullName :  String = ""
    var email : String = ""
    var nickName : String = ""
    var location : String = ""
    var skills : ArrayList<String> = arrayListOf()
    var description : String = ""
    var profilePicture : ByteArray? = null

    private var et_fullname : EditText? = null
    private var et_nickname : EditText? = null
    private var et_email : EditText? = null
    private var et_location : EditText? = null
    private var iv_profilePicture : ImageView? = null
    private var h_et_fullname : EditText? = null
    private var h_et_nickname : EditText? = null
    private var h_et_email : EditText? = null
    private var h_et_location : EditText? = null
    private var h_iv_profilePicture : ImageView? = null

    private var imgButton : ImageButton? = null
    private var imgButton2 : ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        fullName = intent.getStringExtra("fullName") ?: ""
        email = intent.getStringExtra("email") ?: ""
        nickName = intent.getStringExtra("nickName") ?: ""
        location = intent.getStringExtra("location") ?: ""
        skills = intent.getStringArrayListExtra("skills") ?: arrayListOf()
        description = intent.getStringExtra("description") ?: ""
        profilePicture = intent.getByteArrayExtra("profilePicture")

        setEditTextReferences()
        populateEditText()
        attachTextChangedListeners()

        imgButton = findViewById<ImageButton>(R.id.imageButton)
        imgButton2 = findViewById<ImageButton>(R.id.imageButton2)
        imgButton?.let {
            registerForContextMenu(imgButton)
        }
        imgButton2?.let {
            registerForContextMenu(imgButton2)
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
        populateEditText()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navbar, menu)
        supportActionBar?.title = ""
        menu.findItem(R.id.pencil).setVisible(false)
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.change_profile_picture_context_menu, menu)
    }

    // menu item select listener
    override fun onContextItemSelected(item: MenuItem): Boolean {
         return when (item.itemId){
             R.id.change_profile_picture_camera -> {

                     val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                     try {
                         startForResult.launch(takePictureIntent)


                     } catch (e: ActivityNotFoundException) {
                         // display error state to the user
                     }
                true
             }
             R.id.change_profile_picture_gallery -> {
                 true
             }
             else -> super.onContextItemSelected(item)
            //fotocamera
            //galleria

        }



        return true
    }

    override fun onBackPressed() {
        //Returning result to ShowProfileActivity
        val resultData = Intent()
        resultData.putExtra("fullName", fullName)
        resultData.putExtra("email", email)
        resultData.putExtra("nickName", nickName)
        resultData.putExtra("location", location)
        resultData.putExtra("skills", skills)
        resultData.putExtra("description", description)
        resultData.putExtra("profilePicture",profilePicture)
        setResult(Activity.RESULT_OK, resultData)


        //this calls finish() so it needs to be put AFTER setResult()
        //otherwise setResult will not be called
        super.onBackPressed()
    }



    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK ) {

            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            profilePicture = stream.toByteArray()
            populateEditText()


        }
    }

    fun setEditTextReferences(){
        et_fullname = findViewById<EditText>(R.id.editTextTextPersonName2)
        et_nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        et_email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        et_location = findViewById<EditText>(R.id.editTextTextPersonName4)
        iv_profilePicture = findViewById<ImageView>(R.id.imageView3)

        h_et_fullname = findViewById<EditText>(R.id.editTextTextPersonName)
        h_et_nickname = findViewById<EditText>(R.id.editTextTextPersonName5)
        h_et_email = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        h_et_location = findViewById<EditText>(R.id.editTextTextPersonName6)
        h_iv_profilePicture = findViewById<ImageView>(R.id.imageView2)
    }

    fun populateEditText(){
        if (profilePicture != null){
            val bmp = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture!!.size)
            iv_profilePicture?.setImageBitmap(bmp)
            h_iv_profilePicture?.setImageBitmap(bmp)
        }
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