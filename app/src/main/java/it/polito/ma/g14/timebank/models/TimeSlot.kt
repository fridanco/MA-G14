package it.polito.ma.g14.timebank.models

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
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.fragments.Advertisement
import it.polito.ma.g14.timebank.fragments.FirebaseVM
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

