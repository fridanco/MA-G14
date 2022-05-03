package it.polito.ma.g14.timebank.models

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
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.fragments.TimeSlotVM

@Entity(tableName = "time_slots")
class TimeSlot {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    var title : String = ""
    var description : String = ""
    var date : String = ""
    var from : String = ""
    var to : String = ""
    var location : String = ""

    override fun toString(): String = "{ id:$id, title:$title, description:$description, date:${date.toString()}, from:$from, to:$to, location:$location }"
}

class TimeSlotAdapter(val view: View, val vm: TimeSlotVM): RecyclerView.Adapter<TimeSlotAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<TimeSlot>()
    var displayData = data.toMutableList()

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val timeSlotContainer = v.findViewById<LinearLayout>(R.id.time_slot_container)

        fun bind(timeSlot: TimeSlot, action1: (v: View) -> Unit, action2: (v: View)->Unit, action3: (v: View)->Unit) {
            timeSlotContainer.findViewById<TextView>(R.id.textView4).text = timeSlot.title
            timeSlotContainer.findViewById<TextView>(R.id.textView5).text = timeSlot.description
            timeSlotContainer.findViewById<TextView>(R.id.textView6).text = timeSlot.date
            timeSlotContainer.findViewById<TextView>(R.id.textView7).text = "${timeSlot.from} - ${timeSlot.to}"
            timeSlotContainer.findViewById<TextView>(R.id.textView19).text = timeSlot.location
            timeSlotContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(action1)
            timeSlotContainer.findViewById<ImageButton>(R.id.imageButton3).setOnClickListener(action2)
            timeSlotContainer.findViewById<ImageButton>(R.id.imageButton4).setOnClickListener(action3)
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

        fun action1(view: View) {
            val bundle = bundleOf("timeSlotID" to timeSlot.id)
            view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, bundle)
        }
        fun action2(view: View) {
            vm.deleteTimeSlot(timeSlot.id)
        }

        fun action3(view: View) {
            val bundle = bundleOf("timeSlotID" to timeSlot.id, "operationType" to "edit_time_slot", "originFragment" to "list_time_slot")
            view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
        }

        holder.bind(timeSlot, {action1(view)}, {action2(view)}, {action3(view)})
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
