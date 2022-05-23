package it.polito.ma.g14.timebank.fragments

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.User
import it.polito.ma.g14.timebank.utils.Utils
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditProfileFragment : Fragment() {

    private val vm by viewModels<FirebaseVM>()

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

    var performProfileBackup = false
    var performSkillsBackup = false
    var cancelOperation = false

    lateinit var profileBackup: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        activity?.invalidateOptionsMenu()

//        try {
//            val inputStream : FileInputStream = requireContext().openFileInput(getString(R.string.profile_picture_filename))
//            profilePicture = IOUtils.toByteArray(inputStream)
//            if(profilePicture?.size==0){
//                profilePicture = null
//            }
//        }
//        catch (e: Exception){
//            profilePicture=null
//        }

        //TODO: Set email field as disabled

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

        profileBackup = arguments?.getSerializable("profileBackup") as User

        setEditTextReferences()
        attachListeners()
        attachContextMenu()

        //populateProfileImage()
        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        val options: RequestOptions = RequestOptions()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.user)

        iv_profilePicture?.let { it1 ->
            Glide.with(this)
                .load(vm.storageRef.child(Firebase.auth.currentUser!!.uid))
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(it1)
        }
        h_iv_profilePicture?.let { it2 ->
            Glide.with(this)
                .load(vm.storageRef.child(Firebase.auth.currentUser!!.uid))
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(it2)
        }

        vm.profile.observe(viewLifecycleOwner){
            if(performProfileBackup){
                profileBackup = User().apply {
                    this.fullname = it.fullname
                    this.nickname = it.nickname
                    this.email = it.email
                    this.location = it.location
                    this.description = it.description
                    this.skills = it.skills
                }
                performProfileBackup = false
            }
            skills = it.skills as ArrayList<String>
            populateProfileEditText(it)
            populateProfileSkills(it.skills)
        }
    }

    override fun onDestroy() {
        if(cancelOperation){
            //Restore profile data
            profileBackup.let {
                val inputStream : FileInputStream
                var byteArray = byteArrayOf()
                try {
                    inputStream = requireContext().openFileInput(getString(R.string.profile_picture_filename))
                    byteArray = IOUtils.toByteArray(inputStream)
                }
                catch (e: Exception){

                }
                vm.updateProfile(it.fullname,it.nickname,it.email,it.location,it.description, it.skills, byteArray)
            }

            Toast.makeText(requireContext(), "Changes discarded", Toast.LENGTH_SHORT).show()

            super.onDestroy()
            return
        }

        var byteArray = byteArrayOf()
        profilePicture?.let {
            byteArray = it
        }

        vm.updateProfile(fullName, nickName, email, location, description, skills, byteArray)
        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()

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
        if (result.resultCode == RESULT_OK ) {

            val bitmap : Bitmap? = BitmapFactory.decodeFile(imageFilepath)

            val ei = ExifInterface(imageFilepath)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            bitmap?.let {
                val rotatedBitmap: Bitmap
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
                populateProfileImage()
            }
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

                populateProfileImage()
            }
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

    private fun populateProfileImage(){
        profilePicture?.let{
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            iv_profilePicture?.setImageBitmap(bmp)
            h_iv_profilePicture?.setImageBitmap(bmp)
        }
    }

    private fun populateProfileEditText(profile: User){

        et_fullname?.text = profile.fullname.toEditable()
        et_nickname?.text = profile.nickname.toEditable()
        et_email?.text = profile.email.toEditable()
        et_location?.text = profile.location.toEditable()
        et_description?.text = profile.description.toEditable()

        h_et_fullname?.text = profile.fullname.toEditable()
        h_et_nickname?.text = profile.nickname.toEditable()
        h_et_email?.text = profile.email.toEditable()
        h_et_location?.text = profile.location.toEditable()
        h_et_description?.text = profile.description.toEditable()

    }

    private fun populateProfileSkills(skills: List<String>){
        et_skills?.removeAllViews()
        h_et_skills?.removeAllViews()
        if(skills.isEmpty()){
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
                    vm.removeProfileSkill(skills, skill.text.toString())
                }
                et_skills?.addView(skill)
                h_et_skills?.addView(skill)
            }
        }
    }

    private fun attachListeners(){
        et_fullname?.doOnTextChanged { text, _, _, _ ->
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
        et_nickname?.doOnTextChanged { text, _, _, _ ->
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
        et_email?.doOnTextChanged { text, _, _, _ ->
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
        et_location?.doOnTextChanged { text, _, _, _ ->
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
        et_description?.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
        }

        button_skills?.setOnClickListener {
            var byteArray = byteArrayOf()
            profilePicture?.let { byteArray=it }
            vm.updateProfile(fullName, nickName, email, location, description, skills, byteArray)

            view?.findNavController()?.navigate(R.id.action_edit_profile_to_chooseSkillsFragment)
        }


        h_et_fullname?.doOnTextChanged { text, _, _, _ ->
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
        h_et_nickname?.doOnTextChanged { text, _, _, _ ->
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
        h_et_email?.doOnTextChanged { text, _, _, _ ->
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
        h_et_location?.doOnTextChanged { text, _, _, _ ->
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
        h_et_description?.doOnTextChanged { text, _, _, _ ->
            description = text.toString()
        }
        h_button_skills?.setOnClickListener {
            var byteArray = byteArrayOf()
            profilePicture?.let { byteArray=it }
            vm.updateProfile(fullName, nickName, email, location, description, skills, byteArray)

            view?.findNavController()?.navigate(R.id.action_edit_profile_to_chooseSkillsFragment)
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
        val imageFilename = "JPEG_" + timestamp + "_"
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
        if(fullName.trim().isEmpty()){
            et_fullname?.error = "Fullname cannot be empty"
            h_et_fullname?.error = "Fullname cannot be empty"
        }
        if(nickName.trim().isEmpty()){
            et_nickname?.error = "Nickname cannot be empty"
            h_et_nickname?.error = "Nickname cannot be empty"
        }
        if(email.trim().isEmpty()){
            et_email?.error = "Email cannot be empty"
            h_et_email?.error = "Email cannot be empty"
        }
        if(location.trim().isEmpty()){
            et_location?.error = "Location cannot be empty"
            h_et_location?.error = "Location cannot be empty"
        }

        if(et_fullname?.error != null || et_nickname?.error != null || et_email?.error != null ||et_location?.error != null ||
            h_et_fullname?.error != null || h_et_nickname?.error != null || h_et_email?.error != null || h_et_location?.error != null){
            return false
        }
        return true
    }

}
