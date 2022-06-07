package it.polito.ma.g14.timebank.RVadapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Rating
import java.text.SimpleDateFormat
import java.util.*

class ProfileRatingAdapter(val view: View, val context: Context, val uid: String): RecyclerView.Adapter<ProfileRatingAdapter.ItemViewHolder>() {
    var data = listOf<Rating>()
    var displayData = data.toMutableList()

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val ratingContainer = v.findViewById<LinearLayout>(R.id.ratingContainer)

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(rating: Rating, context: Context, action1: (v: View) -> Unit) {

            val raterIcon = ratingContainer.findViewById<ImageView>(R.id.raterImg)
            val raterImageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference.child(rating.raterUid)

            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(context)
                .load(raterImageRef)
                .apply(options)
                .into(raterIcon)

            ratingContainer.findViewById<TextView>(R.id.raterName).text = rating.raterName

            val dateTime = Date(rating.timestamp)
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = dateTime
            val today: Calendar = Calendar.getInstance()
            val timeFormatter1 = SimpleDateFormat("HH:mm")
            val timeFormatter2 = SimpleDateFormat("EEE")
            val timeFormatter3 = SimpleDateFormat("EEE dd")
            val timeFormatter4 = SimpleDateFormat("MMM")
            val timeFormatter5 = SimpleDateFormat("MMM yyyy")

            val timeString: String

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                timeString = timeFormatter1.format(dateTime)
            }
            else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)) {
                timeString = timeFormatter2.format(dateTime)
            }
            else if(calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH)==today.get(Calendar.MONTH)) {
                timeString = timeFormatter3.format(dateTime)
            }
            else if(calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR)) {
                timeString = timeFormatter4.format(dateTime)
            }
            else{
                timeString = timeFormatter5.format(dateTime)
            }
            ratingContainer.findViewById<TextView>(R.id.ratingDate).text = timeString

            ratingContainer.findViewById<RatingBar>(R.id.raterRating).rating = rating.rating
            if(rating.textRating.isNotBlank()) {
                ratingContainer.findViewById<TextView>(R.id.raterReview).text = rating.textRating
            }
            else{
                ratingContainer.findViewById<TextView>(R.id.raterReview).text = "No review provided"
            }
            ratingContainer.findViewById<LinearLayout>(R.id.raterProfileContainer).setOnClickListener(action1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.profile_rating_entry,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val rating = displayData[position]

        holder.bind(rating, context) {
            val bundle = bundleOf("uid" to rating.raterUid)
            if(rating.raterUid==Firebase.auth.currentUser!!.uid) {
                view.findNavController()
                    .navigate(R.id.action_showProfileAdFragment_to_myProfile, bundle)
            }
            else{
                view.findNavController()
                    .navigate(R.id.action_showProfileAdFragment_self, bundle)
            }
        }
    }

    override fun getItemCount(): Int = displayData.size

    fun updateRatings(ratings: List<Rating>){
        val newData = ratings.sortedByDescending { it.timestamp }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackProfileRating(displayData, newData))
        displayData = newData.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }
}

class MyDiffCallbackProfileRating(val old: List<Rating>, val new: List<Rating>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}