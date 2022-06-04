package it.polito.ma.g14.timebank.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.User
import it.polito.ma.g14.timebank.utils.Utils
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

    var otherUid : String? = null

    lateinit var tabLayout : TabLayout
    lateinit var viewPager : ViewPager2
    lateinit var pagerAdapter: FragmentStateAdapter

    var isImageDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        tabLayout = view.findViewById(R.id.ratingTabLayout)

        viewPager = view.findViewById(R.id.ratingViewPager)
        pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        val tabText = listOf("As advertiser", "As client")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabText[position]
        }.attach()

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

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        val uid = Firebase.auth.currentUser!!.uid
        val fragmentList = listOf<Fragment>(
            ShowProfileRatingFragment(otherUid.toString(), "asAdvertiser"),
            ShowProfileRatingFragment(otherUid.toString(), "asClient")
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    private fun setViewsReferences(){
        tv_fullname = view?.findViewById(R.id.textView4)
        tv_nickname = view?.findViewById(R.id.textView5)
        tv_email = view?.findViewById(R.id.textView6)
        tv_location = view?.findViewById(R.id.textView7)
        tv_description = view?.findViewById(R.id.textView19)
        et_skills = view?.findViewById(R.id.chipGroup)
        iv_profilePicture = view?.findViewById(R.id.imageView4)

        h_tv_fullname = view?.findViewById(R.id.textView)
        h_tv_nickname = view?.findViewById(R.id.textView2)
        h_tv_email  = view?.findViewById(R.id.textView3)
        h_tv_location = view?.findViewById(R.id.textView8)
        h_iv_profilePicture = view?.findViewById(R.id.imageView)
        h_tv_description = view?.findViewById(R.id.textView20)
        h_et_skills = view?.findViewById(R.id.chipGroup2)

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
