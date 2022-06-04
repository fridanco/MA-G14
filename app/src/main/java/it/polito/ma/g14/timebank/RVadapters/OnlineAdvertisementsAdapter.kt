package it.polito.ma.g14.timebank.RVadapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM
import java.text.SimpleDateFormat

class OnlineAdvertisementsAdapter(val view: View, val vm: FirebaseVM, val context: Context, val sourceUser: String, val selectedSkill: String?): RecyclerView.Adapter<OnlineAdvertisementsAdapter.ItemViewHolder>() {
    var data = listOf<Advertisement>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0
    var sort = 0

    private var sortBy = ""

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val advertisementContainer = v.findViewById<LinearLayout>(R.id.online_ad_card)

        fun bind(advertisement: Advertisement, context: Context, vm: FirebaseVM, color: String, action1: (v: View) -> Unit) {
            val userIcon = advertisementContainer.findViewById<ImageView>(R.id.imageView6)

            val profileImageRef = vm.storageRef.child(advertisement.uid)

            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(context)
                .load(profileImageRef)
                .apply(options)
                .into(userIcon)


            if(advertisement.uid!=Firebase.auth.currentUser!!.uid) {
                advertisementContainer.findViewById<TextView>(R.id.textView74).text = "By ${advertisement.user.fullname}"
            }
            else{
                advertisementContainer.findViewById<TextView>(R.id.textView74).text = "By You"
            }
            advertisementContainer.findViewById<TextView>(R.id.textView4).text = advertisement.title
            advertisementContainer.findViewById<TextView>(R.id.textView5).text = advertisement.description
            advertisementContainer.findViewById<TextView>(R.id.textView6).text = advertisement.date
            advertisementContainer.findViewById<TextView>(R.id.textView7).text = "${advertisement.from} - ${advertisement.to}"
            advertisementContainer.findViewById<TextView>(R.id.textView19).text = advertisement.location
            advertisementContainer.findViewById<LinearLayout>(R.id.cardColor).setBackgroundColor(Color.parseColor(color))
            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(action1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.online_ad_card,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val advertisement = displayData[position]

        holder.bind(advertisement, context, vm, colorList[(colorIndex++)%colorList.size]) {

            val advertisementSkill = if(selectedSkill!=null) {
                selectedSkill
            }
            else{
                advertisement.bookedSkill
            }

            val bundle = bundleOf("advertisement" to advertisement, "advertisementSkill" to advertisementSkill)
            if(sourceUser == "online") {
                view.findNavController().navigate(R.id.action_onlineAdsListFragment_to_onlineAdDetailsFragment, bundle)
            }
            else {
                view.findNavController().navigate(R.id.action_nav_linkedAds_to_onlineAdDetailsFragment, bundle)
            }

        }
    }

    override fun getItemCount(): Int = displayData.size

    fun updateAdvertisements(advertisements: List<Advertisement>, sortBy: String){
        this.sortBy = sortBy
        colorIndex = 0
        data = advertisements.toList()
        val newData = performSort(sortBy, false)
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackOnlineAdvertisements(displayData,newData))
        displayData = newData.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) : Int {
        if(data.isEmpty()){
            return 0
        }
        val newData: MutableList<Advertisement>
        val allData = performSort(sortBy, false)
        newData = if(text.isEmpty() || text.isBlank()){
            allData.toMutableList()
        } else{
            allData.filter {ad ->

                if(ad.title.contains(text, ignoreCase = true) ||
                    ad.location.contains(text, ignoreCase = true) ||
                    ad.description.contains(text, ignoreCase = true)){
                    return@filter true
                }
                return@filter false

            } as MutableList<Advertisement>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackOnlineAdvertisements(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
        return displayData.size
    }

    fun addSort(sortBy: String){
        this.sortBy = sortBy
        if(displayData.isEmpty()){
            return
        }
        val dataTmp = data
        data = displayData.toList()
        val newData = performSort(sortBy)
        data = dataTmp
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackOnlineAdvertisements(displayData, newData))
        displayData = newData.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }

    fun performSort(sortBy: String, showToast: Boolean = true) : List<Advertisement>{
        var newData = data.toMutableList()
        when(sortBy){
            "title_asc" -> { newData.sortBy { it.title }
                if(showToast) Toast.makeText(context, "Sorted by title A-Z", Toast.LENGTH_SHORT).show()
            }
            "title_desc" -> { newData.sortByDescending { it.title }
                if(showToast) Toast.makeText(context, "Sorted by title Z-A", Toast.LENGTH_SHORT).show()
            }
            "creator_asc" -> { newData.sortBy { it.user.fullname }
                if(showToast) Toast.makeText(context, "Sorted by creator A-Z", Toast.LENGTH_SHORT).show()
            }
            "creator_desc" -> { newData.sortByDescending { it.user.fullname }
                if(showToast) Toast.makeText(context, "Sorted by creator Z-A", Toast.LENGTH_SHORT).show()
            }
            "location_asc" -> { newData.sortBy { it.location }
                if(showToast) Toast.makeText(context, "Sorted by location A-Z", Toast.LENGTH_SHORT).show()
            }
            "location_desc" -> { newData.sortByDescending { it.location }
                if(showToast) Toast.makeText(context, "Sorted by location Z-A", Toast.LENGTH_SHORT).show()
            }
            "date_desc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareByDescending<Advertisement> { sdf_date.parse(it.date) }.thenByDescending { sdf_time.parse(it.from) }
                newData = newData.sortedWith(cmp).toMutableList()
                if(showToast) {
                    Toast.makeText(context, "Sorted by most recent", Toast.LENGTH_SHORT).show()
                }
            }
            "date_asc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareBy<Advertisement> { sdf_date.parse(it.date) }.thenBy { sdf_time.parse(it.from) }
                newData = newData.sortedWith(cmp).toMutableList()
                if(showToast) {
                    Toast.makeText(context, "Sorted by oldest", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return newData
    }
}

class MyDiffCallbackOnlineAdvertisements(val old: List<Advertisement>, val new: List<Advertisement>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
