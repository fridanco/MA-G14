package it.polito.ma.g14.timebank.RVadapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM

class OnlineAdvertisementsAdapter(val view: View, val vm: FirebaseVM): RecyclerView.Adapter<OnlineAdvertisementsAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<Advertisement>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val advertisementContainer = v.findViewById<LinearLayout>(R.id.online_ad_card)

        fun bind(advertisement: Advertisement, color: String, action1: (v: View) -> Unit) {
            advertisementContainer.findViewById<TextView>(R.id.textView74).text = "By ${advertisement.user.fullname}"
            advertisementContainer.findViewById<TextView>(R.id.textView4).text = advertisement.title
            advertisementContainer.findViewById<TextView>(R.id.textView5).text = advertisement.description
            advertisementContainer.findViewById<TextView>(R.id.textView6).text = advertisement.date
            advertisementContainer.findViewById<TextView>(R.id.textView7).text = "${advertisement.from} - ${advertisement.to}"
            advertisementContainer.findViewById<TextView>(R.id.textView19).text = advertisement.location
            advertisementContainer.findViewById<LinearLayout>(R.id.cardColor).setBackgroundColor(Color.parseColor(color))
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton3).backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(color))
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton4).backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(color))
            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(action1)
        }
        fun unbind() {
            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(null)
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

        holder.bind(advertisement, colorList[(colorIndex++)%colorList.size]) {
            val bundle = bundleOf("advertisementID" to advertisement.id)
            view.findNavController().navigate(R.id.action_onlineAdsListFragment_to_onlineAdDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int = displayData.size

    fun updateAdvertisements(timeSlots: List<Advertisement>){
        colorIndex = 0
        data = timeSlots
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackOnlineAdvertisements(displayData as List<Advertisement>,data))
        displayData = data as MutableList<Advertisement>
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) {
        var newData = mutableListOf<Advertisement>()
//        if(text.isEmpty() || text.isBlank()){
//            newData = data
//        }
//        else{
//            newData = data.filter { it.name.contains(text, ignoreCase = true) } as MutableList<TimeSlot>
//        }
        newData = data as MutableList<Advertisement>
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackOnlineAdvertisements(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
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
