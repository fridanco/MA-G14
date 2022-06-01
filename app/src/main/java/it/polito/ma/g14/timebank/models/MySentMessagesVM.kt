package it.polito.ma.g14.timebank.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MySentMessagesVM : ViewModel() {

    val _sentMessages = MutableLiveData<Map<String, AdvertisementWithChat>>()
    val sentMessages : LiveData<Map<String, AdvertisementWithChat>> = _sentMessages

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    lateinit var sentMessagesListener : ListenerRegistration

    fun getReceivedMessages(clientUID: String){
        sentMessagesListener = db.collection("chats")
            .whereEqualTo("clientUID",clientUID)
            .whereNotEqualTo("chatMessages", listOf<ChatMessage>())
            .addSnapshotListener{ value, exception ->
                if (exception != null) {
                    Log.e("Timebank", "Received messages could not be retrieved", exception)
                    return@addSnapshotListener
                }

                val messagesMap = mutableMapOf<String, AdvertisementWithChat>()
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
                            messagesMap[it.key] = advertisementWithChat
                        }
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

                        _sentMessages.value = messagesMap.toList().sortedWith(cmp).toMap()
                    }
                }
            }
    }

    private val _sortBy = MutableLiveData<String>("date_desc")
    val sortBy : LiveData<String> = _sortBy

    fun setSortBy(sort: String){
        _sortBy.value = sort
    }
    fun getSortBy() : String{
        return _sortBy.value ?: "date_desc"
    }

}