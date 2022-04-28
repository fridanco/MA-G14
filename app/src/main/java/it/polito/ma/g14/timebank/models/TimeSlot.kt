package it.polito.ma.g14.timebank.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.listapplication.Item
import it.polito.ma.g14.timebank.R
import java.util.*

@Entity(tableName = "time_slots")
class TimeSlot {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    var title : String = ""
    var description : String = ""
    var dateTime : String = ""
    var duration : Int = 0
    var location : String = ""

    override fun toString(): String = "{ id:$id, title:$title, description:$description, dateTime:${dateTime.toString()}, duration:$duration, location:$location }"
}

class TimeSlotAdapter(val view: View): RecyclerView.Adapter<TimeSlotAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<TimeSlot>()
    var displayData = data.toMutableList()

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val timeSlotContainer = v.findViewById<LinearLayout>(R.id.time_slot_container)

        fun bind(timeSlot: TimeSlot, action: (v: View)->Unit) {
            timeSlotContainer.findViewById<TextView>(R.id.textView4).text = timeSlot.title
            timeSlotContainer.findViewById<TextView>(R.id.textView5).text = timeSlot.description
            timeSlotContainer.findViewById<TextView>(R.id.textView6).text = timeSlot.dateTime
            timeSlotContainer.findViewById<TextView>(R.id.textView7).text = timeSlot.duration.toString()
            timeSlotContainer.findViewById<TextView>(R.id.textView19).text = timeSlot.location
            timeSlotContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(action)
        }
        fun unbind() {
            timeSlotContainer.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.time_slot_entry,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val timeSlot = displayData[position]
        holder.bind(timeSlot) {
            val bundle = bundleOf("timeSlotID" to timeSlot.id)
            view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int = displayData.size

    fun updateTimeSlots(timeSlots : List<TimeSlot>){
        data = timeSlots
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData, data))
        displayData = data as MutableList<TimeSlot>
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) {
        var newData = mutableListOf<TimeSlot>()
//        if(text.isEmpty() || text.isBlank()){
//            newData = data
//        }
//        else{
//            newData = data.filter { it.name.contains(text, ignoreCase = true) } as MutableList<TimeSlot>
//        }
        newData = data as MutableList<TimeSlot>
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
    }
}

class MyDiffCallback(val old: List<TimeSlot>, val new: List<TimeSlot>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
