package it.polito.ma.g14.timebank

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.ByteArrayOutputStream
import java.io.File


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
    private var et_description : EditText? = null
    private var et_skills : ChipGroup? = null
    private var button_skills : Button? = null
    private var iv_profilePicture : ImageView? = null

    private var h_et_fullname : EditText? = null
    private var h_et_nickname : EditText? = null
    private var h_et_email : EditText? = null
    private var h_et_location : EditText? = null
    private var h_et_description : EditText? = null
    private var h_et_skills : ChipGroup? = null
    private var h_button_skills : Button? = null
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

        val sv = findViewById<ScrollView>(R.id.scrollView)
        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        val frameLayout2 = findViewById<FrameLayout>(R.id.frameLayout2)
        sv?.let {
            it.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height = sv.height
                    val width = sv.width
                    frameLayout?.post {
                        frameLayout.layoutParams = LinearLayout.LayoutParams(width, height / 15)
                    }
                    frameLayout2?.post {
                        frameLayout.layoutParams = LinearLayout.LayoutParams(width, (height * 4) / 15)
                    }
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        setEditTextReferences()
        populateEditText()
        attachListeners()
        attachContextMenu()
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
        supportActionBar?.title = "Edit profile"
        //supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#03a2ff")))
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
                 try {
                     val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                     val filePhoto = File(applicationContext.filesDir, "profile_picture_tmp")
//                     val imageUri = filePhoto.toUri()
//                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.path)
                     startForTakeImageFromCamera.launch(takePictureIntent)
                 } catch (e: ActivityNotFoundException) {
                     // display error state to the user
                 }
                true
             }
             R.id.change_profile_picture_gallery -> {
                 try {
                     val pickImageintent = Intent()
                     pickImageintent.type = "image/*"
                     pickImageintent.action = Intent.ACTION_GET_CONTENT
                     startForPickImageFromGallery.launch(pickImageintent)
                 } catch (e: ActivityNotFoundException) {
                     // display error state to the user
                 }
                 true
             }
             else -> super.onContextItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if(et_fullname?.error != null || et_nickname?.error != null || et_email?.error != null ||et_location?.error != null ||
            h_et_fullname?.error != null || h_et_nickname?.error != null || h_et_email?.error != null || h_et_location?.error != null){
            val toast = Toast.makeText(this, "Please fill in all the mandatory fields", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }

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

    private val startForChooseSkills = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            skills = intent?.getStringArrayListExtra("skills") as ArrayList<String>

            val toast = Toast.makeText(this, "Skills updated", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()

            populateEditText()
        }
    }

    private val startForTakeImageFromCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK ) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val profilePictureSize = stream.toByteArray().size
            if(profilePictureSize/1024 > 400){
                val compressionRatio = (400 / (profilePictureSize/1024))*100;
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, stream)
            }
            profilePicture = stream.toByteArray()

