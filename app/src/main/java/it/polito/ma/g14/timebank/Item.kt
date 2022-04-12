package it.polito.listapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polito.ma.g14.timebank.R

data class Item(val name:String, var active:Boolean )

class ItemAdapter(val data:MutableList<Item>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var displayData = data.toMutableList()
    var checked_skills = mutableListOf<String>()


    class ItemViewHolder(v:View): RecyclerView.ViewHolder(v) {
        private val skill: CheckedTextView = v.findViewById(R.id.simpleCheckedTextView)

        fun bind(item:Item, action: (v:View)->Unit) {
            skill.text = item.name
            skill.isChecked = item.active
            skill.setOnClickListener(action)
        }
        fun unbind() {
            skill.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.skills_entry,parent, false)
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
        var newData = mutableListOf<Item>()
        if(text.isEmpty() || text.isBlank()){
            newData = data
        }
        else{
            newData = data.filter { it.name.contains(text, ignoreCase = true) } as MutableList<Item>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallback(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
    }
}

class MyDiffCallback(val old: List<Item>, val new: List<Item>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}