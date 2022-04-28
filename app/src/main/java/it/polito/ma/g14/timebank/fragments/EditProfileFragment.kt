package it.polito.ma.g14.timebank.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
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
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.activities.MainActivity
import it.polito.ma.g14.timebank.utils.Utils
import org.apache.commons.io.IOUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {

    var imageFilepath : String = ""

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

    var cancelOperation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        try {
            sharedPref?.let {
                val jsonPreferences = it.getString("profile", "")
                if (jsonPreferences != null && jsonPreferences.isNotEmpty()) {
                    val jsonObject = JSONObject(jsonPreferences)
                    fullName = jsonObject.getString("fullName")
                        ?: getString(R.string.profile_fullname_placeholder)
                    nickName = jsonObject.getString("nickName")
                        ?: getString(R.string.profile_nickname_placeholder)
                    email =
                        jsonObject.getString("email") ?: getString(R.string.profile_email_placeholder)
                    location = jsonObject.getString("location")
                        ?: getString(R.string.profile_location_placeholder)
                    description = jsonObject.getString("description")
                        ?: getString(R.string.profile_description_placeholder)
                    val jsonSkills: JSONArray = jsonObject.getJSONArray("skills") ?: JSONArray()
                    for (i in 0 until jsonSkills.length()) {
                        skills.add(jsonSkills.getString(i))
                    }
                }
            }
        }
        catch (e:Exception){
            println("ERROR in retrieving sharedPrefs")
            sharedPref?.let {
                with(it.edit()) {
                    clear()
                }
            }
        }

        try {
            val inputStream : FileInputStream = requireContext().openFileInput(getString(R.string.profile_picture_filename))
            profilePicture = IOUtils.toByteArray(inputStream)
            if(profilePicture?.size==0){
                profilePicture = null
            }
        }
        catch (e: Exception){
            profilePicture=null
        }

        activity?.invalidateOptionsMenu()

        val sv = view?.findViewById<ScrollView>(R.id.scrollView)
        val frameLayout = view?.findViewById<FrameLayout>(R.id.frameLayout)
        val frameLayout2 = view?.findViewById<FrameLayout>(R.id.frameLayout2)
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
                        frameLayout2.layoutParams =
                            LinearLayout.LayoutParams(width, (height * 4) / 15)
                    }
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelOperation = false

        setEditTextReferences()
        populateEditText()
        attachListeners()
        attachContextMenu()
    }

    override fun onDestroy() {
        if(cancelOperation){
            super.onDestroy()
            return
        }

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val jsonObject = JSONObject()
        val jsonSkills = JSONArray(skills)
        jsonObject.put("fullName", fullName)
        jsonObject.put("email", email)
        jsonObject.put("nickName", nickName)
        jsonObject.put("location", location)
        jsonObject.put("description", description)
        jsonObject.put("skills", jsonSkills)

        sharedPref?.let {
            with(it.edit()) {
                clear()
                putString("profile", jsonObject.toString())
                apply()
            }
        }

        try {
            profilePicture?.let {
                if (profilePicture?.size != 0) {
                    requireContext().openFileOutput("profile_picture", Context.MODE_PRIVATE).use {
                        it.write(profilePicture)
                    }
                }
            }
        }
        catch (e: Exception){
            profilePicture = null;
            requireContext().deleteFile("profile_picture")
        }
        super.onDestroy()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.change_profile_picture_context_menu, menu)
    }

    // menu item select listener
    override fun onContextItemSelected(item: MenuItem): Boolean {
         return when (item.itemId){
             R.id.change_profile_picture_camera -> {
                 try {
                     val imageFile = createImageFile()
                     val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                     val authorities = "${requireActivity().packageName}.fileprovider"
                     val imageUri = FileProvider.getUriForFile(requireContext(), authorities, imageFile)
                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                     startForTakeImageFromCamera.launch(takePictureIntent)
                 } catch (e: Exception) {
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


    private val startForTakeImageFromCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK ) {

            var bitmap : Bitmap? = BitmapFactory.decodeFile(imageFilepath)

            val ei = ExifInterface(imageFilepath)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            bitmap?.let {
                var rotatedBitmap: Bitmap
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
                        rotateImage(bitmap, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                        rotateImage(bitmap, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                        rotateImage(bitmap, 270)
                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                    else -> rotatedBitmap = bitmap
                }
                val stream = ByteArrayOutputStream()
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
                profilePicture = stream.toByteArray()
            }
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
                            requireContext().contentResolver,
                            selectedImageUri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        selectedImageUri
                    )
                }

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)

                profilePicture = stream.toByteArray()
            }
            populateEditText()
        }
    }

    private fun setEditTextReferences(){
        et_fullname = view?.findViewById<EditText>(R.id.editTextTextPersonName2)
        et_nickname = view?.findViewById<EditText>(R.id.editTextTextPersonName3)
        et_email = view?.findViewById<EditText>(R.id.editTextTextEmailAddress)
        et_location = view?.findViewById<EditText>(R.id.editTextTextPersonName4)
        et_description = view?.findViewById<EditText>(R.id.editTextTextMultiLine)
        et_skills = view?.findViewById<ChipGroup>(R.id.chipGroup)
        button_skills = view?.findViewById<Button>(R.id.button)
        imgButton = view?.findViewById<ImageButton>(R.id.imageButton)
        iv_profilePicture = view?.findViewById<ImageView>(R.id.imageView3)

        h_et_fullname = view?.findViewById<EditText>(R.id.editTextTextPersonName)
        h_et_nickname = view?.findViewById<EditText>(R.id.editTextTextPersonName5)
        h_et_email = view?.findViewById<EditText>(R.id.editTextTextEmailAddress2)
        h_et_location = view?.findViewById<EditText>(R.id.editTextTextPersonName6)
        h_et_description = view?.findViewById<EditText>(R.id.editTextTextMultiLine2)
        h_et_skills = view?.findViewById<ChipGroup>(R.id.chipGroup2)
        h_button_skills = view?.findViewById<Button>(R.id.button2)
        imgButton2 = view?.findViewById<ImageButton>(R.id.imageButton2)
        h_iv_profilePicture = view?.findViewById<ImageView>(R.id.imageView2)
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
            view?.findViewById<TextView>(R.id.textView10)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView11)?.isVisible = true
        }
        else {
            view?.findViewById<TextView>(R.id.textView10)?.isVisible = false
            view?.findViewById<TextView>(R.id.textView11)?.isVisible = false
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
                        view?.findViewById<TextView>(R.id.textView10)?.isVisible = true
                        view?.findViewById<TextView>(R.id.textView11)?.isVisible = true
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
//        button_skills?.setOnClickListener {
//            val i = Intent(this, ChooseSkillsActivity::class.java)
//            i.putExtra("skills", skills)
//            startForChooseSkills.launch(i)
//        }


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
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(ChooseSkillsFragment())
            //val i = Intent(this, ChooseSkillsActivity::class.java)
            //i.putExtra("skills", skills)
            //startForChooseSkills.launch(i)
        }

    }

    private fun attachContextMenu() {
        imgButton?.setOnClickListener{
            requireActivity().openContextMenu(it)
        }
        imgButton?.let {
            registerForContextMenu(imgButton!!)
        }
        imgButton2?.setOnClickListener {
            requireActivity().openContextMenu(it)
        }
        imgButton2?.let {
            registerForContextMenu(imgButton2!!)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile() : File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US).format(Date())
        val imageFilename = "JPEG_" + timestamp + "_";
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(!storageDir!!.exists()){
            storageDir.mkdirs()
        }
        val imageFile = createTempFile(imageFilename, ".jpg", storageDir)
        imageFilepath = imageFile.absolutePath
        return imageFile
    }

    fun rotateImage(source: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun isFormValid() : Boolean {
        if(et_fullname?.error != null || et_nickname?.error != null || et_email?.error != null ||et_location?.error != null ||
            h_et_fullname?.error != null || h_et_nickname?.error != null || h_et_email?.error != null || h_et_location?.error != null){
            return false
        }
        return true
    }

}