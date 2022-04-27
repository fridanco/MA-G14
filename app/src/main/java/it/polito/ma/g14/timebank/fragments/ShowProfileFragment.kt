package it.polito.ma.g14.timebank.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.ma.g14.timebank.R
import org.apache.commons.io.IOUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream

/**
 * A simple [Fragment] subclass.
 * Use the [ShowProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowProfileFragment : Fragment() {

    var sharedPref : SharedPreferences? = null

    var fullName :  String = "Peter Parker";
    var email : String = "peter.parker@stark.us"
    var nickName : String = "Underoose"
    var location : String = "Queens, New York, NY, US"
    var skills = arrayListOf<String>();
    var description : String = "";
    var profilePicture : ByteArray? = null

    private var tv_fullname : TextView? = null
    private var tv_nickname : TextView? = null
    private var tv_email : TextView? = null
    private var tv_location : TextView? = null
    private var tv_description : TextView? = null
    private var et_skills : ChipGroup? = null
    private var iv_profilePicture : ImageView? = null
    private var h_tv_fullname : TextView? = null
    private var h_tv_nickname : TextView? = null
    private var h_tv_email : TextView? = null
    private var h_tv_location : TextView? = null
    private var h_tv_description : TextView? = null
    private var h_et_skills : ChipGroup? = null
    private var h_iv_profilePicture : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
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
            val inputStream : FileInputStream = context.openFileInput(getString(R.string.profile_picture_filename))
            profilePicture = IOUtils.toByteArray(inputStream)
            if(profilePicture?.size==0){
                profilePicture = null
            }
        }
        catch (e: Exception){
            profilePicture=null
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sv = view?.findViewById<ScrollView>(R.id.scrollView2)
        val frameLayout = view?.findViewById<FrameLayout>(R.id.frameLayout2)
        sv?.let {
            it.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height = sv.height
                    val width = sv.width
                    frameLayout?.post {
                        frameLayout.layoutParams = LinearLayout.LayoutParams(width, height / 3)
                    }
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        setViewsReferences()
        populateViews()
    }

//    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val intent: Intent? = result.data
//            fullName = intent?.getStringExtra("fullName") ?: ""
//            email = intent?.getStringExtra("email") ?: ""
//            nickName = intent?.getStringExtra("nickName") ?: ""
//            location = intent?.getStringExtra("location") ?: ""
//            skills = intent?.getStringArrayListExtra("skills") ?: arrayListOf()
//            description = intent?.getStringExtra("description") ?: ""
//            profilePicture = intent?.getByteArrayExtra("profilePicture")
//
//            val jsonObject = JSONObject()
//            val jsonSkills = JSONArray(skills)
//            jsonObject.put("fullName", fullName)
//            jsonObject.put("email", email)
//            jsonObject.put("nickName", nickName)
//            jsonObject.put("location", location)
//            jsonObject.put("description", description)
//            jsonObject.put("skills", jsonSkills)
//
//            sharedPref?.let {
//                with(it.edit()) {
//                    clear()
//                    putString("profile", jsonObject.toString())
//                    apply()
//                }
//            }
//
//            try {
//                profilePicture?.let {
//                    if (profilePicture?.size != 0) {
//                        context?.openFileOutput("profile_picture", Context.MODE_PRIVATE).use {
//                            it?.write(profilePicture)
//                        }
//                    }
//                }
//            }
//            catch (e: Exception){
//                profilePicture = null;
//                context?.deleteFile("profile_picture")
//            }
//
//
//            val toast = Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_LONG)
//            toast.setGravity(Gravity.CENTER, 0, 0)
//            toast.show()
//
//            populateViews()
//        }
//    }

    private fun setViewsReferences(){
        tv_fullname = view?.findViewById<TextView>(R.id.textView4)
        tv_nickname = view?.findViewById<TextView>(R.id.textView5)
        tv_email = view?.findViewById<TextView>(R.id.textView6)
        tv_location = view?.findViewById<TextView>(R.id.textView7)
        tv_description = view?.findViewById<TextView>(R.id.textView19)
        et_skills = view?.findViewById<ChipGroup>(R.id.chipGroup)
        iv_profilePicture = view?.findViewById<ImageView>(R.id.imageView4)

        h_tv_fullname = view?.findViewById<TextView>(R.id.textView)
        h_tv_nickname = view?.findViewById<TextView>(R.id.textView2)
        h_tv_email  = view?.findViewById<TextView>(R.id.textView3)
        h_tv_location = view?.findViewById<TextView>(R.id.textView8)
        h_iv_profilePicture = view?.findViewById<ImageView>(R.id.imageView)
        h_tv_description = view?.findViewById<TextView>(R.id.textView20)
        h_et_skills = view?.findViewById<ChipGroup>(R.id.chipGroup2)
    }

    private fun populateViews() {
        tv_fullname?.text = fullName
        tv_nickname?.text = nickName
        tv_email?.text = email
        tv_location?.text = location



        h_tv_fullname?.text = fullName
        h_tv_nickname?.text = nickName
        h_tv_email?.text = email
        h_tv_location?.text = location


        if (description.trim().length == 0) {
            tv_description?.isGone = true
            h_tv_description?.isGone = true
            view?.findViewById<TextView>(R.id.textView39)?.isVisible = true;
            view?.findViewById<TextView>(R.id.textView40)?.isVisible = true;
        } else {
            tv_description?.isVisible = true
            h_tv_description?.isVisible = true
            tv_description?.text = description
            h_tv_description?.text = description
            view?.findViewById<TextView>(R.id.textView39)?.isGone = true;
            view?.findViewById<TextView>(R.id.textView40)?.isGone = true;
        }

        profilePicture?.let {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            iv_profilePicture?.setImageBitmap(bmp)
            h_iv_profilePicture?.setImageBitmap(bmp)
        }

        et_skills?.removeAllViews()
        h_et_skills?.removeAllViews()

        if (skills.size == 0) {
            view?.findViewById<TextView>(R.id.textView33)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView30)?.isVisible = true
        } else {
            view?.findViewById<TextView>(R.id.textView33)?.isVisible = false
            view?.findViewById<TextView>(R.id.textView30)?.isVisible = false

            skills.forEach {
                val inflater: LayoutInflater = layoutInflater
                val skill: Chip = inflater.inflate(R.layout.skill_chip, null) as Chip
                skill.text = it
                skill.isCloseIconVisible = false
                et_skills?.addView(skill)
                h_et_skills?.addView(skill)
            }
        }
    }

}