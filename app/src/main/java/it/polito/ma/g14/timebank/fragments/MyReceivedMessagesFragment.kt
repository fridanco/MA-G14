package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.*

class MyReceivedMessagesFragment : Fragment() {

    val receivedMessagesVM by viewModels<MyReceivedMessagesVM>()

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    lateinit var receivedMessagesListener : ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_received_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun getReceivedMessages(){
        val advertiserUID = Firebase.auth.currentUser!!.uid

        receivedMessagesListener = db.collection("chats").whereEqualTo("advertiserUID",advertiserUID)
            .addSnapshotListener{ value, exception ->
                if (exception != null) {
                    Log.e("Timebank", "Received messages could not be retrieved", exception)
                    return@addSnapshotListener
                }

                val messagesMap = mutableMapOf<String, MutableList<AdvertisementWithChat>>()
                val messageWithCounterMap = mutableMapOf<String, MutableList<ChatMessageWithCounter>>()
                value?.let { querySnapshot ->
                    val chatList = querySnapshot.mapNotNull { it.toObject(Chat::class.java) }
                    chatList.forEach{ chat ->
                        val chatMessageWithCounter = ChatMessageWithCounter(
                            chat.clientUID,
                            chat.clientName,
                            chat.chatMessages.last().message,
                            chat.chatMessages.last().senderName,
                            chat.chatMessages.last().timestamp,
                            chat.advertiserNotifications
                        )
                        if(!messageWithCounterMap.containsKey(chat.advertisementID)){
                            messageWithCounterMap[chat.advertisementID] = mutableListOf()
                        }
                        messageWithCounterMap[chat.advertisementID]!!.add(chatMessageWithCounter)
                    }

                    messageWithCounterMap.forEach {
                        val cmp = compareByDescending<ChatMessageWithCounter> { it.messageCounter>0 }.thenByDescending { it.timestamp }
                        messageWithCounterMap[it.key] = it.value.sortedWith(cmp).toMutableList()
                    }

                    db.runTransaction { transaction ->
                        messageWithCounterMap.forEach {
                            val advertisementRef = db.collection("advertisements").document(it.key)
                            val advertisement = transaction.get(advertisementRef).toObject(Advertisement::class.java)

                            val advertisementWithChat = AdvertisementWithChat(
                                advertisement!!,
                                it.value
                            )
                            advertisementWithChat.apply {
                                this.containsUnreadMessage = it.value.find{ it.messageCounter>0}!=null
                                this.lastUnreadMessageTimestamp = if(containsUnreadMessage){
                                    it.value.first().timestamp
                                }
                                else{
                                    -1
                                }

                                this.lastReadMessageTimestamp = if(!containsUnreadMessage){
                                    it.value.first().timestamp
                                }
                                else{
                                    it.value.find { it.messageCounter==0 }?.timestamp ?: -1
                                }

                            }
                            if(!messagesMap.containsKey(it.key)){
                                messagesMap[it.key] = mutableListOf()
                            }
                            messagesMap[it.key]!!.add(advertisementWithChat)
                        }
                    }
                }
            }
    }


}