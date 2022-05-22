package it.polito.ma.g14.timebank.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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

class ShowProfileFragment : Fragment() {

    private val vm by viewModels<FirebaseVM>()

    var fullName :  String = "Peter Parker"
    var email : String = "peter.parker@stark.us"
    var nickName : String = "Underoose"
    var location : String = "Queens, New York, NY, US"
    var skills = arrayListOf<String>()
    var description : String = ""
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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_profile, container, false)

        requireActivity().invalidateOptionsMenu()

        val sv = view?.findViewById<ScrollView>(R.id.scrollView2)
        val frameLayout = view?.findViewById<FrameLayout>(R.id.frameLayout)
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewsReferences()

        vm.profile.observe(viewLifecycleOwner){
            populateProfileText(it)
            populateProfileSkills(it.skills)
        }

        vm.profileImage.observe(viewLifecycleOwner){
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            populateProfilePicture(bmp)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

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

    private fun populateProfileText(profile: User) {
        tv_fullname?.text = profile.fullname
        tv_nickname?.text = profile.nickname
        tv_email?.text = profile.email
        tv_location?.text = profile.location

        h_tv_fullname?.text = profile.fullname
        h_tv_nickname?.text = profile.nickname
        h_tv_email?.text = profile.email
        h_tv_location?.text = profile.location

        if (profile.description.trim().isEmpty()) {
            tv_description?.isGone = true
            h_tv_description?.isGone = true
            view?.findViewById<TextView>(R.id.textView39)?.isVisible = true
            view?.findViewById<TextView>(R.id.textView40)?.isVisible = true
        } else {
            tv_description?.isVisible = true
            h_tv_description?.isVisible = true
            tv_description?.text = profile.description
            h_tv_description?.text = profile.description
            view?.findViewById<TextView>(R.id.textView39)?.isGone = true
            view?.findViewById<TextView>(R.id.textView40)?.isGone = true
        }
    }

    private fun populateProfilePicture(imageBitmap: Bitmap){
        iv_profilePicture?.setImageBitmap(imageBitmap)
        h_iv_profilePicture?.setImageBitmap(imageBitmap)
    }

    private fun populateProfileSkills(skills: List<String>){
        et_skills?.removeAllViews()
        h_et_skills?.removeAllViews()

        if (skills.isEmpty()) {
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