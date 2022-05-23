package it.polito.ma.g14.timebank.RVadapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM
import java.text.SimpleDateFormat

class MyAdvertisementsAdapter(
    val view: View,
    val vm: FirebaseVM,
    val context: Context
): RecyclerView.Adapter<MyAdvertisementsAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<Advertisement>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0

    private var sortBy: String = ""

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val advertisementContainer = v.findViewById<LinearLayout>(R.id.my_ad_card)

        fun bind(advertisement: Advertisement, inflater: LayoutInflater, color: String, action1: (v: View) -> Unit, action2: (v: View)->Unit, action3: (v: View)->Unit) {
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

            val skillContainer = advertisementContainer.findViewById<ChipGroup>(R.id.chipGroupMyAd)
            skillContainer.removeAllViews()
            advertisement.skills.forEach { skillName ->

                val skill: Chip = inflater.inflate(R.layout.profile_skill_chip, null) as Chip
                skill.text = skillName
                skill.isCloseIconVisible = false
                skillContainer.addView(skill)
            }

            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(action1)
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton3).setOnClickListener(action2)
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton4).setOnClickListener(action3)
        }
        fun unbind() {
            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(null)
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton3).setOnClickListener(null)
            advertisementContainer.findViewById<ImageButton>(R.id.imageButton4).setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.my_ad_card,parent, false)
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
            Toast.makeText(context, "Advertisement deleted", Toast.LENGTH_SHORT).show()
        }

        fun action3(view: View) {
            val bundle = bundleOf("advertisementID" to advertisement.id, "operationType" to "edit_time_slot", "originFragment" to "list_time_slot")
            view.findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
        }

        val inflater = LayoutInflater.from(context)

        holder.bind(advertisement, inflater, colorList[(colorIndex++)%colorList.size], {action1(view)}, {action2(view)}, {action3(view)})
    }

    override fun getItemCount(): Int = displayData.size

    fun updateAdvertisements(advertisements: List<Advertisement>, sortBy: String){
        this.sortBy = sortBy
        colorIndex = 0
        data = advertisements.toList()
        val newData = performSort(sortBy, false)
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyAdvertisements(displayData.toList(), newData))
        displayData = newData.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String): Int {
        if(data.isEmpty()){
            return 0
        }
        val newData: MutableList<Advertisement>
        val allData = performSort(sortBy, false)
        if(text.isEmpty() || text.isBlank()){
            newData = allData.toMutableList()
        }
        else{
            newData = allData.filter {ad ->

                if(ad.title.contains(text, ignoreCase = true) ||
                    ad.location.contains(text, ignoreCase = true) ||
                    ad.description.contains(text, ignoreCase = true)){
                    return@filter true
                }
                return@filter false

            } as MutableList<Advertisement>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyAdvertisements(displayData, newData))
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
        val newData = data as MutableList<Advertisement>
        when(sortBy){
            "title_asc" -> { newData.sortBy { it.title }
                if(showToast) Toast.makeText(context, "Sorted by title A-Z", Toast.LENGTH_SHORT).show()
            }
            "title_desc" -> { newData.sortByDescending { it.title }
                if(showToast) Toast.makeText(context, "Sorted by title Z-A", Toast.LENGTH_SHORT).show()
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
                newData.sortedWith(cmp)
                if(showToast) {
                    Toast.makeText(context, "Sorted by most recent", Toast.LENGTH_SHORT).show()
                }
            }
            "date_asc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareBy<Advertisement> { sdf_date.parse(it.date) }.thenBy { sdf_time.parse(it.from) }
                newData.sortedWith(cmp)
                if(showToast) {
                    Toast.makeText(context, "Sorted by oldest", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return newData
    }
}

class MyDiffCallbackMyAdvertisements(val old: List<Advertisement>, val new: List<Advertisement>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
