package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.ProfileRatingAdapter
import it.polito.ma.g14.timebank.models.ShowProfileRatingVM

class ShowProfileRatingFragment(val uid: String, val type: String, val originFragment: String) : Fragment() {

    private val showProfileRatingVM by viewModels<ShowProfileRatingVM>()

    lateinit var adapter: ProfileRatingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_profile_rating, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProfileRatingVM.getProfileReviews(uid)

        val rv = view.findViewById<RecyclerView>(R.id.ratingRV)
        val emptyRv = view.findViewById<TextView>(R.id.emptyRatingRV)

        val overallRatingContainer = view.findViewById<LinearLayout>(R.id.overallRatingContainer)
        val numRatingsContainer = view.findViewById<LinearLayout>(R.id.numRatingsContainer)

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProfileRatingAdapter(view, requireContext(), uid, originFragment)
        rv.adapter = adapter

        showProfileRatingVM.profile.observe(viewLifecycleOwner){
            if(type=="asAdvertiser"){
                if(it.ratingsAsAdvertiser.isEmpty()){
                    overallRatingContainer.isGone = true
                    numRatingsContainer.isGone = true
                    rv.isGone = true
                    emptyRv.isVisible = true
                    if(uid==Firebase.auth.currentUser!!.uid) {
                        emptyRv.text = "You have not received any rating as an advertiser"
                    }
                    else{
                        emptyRv.text = "This user has not received any rating as an advertiser"
                    }
                }
                else {
                    overallRatingContainer.isVisible = true
                    numRatingsContainer.isVisible = true
                    rv.isVisible = true
                    emptyRv.isGone = true
                    overallRatingContainer.findViewById<RatingBar>(R.id.overallRating).rating = (it.ratingsAsAdvertiser.sumOf { it.rating.toDouble() } / it.ratingsAsAdvertiser.size).toFloat()
                    numRatingsContainer.findViewById<TextView>(R.id.numRatings).text = it.ratingsAsAdvertiser.size.toString()
                    adapter.updateRatings(it.ratingsAsAdvertiser)
                }
            }
            else{
                if(it.ratingsAsClient.isEmpty()){
                    overallRatingContainer.isGone = true
                    numRatingsContainer.isGone = true
                    rv.isGone = true
                    emptyRv.isVisible = true
                    if(uid==Firebase.auth.currentUser!!.uid) {
                        emptyRv.text = "You have not received any rating as a client"
                    }
                    else{
                        emptyRv.text = "This user has not received any rating as a client"
                    }
                }
                else {
                    overallRatingContainer.isVisible = true
                    numRatingsContainer.isVisible = true
                    rv.isVisible = true
                    emptyRv.isGone = true
                    overallRatingContainer.findViewById<RatingBar>(R.id.overallRating).rating = (it.ratingsAsClient.sumOf { it.rating.toDouble() } / it.ratingsAsClient.size).toFloat()
                    numRatingsContainer.findViewById<TextView>(R.id.numRatings).text = it.ratingsAsClient.size.toString()
                    adapter.updateRatings(it.ratingsAsClient)
                }
            }
        }

    }

}