//            try {
////                val stream = ByteArrayOutputStream()
////                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
////                profilePicture = stream.toByteArray()
////                profilePicture?.let {
////                    if (profilePicture?.size != 0) {
////                        applicationContext.openFileOutput("profile_picture_tmp", Context.MODE_PRIVATE).use {
////                            it.write(profilePicture)
////                        }
////                    }
////                }
//                //val filename = "${applicationContext.filesDir}/profile_picture_tmp"
//                val ei = ExifInterface("${applicationContext.filesDir}/profile_picture_tmp")
//                val orientation: Int? = ei?.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED
//                )
//
//                var rotatedBitmap: Bitmap? = null
//                when (orientation) {
//                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(imageBitmap, 90)
//                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(imageBitmap, 180)
//                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(imageBitmap, 270)
//                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = imageBitmap
//                    else -> rotatedBitmap = imageBitmap
//                }
//
//                rotatedBitmap?.let {
//                    val stream = ByteArrayOutputStream()
//                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                    val profilePictureSize = stream.toByteArray().size
//                    if(profilePictureSize/1024 > 400){
//                        val compressionRatio = (400 / (profilePictureSize/1024))*100;
//                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, stream)
//                    }
//                    profilePicture = stream.toByteArray()
//                }
////                applicationContext.deleteFile("profile_picture_tmp")
//            }
//            catch (e: Exception){
//                Log.d("timebank",e.message.toString())
//                profilePicture = null;
//            }

            populateEditText()
        }
    }

    private val startForPickImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK ) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            val data: Intent? = result.data
            val selectedImageUri: Uri = data?.data as Uri

            selectedImageUri.let {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            applicationContext.contentResolver,
                            selectedImageUri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        selectedImageUri
                    )
                }

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)

                profilePicture = stream.toByteArray()
            }
            populateEditText()
        }
    }

    private fun setEditTextReferences(){
        et_fullname = findViewById<EditText>(R.id.editTextTextPersonName2)
        et_nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        et_email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        et_location = findViewById<EditText>(R.id.editTextTextPersonName4)
        et_description = findViewById<EditText>(R.id.editTextTextMultiLine)
        et_skills = findViewById<ChipGroup>(R.id.chipGroup)
        button_skills = findViewById<Button>(R.id.button)
        imgButton = findViewById<ImageButton>(R.id.imageButton)
        iv_profilePicture = findViewById<ImageView>(R.id.imageView3)

        h_et_fullname = findViewById<EditText>(R.id.editTextTextPersonName)
        h_et_nickname = findViewById<EditText>(R.id.editTextTextPersonName5)
        h_et_email = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        h_et_location = findViewById<EditText>(R.id.editTextTextPersonName6)
        h_et_description = findViewById<EditText>(R.id.editTextTextMultiLine2)
        h_et_skills = findViewById<ChipGroup>(R.id.chipGroup2)
        h_button_skills = findViewById<Button>(R.id.button2)
        imgButton2 = findViewById<ImageButton>(R.id.imageButton2)
        h_iv_profilePicture = findViewById<ImageView>(R.id.imageView2)
    }

    private fun populateEditText(){

        et_fullname?.text = fullName.toEditable()
        et_nickname?.text = nickName.toEditable()
        et_email?.text = email.toEditable()
        et_location?.text = location.toEditable()
        et_description?.text = description.toEditable()

        h_et_fullname?.text = fullName.toEditable()
        h_et_nickname?.text = nickName.toEditable()
        h_et_email?.text = email.toEditable()
        h_et_location?.text = location.toEditable()
        h_et_description?.text = description.toEditable()

        profilePicture?.let{
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            iv_profilePicture?.setImageBitmap(bmp)
            h_iv_profilePicture?.setImageBitmap(bmp)
        }

        et_skills?.removeAllViews()
        h_et_skills?.removeAllViews()
        if(skills.size==0){
            findViewById<TextView>(R.id.textView10)?.isVisible = true
            findViewById<TextView>(R.id.textView11)?.isVisible = true
        }
        else {
            findViewById<TextView>(R.id.textView10)?.isVisible = false
            findViewById<TextView>(R.id.textView11)?.isVisible = false
            skills.forEach {
                val inflater: LayoutInflater = layoutInflater
                val skill: Chip = inflater.inflate(R.layout.skill_chip, null) as Chip
                skill.text = it
                skill.isCloseIconVisible = true
                skill.setOnCloseIconClickListener {
                    val skill = it as Chip
                    skills.remove(skill.text.toString())
                    et_skills?.removeView(it)
                    h_et_skills?.removeView(it)
                    if(skills.size==0){
                        findViewById<TextView>(R.id.textView10)?.isVisible = true
                        findViewById<TextView>(R.id.textView11)?.isVisible = true
                    }
                }
                et_skills?.addView(skill)
                h_et_skills?.addView(skill)
            }
        }
    }

    private fun attachListeners(){
        et_fullname?.doOnTextChanged { text, start, before, count ->
            fullName = text.toString()
            if(fullName.trim().isEmpty()){
                et_fullname?.error = "Fullname cannot be empty"
                h_et_fullname?.error = "Fullname cannot be empty"
            }
            else{
                et_fullname?.error = null
                h_et_fullname?.error = null
            }
        }
        et_nickname?.doOnTextChanged { text, start, before, count ->
            nickName = text.toString()
            if(nickName.trim().isEmpty()){
                et_nickname?.error = "Nickname cannot be empty"
                h_et_nickname?.error = "Nickname cannot be empty"
            }
            else{
                et_nickname?.error = null
                h_et_nickname?.error = null
            }
        }
        et_email?.doOnTextChanged { text, start, before, count ->
            email = text.toString()
            if(email.trim().isEmpty()){
                et_email?.error = "Email cannot be empty"
                h_et_email?.error = "Email cannot be empty"
            }
            else{
                et_email?.error = null
                h_et_email?.error = null
            }
        }
        et_location?.doOnTextChanged { text, start, before, count ->
            location = text.toString()
            if(location.trim().isEmpty()){
                et_location?.error = "Location cannot be empty"
                h_et_location?.error = "Location cannot be empty"
            }
            else{
                et_location?.error = null
                h_et_location?.error = null
            }
        }
        et_description?.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }
        button_skills?.setOnClickListener {
            val i = Intent(this, ChooseSkillsActivity::class.java)
            i.putExtra("skills", skills)
            startForChooseSkills.launch(i)
        }


        h_et_fullname?.doOnTextChanged { text, start, before, count ->
            fullName = text.toString()
            if(fullName.trim().isEmpty()){
                et_fullname?.error = "Fullname cannot be empty"
                h_et_fullname?.error = "Fullname cannot be empty"
            }
            else{
                et_fullname?.error = null
                h_et_fullname?.error = null
            }
        }
        h_et_nickname?.doOnTextChanged { text, start, before, count ->
            nickName = text.toString()
            if(nickName.trim().isEmpty()){
                et_nickname?.error = "Nickname cannot be empty"
                h_et_nickname?.error = "Nickname cannot be empty"
            }
            else{
                et_nickname?.error = null
                h_et_nickname?.error = null
            }
        }
        h_et_email?.doOnTextChanged { text, start, before, count ->
            email = text.toString()
            if(email.trim().isEmpty()){
                et_email?.error = "Email cannot be empty"
                h_et_email?.error = "Email cannot be empty"
            }
            else{
                et_email?.error = null
                h_et_email?.error = null
            }
        }
        h_et_location?.doOnTextChanged { text, start, before, count ->
            location = text.toString()
            if(location.trim().isEmpty()){
                et_location?.error = "Location cannot be empty"
                h_et_location?.error = "Location cannot be empty"
            }
            else{
                et_location?.error = null
                h_et_location?.error = null
            }
        }
        h_et_description?.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }
        h_button_skills?.setOnClickListener {
            val i = Intent(this, ChooseSkillsActivity::class.java)
            i.putExtra("skills", skills)
            startForChooseSkills.launch(i)
        }

    }

    private fun attachContextMenu() {
        imgButton?.setOnClickListener{
            openContextMenu(it)
        }
        imgButton?.let {
            registerForContextMenu(imgButton)
        }
        imgButton2?.setOnClickListener {
            openContextMenu(it)
        }
        imgButton2?.let {
            registerForContextMenu(imgButton2)
        }
    }

    fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}