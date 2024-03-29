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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.OnlineAdDetailsVM
import it.polito.ma.g14.timebank.models.Rating
import it.polito.ma.g14.timebank.utils.Utils


class OnlineAdDetailsFragment : Fragment() {

    private val onlineAdDetailsVM by viewModels<OnlineAdDetailsVM>()
    private val vm by viewModels<FirebaseVM>()

    lateinit var tv_title: TextView
    lateinit var tv_description: TextView
    lateinit var tv_date: TextView
    lateinit var tv_from: TextView
    lateinit var tv_to: TextView
    lateinit var tv_location: TextView
    lateinit var tv_fullname: TextView
    lateinit var tv_user_description: TextView
    lateinit var iv_profileImage: ImageView
    lateinit var tv_skill_label: TextView
    lateinit var tv_skill: TextView
    lateinit var btn_book: Button
    lateinit var btn_chat: Button
    lateinit var btn_markAsComplete: Button
    lateinit var btn_submitRate: Button
    lateinit var user: LinearLayout

    lateinit var shownAdvertisement: Advertisement
    lateinit var shownAdvertisementSkill: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_online_ad_details, container, false)
        requireActivity().invalidateOptionsMenu()

        val advertisement = requireArguments().getSerializable("advertisement") as Advertisement
        shownAdvertisementSkill = requireArguments().getString("advertisementSkill").toString()

        onlineAdDetailsVM.getAdvertisement(advertisement.id, Firebase.auth.currentUser!!.uid)

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_title = view.findViewById(R.id.textView4)
        tv_description = view.findViewById(R.id.textView5)
        tv_date = view.findViewById(R.id.textView64)
        tv_from = view.findViewById(R.id.textView62)
        tv_to = view.findViewById(R.id.textView63)
        tv_location = view.findViewById(R.id.textView19)
        tv_fullname = view.findViewById(R.id.textView77)
        tv_user_description = view.findViewById(R.id.textView74)
        tv_skill_label = view.findViewById(R.id.textView82)
        tv_skill = view.findViewById(R.id.textView91)
        iv_profileImage = view.findViewById(R.id.imageView6)
        btn_book = view.findViewById(R.id.button7)
        btn_chat = view.findViewById(R.id.button8)
        btn_markAsComplete = view.findViewById(R.id.button10)
        btn_submitRate = view.findViewById<Button>(R.id.button9)
        user = view.findViewById(R.id.user_link)

        onlineAdDetailsVM.advertisement.observe(viewLifecycleOwner) { adUserPair ->

            shownAdvertisement = adUserPair.first
            
            val profileImageRef = vm.storageRef.child(adUserPair.first.uid)

            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(requireContext())
                .load(profileImageRef)
                .apply(options)
                .into(iv_profileImage)


            tv_title.text = adUserPair.first.title
            tv_description.text = adUserPair.first.description
            tv_date.text = adUserPair.first.date
            tv_from.text = adUserPair.first.from
            tv_to.text = adUserPair.first.to
            tv_location.text = adUserPair.first.location
            tv_fullname.text = "By ${adUserPair.first.user.fullname}"
            if (adUserPair.first.user.description.isNotEmpty()) {
                tv_user_description.text = adUserPair.first.user.description
            }
            else {
                tv_user_description.text = "No description provided"
            }

            if(adUserPair.first.bookedSkill.isNotBlank()){
                tv_skill_label.isVisible = true
                tv_skill.isVisible = true
                tv_skill.text = adUserPair.first.bookedSkill
            }
            else{
                tv_skill_label.isGone = true
                tv_skill.isGone = true
            }

            user.setOnClickListener {
                redirectToAdvertiserProfile(adUserPair.first.uid)
            }

            val advertisementStatus = view.findViewById<TextView>(R.id.textView95)
            val bookingPanel = view.findViewById<LinearLayout>(R.id.bookChatContainer)
            val ratingPanel = view.findViewById<LinearLayout>(R.id.ratingSlotLayout)
            val completedPanel = view.findViewById<LinearLayout>(R.id.completedSlotLayout)
            val ratingDonePanel = view.findViewById<LinearLayout>(R.id.ratingDoneContainer)
            val chatButton = view.findViewById<Button>(R.id.button11)

            chatButton.setOnClickListener {
                startChat()
            }

            val iAmAdvertiser = Firebase.auth.currentUser!!.uid == adUserPair.first.uid
            val iAmClient = !iAmAdvertiser

            if((iAmAdvertiser && adUserPair.first.advertiserRating==null) ||
                    iAmClient && adUserPair.first.clientRating==null) {

                ratingDonePanel.isGone = true

                when (adUserPair.first.status) {
                    //If the adv is free -> it shows the ui for booking
                    "booked" -> {

                        chatButton.isVisible = true

                        //I booked the advertisement
                        if(adUserPair.first.bookedByUID==Firebase.auth.currentUser!!.uid){
                            advertisementStatus.text = "Booked by you"
                        }
                        //A client booked the advertisement
                        else{
                            advertisementStatus.text = "Booked by ${adUserPair.first.bookedByName}"
                        }

                        bookingPanel.isGone = true
                        ratingPanel.isGone = true

                        val bookedAdTextView = view.findViewById<TextView>(R.id.textView88)

                        //If i am the one who booked the advertisement
                        if (adUserPair.first.bookedByUID == Firebase.auth.currentUser!!.uid) {
                            completedPanel.isVisible = true

                            bookedAdTextView.text = "You successfully booked this advertisement"
                            completedPanel.findViewById<Button>(R.id.button10).isVisible = true
                            btn_markAsComplete.setOnClickListener {
                                markAsComplete()
                            }
                        } else {
                            if(adUserPair.first.uid != Firebase.auth.currentUser!!.uid) {
                                bookedAdTextView.text =
                                    "Sorry! This advertisement has been already booked."
                            }
                            completedPanel.findViewById<Button>(R.id.button10).isGone = true
                        }

                    }
                    "complete" -> {

                        chatButton.isVisible = true

                        advertisementStatus.text = "Job completed"

                        bookingPanel.isGone = true
                        completedPanel.isGone = true

                        val userId: String = Firebase.auth.currentUser!!.uid

                        if (adUserPair.first.bookedByUID == userId || adUserPair.first.uid == userId) {
                            ratingPanel.isVisible = true

                            if (adUserPair.first.uid == Firebase.auth.currentUser!!.uid) {
                                ratingPanel.findViewById<TextView>(R.id.textView90).text="Rate the client"
                            } else {
                                ratingPanel.findViewById<TextView>(R.id.textView90).text="Rate the advertiser"
                            }

                            btn_submitRate.setOnClickListener {
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
                                    this.advertisement = adUserPair.first
                                    this.textRating =
                                        view.findViewById<TextView>(R.id.RateTextId).text.toString()
                                    this.raterUid = Firebase.auth.currentUser!!.uid
                                    this.timestamp = System.currentTimeMillis()

                                    //if i am the advertiser
                                    if (adUserPair.first.uid == Firebase.auth.currentUser!!.uid) {
                                        this.raterName = adUserPair.first.user.fullname
                                    }
                                    //if i am the client
                                    else {
                                        this.raterName = adUserPair.first.bookedByName
                                    }
                                }

                                if (adUserPair.first.uid == Firebase.auth.currentUser!!.uid) {
                                    submitRating(rating, "asAdvertiser")
                                } else {
                                    submitRating(rating, "asClient")
                                }
                            }
                        }
                    }
                    //FREE
                    else -> {
                        chatButton.isGone = true

                        advertisementStatus.text = "Available for booking"

                        ratingPanel.isGone = true
                        completedPanel.isGone = true

                        //If i am the client show the booking panel
                        if (adUserPair.first.uid != Firebase.auth.currentUser!!.uid) {
                            bookingPanel.isVisible = true

                            //If enough credits
                            if(adUserPair.second.credits>0) {
                                btn_book.isVisible = true
                                view.findViewById<TextView>(R.id.textView101).isGone = true

                                btn_book.setOnClickListener {
                                    bookSlot()
                                }
                            }
                            else{
                                btn_book.isGone = true
                                view.findViewById<TextView>(R.id.textView101).isVisible = true
                            }

                            btn_chat.setOnClickListener {
                                startChat()
                            }
                        }
                        //if i am the advertiser do not show booking panel
                        else {
                            bookingPanel.isGone = true
                        }
                    }
                }
            }
            else{
                chatButton.isVisible = true

                bookingPanel.isGone = true
                ratingPanel.isGone = true
                completedPanel.isGone = true

                if(adUserPair.first.advertiserRating==null && adUserPair.first.clientRating==null){
                    advertisementStatus.text = "Awaiting client & advertiser ratings"
                }
                else if(adUserPair.first.advertiserRating==null){
                    advertisementStatus.text = "Awaiting advertiser rating"
                }
                else if(adUserPair.first.clientRating==null){
                    advertisementStatus.text = "Awaiting client rating"
                }
                else{
                    advertisementStatus.text = "Successfully completed"
                }

                ratingDonePanel.isVisible = true
                if(iAmAdvertiser){
                    ratingDonePanel.findViewById<TextView>(R.id.textView93).text = "Congratulations! You have already rated & reviewed the client."
                }
                else{
                    ratingDonePanel.findViewById<TextView>(R.id.textView93).text = "Congratulations! You have already rated & reviewed the advertiser."
                }
            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    private fun submitRating(rating: Rating, type: String) {
        if(type=="asAdvertiser"){
            onlineAdDetailsVM.rateClient(rating)
        }
        else{
            onlineAdDetailsVM.rateAdvertiser(rating)
        }
        Toast.makeText(requireContext(),"Rating submitted",Toast.LENGTH_SHORT).show()
    }

    private fun bookSlot() {
        onlineAdDetailsVM.updateAdvertisementBooked(shownAdvertisement, shownAdvertisementSkill, context)
        Toast.makeText(requireContext(),"Advertisement booked",Toast.LENGTH_SHORT).show()
    }

    private fun markAsComplete() {
        onlineAdDetailsVM.updateAdvertisementCompleted(shownAdvertisement)
        Toast.makeText(requireContext(),"Advertisement marked as completed",Toast.LENGTH_SHORT).show()
    }

    private fun startChat() {
        val bundle = bundleOf(
            "advertisement" to shownAdvertisement,
            "advertisementSkill" to shownAdvertisementSkill
        )
        view?.findNavController()?.navigate(R.id.action_onlineAdDetailsFragment_to_chatFragment, bundle)
    }

    private fun redirectToAdvertiserProfile(uid: String) {
        val bundle = bundleOf("uid" to uid)
        view?.findNavController()?.navigate(R.id.action_onlineAdDetailsFragment_to_showProfileAdFragment, bundle)

    }
}