package it.polito.ma.g14.timebank.RVadapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
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
import java.util.*

class MyMessagesAdapter(val view: View, val vm: FirebaseVM, val context: Context, val type: String): RecyclerView.Adapter<MyMessagesAdapter.ItemViewHolder>() {
    var data = listOf<Pair<String, AdvertisementWithChat>>()
    var displayData = data.toMutableList()
    var colorList = mutableListOf("#FFFFFF")
    var colorIndex = 0
    var sort = 0

    private var sortBy = ""

    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val messageCard = v.findViewById<LinearLayout>(R.id.my_message_container)
        private val uid = Firebase.auth.currentUser!!.uid

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(advertisementWithChat: AdvertisementWithChat, context: Context, vm: FirebaseVM, type:String, view: View, color: String) {
            messageCard.findViewById<TextView>(R.id.textView4).text = advertisementWithChat.advertisement.title
            messageCard.findViewById<TextView>(R.id.textView5).text = advertisementWithChat.advertisement.description
            messageCard.findViewById<TextView>(R.id.textView6).text = advertisementWithChat.advertisement.date
            messageCard.findViewById<TextView>(R.id.textView7).text = "${advertisementWithChat.advertisement.from} - ${advertisementWithChat.advertisement.to}"
            messageCard.findViewById<LinearLayout>(R.id.cardColor).setBackgroundColor(Color.parseColor(color))

            val messageContainer = messageCard.findViewById<LinearLayout>(R.id.messageContainer)
            messageContainer.removeAllViews()

            this.messageCard.findViewById<CardView>(R.id.cardView).setOnClickListener{
                val bundle = bundleOf("advertisement" to advertisementWithChat.advertisement)
                view.findNavController().navigate(R.id.action_myMessages_to_onlineAdDetailsFragment, bundle)
            }
            advertisementWithChat.messageList.forEach {
                val msgLayout = LayoutInflater.from(context).inflate(R.layout.my_message_entry,messageContainer)
                msgLayout.findViewById<TextView>(R.id.textView74).text = it.recipientName
                val senderName = if(it.lastMessageSenderUID==uid){
                    "You"
                }
                else{
                    it.lastMessageSenderName
                }
                val tv_lastMessage = msgLayout.findViewById<TextView>(R.id.textView83)!!
                tv_lastMessage.text = "${senderName}: ${it.lastMessage}"

                if(it.messageCounter>0) {
                    tv_lastMessage.setTypeface(null, Typeface.BOLD)
                    msgLayout.findViewById<TextView>(R.id.textView85).apply {
                        this.isVisible = true
                        this.text = it.messageCounter.toString()
                    }
                }
                else {
                    tv_lastMessage.typeface = null
                    msgLayout.findViewById<TextView>(R.id.textView85).isGone = true
                }

                val dateTime = Date(it.timestamp)
                val calendar: Calendar = Calendar.getInstance()
                calendar.time = dateTime
                val today: Calendar = Calendar.getInstance()
                val timeFormatter1 = SimpleDateFormat("HH:mm")
                val timeFormatter2 = SimpleDateFormat("EEE")
                val timeFormatter3 = SimpleDateFormat("EEE dd")
                val timeFormatter4 = SimpleDateFormat("MMM")
                val timeFormatter5 = SimpleDateFormat("MMM yyyy")

                var timeString = ""

                if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    timeString = timeFormatter1.format(dateTime)
                }
                else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && calendar.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)) {
                    timeString = timeFormatter2.format(dateTime)
                }
                else if(calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH)==today.get(Calendar.MONTH)) {
                    timeString = timeFormatter3.format(dateTime)
                }
                else if(calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR)) {
                    timeString = timeFormatter4.format(dateTime)
                }
                else{
                    timeString = timeFormatter5.format(dateTime)
                }

                msgLayout.findViewById<TextView>(R.id.textView89).text = timeString

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
                    val bundle = if(type=="received_msg") {
                        //Client is the recipient (i am the advertiser)
                        bundleOf(
                            "advertisementID" to advertisementWithChat.advertisement.id,
                            "clientUID" to it.recipientUID,
                            "advertiserName" to advertisementWithChat.advertisement.user.fullname
                        )
                    }
                    else {
                        //Get advertiser from advertisement object (i am sending messages = i am the client)
                        bundleOf(
                            "advertisementID" to advertisementWithChat.advertisement.id,
                            "advertiserUID" to advertisementWithChat.advertisement.uid,
                            "advertiserName" to advertisementWithChat.advertisement.user.fullname
                        )
                    }
                    view.findNavController().navigate(R.id.action_myMessages_to_chatFragment, bundle)
                }
            }
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

        holder.bind(advertisementWithChat.second, context, vm, type, view, colorList[(colorIndex++)%colorList.size])
    }

    override fun getItemCount(): Int = displayData.size

    fun updateMessages(advertisementsWithChat: List<Pair<String, AdvertisementWithChat>>, sortBy: String, filterBy: String){
        this.sortBy = sortBy
        colorIndex = 0
        data = advertisementsWithChat.toList()
        val dataTmp = data
        data = performFilter(data, filterBy)
        val newData = performSort(sortBy, false)
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyMessages(displayData,newData))
        displayData = newData.toMutableList()
        data = dataTmp
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) : Int {
        if(data.isEmpty()){
            return 0
        }
        val newData: MutableList<Pair<String, AdvertisementWithChat>>
        val allData = performSort(sortBy, false)
        newData = performFilter(allData, text).toMutableList()
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackMyMessages(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
        return displayData.size
    }

    fun performFilter(data: List<Pair<String, AdvertisementWithChat>>, filterBy: String) : List<Pair<String, AdvertisementWithChat>>{
        return if(filterBy.isBlank()){
            data
        }
        else{
            data.filter {adWithMessage ->
                if(adWithMessage.second.advertisement.title.contains(filterBy, ignoreCase = true) ||
                    adWithMessage.second.advertisement.location.contains(filterBy, ignoreCase = true) ||
                    adWithMessage.second.advertisement.description.contains(filterBy, ignoreCase = true)){
                    return@filter true
                }
                adWithMessage.second.messageList.forEach {
                    if(it.recipientName.contains(filterBy, ignoreCase = true)){
                        return@filter true
                    }
                }
                return@filter false
            }
        }
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

    fun performSort(sortBy: String, showToast: Boolean = true) : List<Pair<String, AdvertisementWithChat>>{
        var newData = data.toMutableList()
        when(sortBy){
            "msg_desc" -> {
                val cmp = compareByDescending<Pair<String, AdvertisementWithChat>> {
                    it.second.containsUnreadMessage
                }.thenByDescending {
                    if(it.second.containsUnreadMessage) {
                        it.second.lastUnreadMessageTimestamp
                    }
                    else{
                        it.second.lastReadMessageTimestamp
                    }
                }
                newData = newData.sortedWith(cmp).toMutableList()
            }
            "title_asc" -> { newData.sortBy { it.second.advertisement.title }
                if(showToast) Toast.makeText(context, "Sorted by title A-Z", Toast.LENGTH_SHORT).show()
            }
            "title_desc" -> { newData.sortByDescending { it.second.advertisement.title }
                if(showToast) Toast.makeText(context, "Sorted by title Z-A", Toast.LENGTH_SHORT).show()
            }
            "creator_asc" -> { newData.sortBy { it.second.advertisement.user.fullname }
                if(showToast) Toast.makeText(context, "Sorted by creator A-Z", Toast.LENGTH_SHORT).show()
            }
            "creator_desc" -> { newData.sortByDescending { it.second.advertisement.user.fullname }
                if(showToast) Toast.makeText(context, "Sorted by creator Z-A", Toast.LENGTH_SHORT).show()
            }
            "date_desc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareByDescending<Pair<String, AdvertisementWithChat>> { sdf_date.parse(it.second.advertisement.date) }.thenByDescending { sdf_time.parse(it.second.advertisement.from) }
                newData = newData.sortedWith(cmp).toMutableList()
                if(showToast) {
                    Toast.makeText(context, "Sorted by most recent", Toast.LENGTH_SHORT).show()
                }
            }
            "date_asc" -> {
                val sdf_date = SimpleDateFormat("EEE, d MMM yyyy")
                val sdf_time = SimpleDateFormat("HH:mm")
                val cmp = compareBy<Pair<String, AdvertisementWithChat>> { sdf_date.parse(it.second.advertisement.date) }.thenBy { sdf_time.parse(it.second.advertisement.from) }
                newData = newData.sortedWith(cmp).toMutableList()
                if(showToast) {
                    Toast.makeText(context, "Sorted by oldest", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return newData
    }
}

class MyDiffCallbackMyMessages(val old: List<Pair<String, AdvertisementWithChat>>, val new: List<Pair<String, AdvertisementWithChat>>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
