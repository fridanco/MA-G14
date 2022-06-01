package it.polito.ma.g14.timebank.RVadapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.ChatMessage
import it.polito.ma.g14.timebank.models.FirebaseVM
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(val view: View, val vm: FirebaseVM, val context: Context, val advertiserUID: String): RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {
    var filter: Boolean = false
    var data = listOf<ChatMessage>()
    var displayData = data.toMutableList()
    var lastAdvertiserMsgIndex = -1
    
    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val chatMessageContainer = v.findViewById<LinearLayout>(R.id.chat_message_card)

        fun bind(
            chatMessage: ChatMessage,
            context: Context,
            vm: FirebaseVM,
            data: List<ChatMessage>,
            advertiserUID: String,
            lastAdvertiserMsgIndex: Int
        ) {
            val userIcon = chatMessageContainer.findViewById<ImageView>(R.id.imageView6)

            val profileImageRef = vm.storageRef.child(chatMessage.senderUID)

            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(context)
                .load(profileImageRef)
                .apply(options)
                .into(userIcon)

            //Not me
            if(chatMessage.senderUID != Firebase.auth.currentUser!!.uid) {
                chatMessageContainer.findViewById<LinearLayout>(R.id.spacerLeft).isGone = true
                chatMessageContainer.findViewById<LinearLayout>(R.id.spacerRight).isVisible = true
                chatMessageContainer.findViewById<TextView>(R.id.msg_sender).text = "By ${chatMessage.senderName}"
                //TODO:chatMessageContainer.findViewById<CardView>(R.id.cardView).setBackgroundColor(Color.parseColor("#00000A"))
            }
            //Me
            else{
                chatMessageContainer.findViewById<LinearLayout>(R.id.spacerLeft).isVisible = true
                chatMessageContainer.findViewById<LinearLayout>(R.id.spacerRight).isGone = true
                chatMessageContainer.findViewById<TextView>(R.id.msg_sender).text = "You"
                //TODO:chatMessageContainer.findViewById<CardView>(R.id.cardView).setBackgroundColor(Color.parseColor("#0A0000"))
            }

            if(chatMessage.senderUID==advertiserUID && data.indexOf(chatMessage)==lastAdvertiserMsgIndex){
                chatMessageContainer.findViewById<LinearLayout>(R.id.book_panel).isVisible = true
            }
            else{
                chatMessageContainer.findViewById<LinearLayout>(R.id.book_panel).isGone = true
            }

            chatMessageContainer.findViewById<TextView>(R.id.chat_msg).text = chatMessage.message

            val dateTime = Date(chatMessage.timestamp)
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTime(dateTime)
            val today: Calendar = Calendar.getInstance()
            val yesterday: Calendar = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter1 = SimpleDateFormat("HH:mm")
            val timeFormatter2 = SimpleDateFormat("EEE dd, HH:mm")
            val timeFormatter3 = SimpleDateFormat("dd MMM, HH:mm")
            val timeFormatter4 = SimpleDateFormat("dd MMM yyyy, HH:mm")

            var timeString = ""

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                timeString = "Today " + timeFormatter1.format(dateTime)
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                timeString = "Yesterday " + timeFormatter1.format(dateTime)
            }
            else if(calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH)==today.get(Calendar.MONTH)) {
                timeString = timeFormatter2.format(dateTime)
            }
            else if(calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR)) {
                timeString = timeFormatter3.format(dateTime)
            }
            else{
                timeString = timeFormatter4.format(dateTime)
            }

            chatMessageContainer.findViewById<TextView>(R.id.msg_time).text = timeString


        }
        fun unbind() {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.chat_message_card,parent, false)
        return ItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val chatMessage = displayData[position]

        holder.bind(chatMessage, context, vm, data, advertiserUID, lastAdvertiserMsgIndex)
    }

    override fun getItemCount(): Int = displayData.size

    fun updateChat(chatMessages: List<ChatMessage>){
        data = chatMessages.toList()
        lastAdvertiserMsgIndex = chatMessages.indexOfLast { it.senderUID==advertiserUID }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackChat(displayData,data))
        displayData = data.toMutableList()
        diffs.dispatchUpdatesTo(this)
    }

    fun addFilter(text: String) : Int {
        if(data.isEmpty()){
            return 0
        }
        val newData: MutableList<ChatMessage>
        if(text.isEmpty() || text.isBlank()){
            newData = data.toMutableList()
        }
        else{
            newData = data.filter {chatMessage ->
                if(chatMessage.message.contains(text, ignoreCase = true)){
                    return@filter true
                }
                return@filter false
            } as MutableList<ChatMessage>
        }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackChat(displayData, newData))
        displayData = newData
        diffs.dispatchUpdatesTo(this)
        return displayData.size
    }

}

class MyDiffCallbackChat(val old: List<ChatMessage>, val new: List<ChatMessage>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
