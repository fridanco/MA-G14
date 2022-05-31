package it.polito.ma.g14.timebank.RVadapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.AdvertisementWithChat
import it.polito.ma.g14.timebank.models.FirebaseVM
import java.text.SimpleDateFormat

class MyMessagesAdapter(val view: View, val vm: FirebaseVM, val context: Context, val type: String): RecyclerView.Adapter<MyMessagesAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<AdvertisementWithChat>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0
    var sort = 0

    private var sortBy = ""

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val advertisementContainer = v.findViewById<LinearLayout>(R.id.online_ad_card)

        fun bind(advertisementWithChat: AdvertisementWithChat, context: Context, vm: FirebaseVM, type:String, view: View, color: String) {
            advertisementContainer.findViewById<TextView>(R.id.textView4).text = advertisementWithChat.advertisement.title
            advertisementContainer.findViewById<TextView>(R.id.textView5).text = advertisementWithChat.advertisement.description
            advertisementContainer.findViewById<TextView>(R.id.textView6).text = advertisementWithChat.advertisement.date
            advertisementContainer.findViewById<TextView>(R.id.textView7).text = "${advertisementWithChat.advertisement.from} - ${advertisementWithChat.advertisement.to}"
            advertisementContainer.findViewById<TextView>(R.id.textView19).text = advertisementWithChat.advertisement.location
            advertisementContainer.findViewById<LinearLayout>(R.id.cardColor).setBackgroundColor(Color.parseColor(color))

            val messageContainer = advertisementContainer.findViewById<LinearLayout>(R.id.messageContainer)
            messageContainer.removeAllViews()

            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener{
                val bundle = bundleOf("advertisementWithChat.advertisement" to advertisementWithChat.advertisement)
                view.findNavController().navigate(R.id.action_onlineAdsListFragment_to_onlineAdDetailsFragment, bundle)
            }
            advertisementWithChat.messageList.forEach {
                val msgLayout = LayoutInflater.from(context).inflate(R.layout.my_message_entry, advertisementContainer)
                msgLayout.findViewById<TextView>(R.id.textView74).text = it.recipientUID
                msgLayout.findViewById<TextView>(R.id.textView83).text = "${it.lastMessageSenderName}: ${it.lastMessage}"
                if(it.messageCounter>0) {
                    msgLayout.findViewById<TextView>(R.id.textView85).apply {
                        this.isVisible = true
                        this.text = it.messageCounter.toString()
                    }
                }
                else {
                    msgLayout.findViewById<TextView>(R.id.textView85).isGone = true
                }
                val userIcon = msgLayout.findViewById<ImageView>(R.id.imageView6)

                val profileImageRef = vm.storageRef.child(it.recipientUID)

                val options: RequestOptions = RequestOptions()
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)

                Glide.with(context)
                    .load(profileImageRef)
                    .apply(options)
                    .into(userIcon)

                msgLayout.setOnClickListener {  _ ->
                    var bundle : Bundle
                    if(type=="received_msg") {
                        bundle = bundleOf("client_uid" to it.recipientUID)
                    }
                    else {
                        bundle = bundleOf("advertiser_uid" to advertisementWithChat.advertisement.uid)
                    }
                    //TODO: continue here
                    view.findNavController()
                }

                messageContainer.addView(msgLayout)
            }
        }
        fun unbind() {
            advertisementContainer.findViewById<CardView>(R.id.cardView).setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.my_message_card,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val advertisementWithChat = displayData[position]

        holder.bind(advertisementWithChat, context, vm, type, view, colorList[(colorIndex++)%colorList.size])
    }

    override fun getItemCount(): Int = displayData.size

    fun updateAdvertisements(advertisements: List<AdvertisementWithChat>, sortBy: String){
        this.sortBy = sortBy
        colorIndex = 0
        data = advertisements.toList()
        val newData = performSort(sortBy, false)
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyMessages(displayData,newData))
        displayData = newData.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) : Int {
        if(data.isEmpty()){
            return 0
        }
        val newData: MutableList<AdvertisementWithChat>
        val allData = performSort(sortBy, false)
        if(text.isEmpty() || text.isBlank()){
            newData = allData.toMutableList()
        }
        else{
            newData = allData.filter {adWithMessage ->

                if(adWithMessage.advertisement.title.contains(text, ignoreCase = true) ||
                    adWithMessage.advertisement.location.contains(text, ignoreCase = true) ||
                    adWithMessage.advertisement.description.contains(text, ignoreCase = true)){
                    return@filter true
                }
                adWithMessage.messageList.forEach {
                    if(it.recipientName.contains(text, ignoreCase = true)){
                        return@filter true
                    }
                }
                return@filter false

            } as MutableList<AdvertisementWithChat>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyMessages(displayData, newData))
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
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyMessages(displayData, newData))
        displayData = newData.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }

    fun performSort(sortBy: String, showToast: Boolean = true) : List<AdvertisementWithChat>{
        var newData = data.toMutableList()
        when(sortBy){
            "title_asc" -> { newData.sortBy { it.advertisement.title }
                if(showToast) Toast.makeText(context, "Sorted by title A-Z", Toast.LENGTH_SHORT).show()
            }
            "title_desc" -> { newData.sortByDescending { it.advertisement.title }
                if(showToast) Toast.makeText(context, "Sorted by title Z-A", Toast.LENGTH_SHORT).show()
            }
            "creator_asc" -> { newData.sortBy { it.advertisement.user.fullname }
                if(showToast) Toast.makeText(context, "Sorted by creator A-Z", Toast.LENGTH_SHORT).show()
            }
            "creator_desc" -> { newData.sortByDescending { it.advertisement.user.fullname }
                if(showToast) Toast.makeText(context, "Sorted by creator Z-A", Toast.LENGTH_SHORT).show()
            }
            "location_asc" -> { newData.sortBy { it.advertisement.user.location }
                if(showToast) Toast.makeText(context, "Sorted by location A-Z", Toast.LENGTH_SHORT).show()
            }
            "location_desc" -> { newData.sortByDescending { it.advertisement.user.location }
                if(showToast) Toast.makeText(context, "Sorted by location Z-A", Toast.LENGTH_SHORT).show()
            }
            "date_desc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareByDescending<AdvertisementWithChat> { sdf_date.parse(it.advertisement.date) }.thenByDescending { sdf_time.parse(it.advertisement.from) }
                newData = newData.sortedWith(cmp).toMutableList()
                if(showToast) {
                    Toast.makeText(context, "Sorted by most recent", Toast.LENGTH_SHORT).show()
                }
            }
            "date_asc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareBy<AdvertisementWithChat> { sdf_date.parse(it.advertisement.date) }.thenBy { sdf_time.parse(it.advertisement.from) }
                newData = newData.sortedWith(cmp).toMutableList()
                if(showToast) {
                    Toast.makeText(context, "Sorted by oldest", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return newData
    }
}

class MyDiffCallbackMyMessages(val old: List<AdvertisementWithChat>, val new: List<AdvertisementWithChat>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
