package it.polito.ma.g14.timebank.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.OnlineAdDetailsVM
import it.polito.ma.g14.timebank.models.Rating
import it.polito.ma.g14.timebank.utils.Utils


class MyAdDetailsFragment : Fragment() {

    val vm by viewModels<FirebaseVM>()
    private val onlineAdDetailsVM by viewModels<OnlineAdDetailsVM>()

    lateinit var tv_title : TextView
    lateinit var tv_description : TextView
    lateinit var tv_date : TextView
    lateinit var tv_from : TextView
    lateinit var tv_to : TextView
    lateinit var tv_location : TextView
    lateinit var skillContainer : ChipGroup

    var advertisementID : String = ""
    var shownAdvertisement: Advertisement? = null
    var shownAdvertisementSkill: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_ad_details, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        advertisementID = requireArguments().getString("advertisementID").toString()

        tv_title = view.findViewById(R.id.textView4)
        tv_description = view.findViewById(R.id.textView5)
        tv_date = view.findViewById(R.id.textView64)
        tv_from = view.findViewById(R.id.textView62)
        tv_to = view.findViewById(R.id.textView63)
        tv_location = view.findViewById(R.id.textView19)
        skillContainer = view.findViewById(R.id.chipGroupMyAd)

        vm.myAdvertisements.observe(viewLifecycleOwner){ myAdvertisements ->
            val ad = myAdvertisements.find { it.id==advertisementID }
            ad?.let {
                shownAdvertisement = it

                tv_title.text = it.title
                tv_description.text = it.description
                tv_description.text = "No description provided"
                tv_date.text = it.date
                tv_from.text = it.from
                tv_to.text = it.to
                tv_location.text = it.location

                skillContainer.removeAllViews()
                it.skills.forEach { skillName ->
                    val inflater: LayoutInflater = layoutInflater
                    val skill: Chip = inflater.inflate(R.layout.profile_skill_chip, null) as Chip
                    skill.text = skillName
                    skill.isCloseIconVisible = false
                    skillContainer.addView(skill)
                }

                val advertisementStatus = view.findViewById<TextView>(R.id.textView95)
                val ratingPanel = view.findViewById<LinearLayout>(R.id.ratingSlotLayout)
                val ratingDonePanel = view.findViewById<LinearLayout>(R.id.ratingDoneContainer)
                val chatButton = view.findViewById<Button>(R.id.button11)
                val btn_submitRate = view.findViewById<Button>(R.id.button9)

                chatButton.setOnClickListener {
                    startChat()
                }

                if(it.advertiserRating==null) {

                    ratingDonePanel.isGone = true

                    when (it.status) {
                        //If the adv is free -> it shows the ui for booking
                        "booked" -> {
                            ratingPanel.isGone = true
                            ratingDonePanel.isGone = true

                            chatButton.isVisible = true

                            shownAdvertisementSkill = it.bookedSkill
                        }
                        "complete" -> {

                            ratingPanel.isVisible = true
                            ratingDonePanel.isGone = true
                            chatButton.isVisible = true

                            shownAdvertisementSkill = it.bookedSkill

                            advertisementStatus.text = "Job completed"

                            Firebase.auth.currentUser!!.uid

                            btn_submitRate.setOnClickListener { _ ->
                                if (view.findViewById<RatingBar>(R.id.ratingBar2).rating == 0f) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please select a rating",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@setOnClickListener
                                }

                                val rating = Rating().apply {
                                    this.rating =
                                        view.findViewById<RatingBar>(R.id.ratingBar2).rating
                                    this.advertisement = it
                                    this.textRating =
                                        view.findViewById<TextView>(R.id.RateTextId).text.toString()
                                    this.raterUid = Firebase.auth.currentUser!!.uid
                                    this.timestamp = System.currentTimeMillis()

                                    //i am the advertiser
                                    this.raterName = it.user.fullname
                                }

                                submitRating(rating)
                            }
                        }
                        //FREE
                        else -> {
                            ratingPanel.isGone = true
                            ratingDonePanel.isGone = true
                            chatButton.isGone = true

                            advertisementStatus.text = "Available for booking"
                        }
                    }
                }
                else{
                    chatButton.isVisible = true
                    ratingPanel.isGone = true
                    chatButton.isGone = true

                    shownAdvertisementSkill = it.bookedSkill

                    if(it.advertiserRating==null && it.clientRating==null){
                        advertisementStatus.text = "Awaiting client & advertiser ratings"
                    }
                    else if(it.advertiserRating==null){
                        advertisementStatus.text = "Awaiting advertiser rating"
                    }
                    else if(it.clientRating==null){
                        advertisementStatus.text = "Awaiting client rating"
                    }
                    else{
                        advertisementStatus.text = "Successfully completed"
                    }

                    ratingDonePanel.isVisible = true
                    ratingDonePanel.findViewById<TextView>(R.id.textView93).text = "Congratulations! You have already rated & reviewed the client."
                }

                requireActivity().invalidateOptionsMenu()

            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    private fun startChat() {
        val bundle = bundleOf(
            "advertisement" to shownAdvertisement,
            "advertisementSkill" to shownAdvertisementSkill,
            "clientUID" to shownAdvertisement!!.bookedByUID
        )
        view?.findNavController()?.navigate(R.id.action_myAdvertisementDetails_to_chatFragment, bundle)
    }

    private fun submitRating(rating: Rating) {
        onlineAdDetailsVM.rateClient(rating)
        Toast.makeText(requireContext(),"Rating submitted",Toast.LENGTH_SHORT).show()
    }

    fun deleteAdvertisement() {
        vm.deleteAdvertisement(advertisementID, context)
    }
}