package it.polito.ma.g14.timebank.fragments

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.MyDiffCallbackOnlineAdvertisements
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.Skill
import it.polito.ma.g14.timebank.models.SkillAdvertisement
import java.text.SimpleDateFormat

class SkillAdvertisementAdapter(val view: View, val context: Context, val sortByKey: String): RecyclerView.Adapter<SkillAdvertisementAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<SkillAdvertisement>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val skillAdvertisementContainer = v.findViewById<LinearLayout>(R.id.skill_advertisement_container)

        fun bind(skillAdvertisement: SkillAdvertisement, color: String, action1: (v: View) -> Unit) {
            skillAdvertisementContainer.findViewById<TextView>(R.id.textView4).text = skillAdvertisement.skill
            skillAdvertisementContainer.findViewById<TextView>(R.id.textView5).text = "${skillAdvertisement.numAdvertisements} advertisements"
            skillAdvertisementContainer.findViewById<LinearLayout>(R.id.cardColor).setBackgroundColor(
                Color.parseColor(color))
            skillAdvertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(action1)
        }
        fun unbind() {
            skillAdvertisementContainer.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.skill_advertisement_entry,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val skillAdvertisement = displayData[position]

        fun action1(view: View) {
            val bundle = bundleOf("selectedSkill" to skillAdvertisement.skill, "operationType" to "online_advertisements")
            view.findNavController().navigate(R.id.action_advertisement_skills_to_onlineAdsListFragment, bundle)
        }

        holder.bind(skillAdvertisement, colorList[(colorIndex++)%colorList.size]) { action1(view) }
    }

    override fun getItemCount(): Int = displayData.size

    fun updateSkillAdvertisements(skillAdvertisements: List<SkillAdvertisement>){
        colorIndex = 0
        data = skillAdvertisements
        val newData = performSort(sortByKey)
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData, newData))
        displayData = data as MutableList<SkillAdvertisement>
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) {
        var newData = mutableListOf<SkillAdvertisement>()
        if(text.isEmpty() || text.isBlank()){
            newData = data as MutableList<SkillAdvertisement>
        }
        else{
            newData = data.filter { it.skill.contains(text, ignoreCase = true) } as MutableList<SkillAdvertisement>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
    }

    fun addSort(sortBy: String){
        data = displayData.toList()
        val newData = performSort(sortBy)
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData, newData))
        displayData = newData as MutableList<SkillAdvertisement>
        diffs.dispatchUpdatesTo(this)
    }

    fun performSort(sortBy: String) : List<SkillAdvertisement>{
        val newData = data as MutableList<SkillAdvertisement>
        when(sortBy){
            "skill_asc" -> { newData.sortBy { it.skill }; Toast.makeText(context, "Sorted by skill A-Z", Toast.LENGTH_SHORT).show() }
            "skill_desc" -> { newData.sortByDescending { it.skill }; Toast.makeText(context, "Sorted by skill Z-A", Toast.LENGTH_SHORT).show() }
            "numAd_asc" -> { newData.sortBy { it.numAdvertisements }; Toast.makeText(context, "Sorted by least number of ads", Toast.LENGTH_SHORT).show() }
            "numAd_desc" -> { newData.sortByDescending { it.numAdvertisements }; Toast.makeText(context, "Sorted by most number of ads", Toast.LENGTH_SHORT).show() }
        }
        return newData
    }
}

class MyDiffCallback(val old: List<SkillAdvertisement>, val new: List<SkillAdvertisement>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}