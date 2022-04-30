package it.polito.ma.g14.timebank.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.ma.g14.timebank.R

@Entity(tableName = "skills")
class Skill {
    @PrimaryKey(autoGenerate = false)
    var skill : String = ""
}

