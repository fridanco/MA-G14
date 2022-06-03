package it.polito.ma.g14.timebank.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.Rating
import it.polito.ma.g14.timebank.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ShowProfileAdFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()

    var fullName :  String = "Peter Parker"
    var email : String = "peter.parker@stark.us"
    var nickName : String = "Underoose"
    var location : String = "Queens, New York, NY, US"
    var skills = arrayListOf<String>()
    var description : String = ""
    var profilePicture : ByteArray? = null
    var ratingAdvertisement : List<Rating> = listOf()
    var ratingProfile : Float = 0f
    var ratingCustomer : List<Rating> = listOf()

    private var tv_fullname : TextView? = null
    private var tv_nickname : TextView? = null
    private var tv_email : TextView? = null
    private var tv_location : TextView? = null
    private var tv_description : TextView? = null
    private var et_skills : ChipGroup? = null
    private var iv_profilePicture : ImageView? = null
    private var tv_ratingProfile : RatingBar? = null
    private var h_tv_ratingProfile : RatingBar? = null
    private var h_tv_fullname : TextView? = null
    private var h_tv_nickname : TextView? = null
    private var h_tv_email : TextView? = null
    private var h_tv_location : TextView? = null
    private var h_tv_description : TextView? = null
    private var h_et_skills : ChipGroup? = null
    private var h_iv_profilePicture : ImageView? = null
    private var tv_captionNoRatings : TextView? = null
    private var h_tv_captionNoRatings : TextView? = null
    var otherUid : String? = null

    var isImageDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_profile_ad, container, false)

        otherUid = requireArguments().getString("uid")
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

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        val options: RequestOptions = RequestOptions()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.user)


        vm.db.collection("users")
            .document(otherUid.toString())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.toObject(User::class.java)

                user?.let {
                    fullName = it.fullname
                    email = it.email
                    nickName = it.nickname
                    location = it.location
                    skills = it.skills as ArrayList<String>
                    description = it.description
                    ratingAdvertisement = it.ratingsAdvertiser
                    ratingCustomer = it.ratingsCustomer


                    populateProfileText(it)
                    populateProfileSkills(it.skills)
                }

            }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = Glide.with(requireContext())
                    .asBitmap()
                    .load(vm.storageRef.child(otherUid.toString()))
                    .apply(options)
                    .submit()
                    .get()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                profilePicture = stream.toByteArray()
                isImageDownloaded = true
                activity?.invalidateOptionsMenu()
            } catch (ex: Exception) {
                isImageDownloaded = true
                activity?.invalidateOptionsMenu()
            }
        }

        iv_profilePicture?.let { it1 ->
            Glide.with(this).clear(it1)
            Glide.with(this)
                .load(vm.storageRef.child(otherUid.toString()))
                .apply(options)
                .into(it1)
        }
        h_iv_profilePicture?.let { it2 ->
            Glide.with(this).clear(it2)
            Glide.with(this)
                .load(vm.storageRef.child(otherUid.toString()))
                .apply(options)
                .into(it2)
        }


    }
    private fun setViewsReferences(){
        tv_fullname = view?.findViewById(R.id.textView4)
        tv_nickname = view?.findViewById(R.id.textView5)
        tv_email = view?.findViewById(R.id.textView6)
        tv_location = view?.findViewById(R.id.textView7)
        tv_description = view?.findViewById(R.id.textView19)
        et_skills = view?.findViewById(R.id.chipGroup)
        iv_profilePicture = view?.findViewById(R.id.imageView4)
        tv_ratingProfile = view?.findViewById<RatingBar>(R.id.ratingBar)
        tv_captionNoRatings = view?.findViewById<TextView>(R.id.textView84)

        h_tv_fullname = view?.findViewById(R.id.textView)
        h_tv_nickname = view?.findViewById(R.id.textView2)
        h_tv_email  = view?.findViewById(R.id.textView3)
        h_tv_location = view?.findViewById(R.id.textView8)
        h_iv_profilePicture = view?.findViewById(R.id.imageView)
        h_tv_description = view?.findViewById(R.id.textView20)
        h_et_skills = view?.findViewById(R.id.chipGroup2)
        h_tv_ratingProfile = view?.findViewById<RatingBar>(R.id.ratingBar1)
        h_tv_captionNoRatings = view?.findViewById<TextView>(R.id.textView85)

        tv_ratingProfile?.max = 5
        h_tv_ratingProfile?.max = 5
    }

    private fun populateProfileText(profile: User) {
        tv_fullname?.text = profile.fullname
        tv_nickname?.text = profile.nickname
        tv_email?.text = profile.email
        tv_location?.text = profile.location

        if(profile.ratingsAdvertiser.isEmpty()){
            tv_captionNoRatings?.isVisible = true
            h_tv_captionNoRatings?.isVisible = true
            tv_ratingProfile?.isGone = true
            h_tv_ratingProfile?.isGone = true
            tv_captionNoRatings?.text = "You have not received any rating yet"
            h_tv_captionNoRatings?.text = "You have not received any rating yet"
        }
        else{
            tv_captionNoRatings?.isGone = true
            h_tv_captionNoRatings?.isGone = true
            tv_ratingProfile?.isVisible = true
            h_tv_ratingProfile?.isVisible = true
            tv_ratingProfile?.rating = ratingProfile
            h_tv_ratingProfile?.rating = ratingProfile
        }




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
                val skill: Chip = inflater.inflate(R.layout.profile_skill_chip, null) as Chip
                skill.text = it
                skill.isCloseIconVisible = false
                et_skills?.addView(skill)
                h_et_skills?.addView(skill)
            }
        }
    }
}
