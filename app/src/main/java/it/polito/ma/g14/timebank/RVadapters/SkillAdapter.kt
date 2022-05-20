package it.polito.ma.g14.timebank.RVadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.SkillEntry

class SkillAdapter(val data: MutableList<SkillEntry>): RecyclerView.Adapter<SkillAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var displayData = data.toMutableList()
    var checked_skills = mutableListOf<String>()


    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val skill: CheckedTextView = v.findViewById(R.id.simpleCheckedTextView)

        fun bind(skillEntry: SkillEntry, action: (v: View)->Unit) {
            this.skill.text = skillEntry.name
            this.skill.isChecked = skillEntry.active
            this.skill.setOnClickListener(action)
        }
        fun unbind() {
            skill.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.profile_skills_checkedtext,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = displayData[position]
        holder.bind(item) {

            val pos = data.indexOf(item)
            if (pos!=-1) {
                if(!(holder.itemView as CheckedTextView).isChecked){
                    (holder.itemView as CheckedTextView).isChecked = true
                    checked_skills.add(data[pos].name)
                }
                else{
                    (holder.itemView as CheckedTextView).isChecked = false
                    checked_skills.remove(data[pos].name)
                }

            }
        }
    }

    override fun getItemCount(): Int = displayData.size


    fun addFilter(text: String) {
        val newData: MutableList<SkillEntry>
        if(text.isEmpty() || text.isBlank()){
            newData = data
        }
        else{
            newData = data.filter { it.name.contains(text, ignoreCase = true) } as MutableList<SkillEntry>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackSkills(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
    }

    fun updateSelectedSkills(checkedSkills: List<String>){
        checked_skills = checkedSkills as MutableList<String>
        val newData = data
        for(skillItem in checkedSkills){
            newData.find{ it.name==skillItem}?.active = true
        }
        notifyDataSetChanged()
    }
}

class MyDiffCallbackSkills(val old: List<SkillEntry>, val aNew: List<SkillEntry>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = aNew.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === aNew[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == aNew[newItemPosition]
    }
}