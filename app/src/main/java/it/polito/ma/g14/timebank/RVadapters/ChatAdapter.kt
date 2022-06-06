package it.polito.ma.g14.timebank.RVadapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.ChatMessage
import it.polito.ma.g14.timebank.models.ChatVM
import it.polito.ma.g14.timebank.models.User
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(val view: View, val vm: ChatVM, val context: Context, var advertisement: Advertisement, val advertisementSkill: String): RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {
    var data = listOf<ChatMessage>()
    var client = User()
    var displayData = data.toMutableList()
    var lastAdvertiserMsgIndex = -1
    
    class ItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val chatMessageContainer = v.findViewById<LinearLayout>(R.id.chat_message_card)
        private val myUID = Firebase.auth.currentUser!!.uid

        fun bind(
            chatMessage: ChatMessage,
            client: User,
            context: Context,
            vm: ChatVM,
            data: List<ChatMessage>,
            advertisement: Advertisement,
            advertisementSkill: String,
            lastAdvertiserMsgIndex: Int
        ) {
            val userIcon = chatMessageContainer.findViewById<ImageView>(R.id.imageView6)

            val profileImageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference.child(chatMessage.senderUID)

            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(context)
                .load(profileImageRef)
                .apply(options)
                .into(userIcon)

            //Not me
            if(chatMessage.senderUID != myUID) {
                chatMessageContainer.gravity = Gravity.LEFT
                chatMessageContainer.findViewById<TextView>(R.id.msg_sender).text = chatMessage.senderName
            }
            //Me
            else{
                chatMessageContainer.gravity = Gravity.RIGHT
                chatMessageContainer.findViewById<TextView>(R.id.msg_sender).text = "You"
            }

            val advertiserUID = advertisement.uid
            //If I am the Client & the message is from the advertiser
            if(advertisement.status=="free" && advertiserUID!=myUID && chatMessage.senderUID==advertiserUID && data.indexOf(chatMessage)==lastAdvertiserMsgIndex){
                chatMessageContainer.findViewById<LinearLayout>(R.id.book_panel).isVisible = true
                if(client.credits>0) {
                    chatMessageContainer.findViewById<LinearLayout>(R.id.bookPanelContainer).isVisible = true
                    chatMessageContainer.findViewById<TextView>(R.id.textView103).isGone = true
                    chatMessageContainer.findViewById<Button>(R.id.button6).setOnClickListener {
                        vm.updateAdvertisementBooked(advertisement, advertisementSkill, context)
                    }
                }
                else{
                    chatMessageContainer.findViewById<LinearLayout>(R.id.bookPanelContainer).isGone = true
                    chatMessageContainer.findViewById<TextView>(R.id.textView103).isVisible = true
                }
            }
            else{
                chatMessageContainer.findViewById<LinearLayout>(R.id.book_panel).isGone = true
            }

            chatMessageContainer.findViewById<TextView>(R.id.chat_msg).text = chatMessage.message

            val dateTime = Date(chatMessage.timestamp)
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = dateTime
            val today: Calendar = Calendar.getInstance()
            val yesterday: Calendar = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter1 = SimpleDateFormat("HH:mm")
            val timeFormatter2 = SimpleDateFormat("EEE dd, HH:mm")
            val timeFormatter3 = SimpleDateFormat("dd MMM, HH:mm")
            val timeFormatter4 = SimpleDateFormat("dd MMM yyyy, HH:mm")

            val timeString: String

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

        holder.bind(chatMessage, client, context, vm, data, advertisement, advertisementSkill, lastAdvertiserMsgIndex)
    }

    override fun getItemCount(): Int = displayData.size

    fun updateChat(chatMessages: Pair<List<ChatMessage>, User>): Int {
        data = chatMessages.first.toList()
        client = chatMessages.second
        val previousLastAdvertiserMsgIndex = lastAdvertiserMsgIndex
        lastAdvertiserMsgIndex = chatMessages.first.indexOfLast { it.senderUID==advertisement.uid }
        val diffs = DiffUtil.calculateDiff(MyDiffCallbackChat(displayData,data))
        displayData = data.toMutableList()
        diffs.dispatchUpdatesTo(this)

        return if(previousLastAdvertiserMsgIndex!=-1 && previousLastAdvertiserMsgIndex!=lastAdvertiserMsgIndex){
            previousLastAdvertiserMsgIndex
        }
        else{
            -1
        }
    }

    fun addFilter(text: String) : Int {
        if(data.isEmpty()){
            return 0
        }
        val newData: MutableList<ChatMessage>
        newData = if(text.isEmpty() || text.isBlank()){
            data.toMutableList()
        } else{
            data.filter {chatMessage ->
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
