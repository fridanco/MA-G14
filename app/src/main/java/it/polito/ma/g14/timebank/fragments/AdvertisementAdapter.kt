package it.polito.ma.g14.timebank.fragments

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

class AdvertisementAdapter(val view: View, val vm: FirebaseVM): RecyclerView.Adapter<AdvertisementAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<Advertisement>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val advertisementContainer = v.findViewById<LinearLayout>(R.id.time_slot_container)

        fun bind(advertisement: Advertisement, color: String, action1: (v: View) -> Unit, action2: (v: View)->Unit, action3: (v: View)->Unit) {
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
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton3).setOnClickListener(action2)
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton4).setOnClickListener(action3)
        }
        fun unbind() {
            advertisementContainer.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.time_slot_entry,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val advertisement = displayData[position]

        fun action1(view: View) {
            val bundle = bundleOf("advertisementID" to advertisement.id)
            view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, bundle)
        }
        fun action2(view: View) {
            vm.deleteAdvertisement(advertisement.id)
        }

        fun action3(view: View) {
            val bundle = bundleOf("advertisementID" to advertisement.id, "operationType" to "edit_time_slot", "originFragment" to "list_time_slot")
            view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
        }

        holder.bind(advertisement, colorList[(colorIndex++)%colorList.size], {action1(view)}, {action2(view)}, {action3(view)})
    }

    override fun getItemCount(): Int = displayData.size

    fun updateAdvertisements(timeSlots: List<Advertisement>){
        colorIndex = 0
        data = timeSlots
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData as List<SkillAdvertisement>, data as List<SkillAdvertisement>))
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
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackAdvertisements(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
    }
}

class MyDiffCallbackAdvertisements(val old: List<Advertisement>, val new: List<Advertisement>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
