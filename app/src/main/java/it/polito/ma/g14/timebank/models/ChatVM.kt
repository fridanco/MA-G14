package it.polito.ma.g14.timebank.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatVM : ViewModel() {

    private val _chat = MutableLiveData<List<ChatMessage>>(listOf())
    val chat : LiveData<List<ChatMessage>> = _chat


    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    lateinit var chatMessagesListener : ListenerRegistration

    fun setChat(chat: List<ChatMessage>){
        _chat.value = chat
    }

    fun getChatMessages(chatID: String, client_uid: String, advertiser_uid: String, advertisementID: String){
        val uid = Firebase.auth.currentUser!!.uid
        chatMessagesListener = db.collection("chats").document(chatID)
            .addSnapshotListener{ result, exception ->
                if(exception!=null){
                    Log.e("Timebank","Could not retrieve chat messages & created chat document")
                    return@addSnapshotListener
                }


                if(result==null || !result.exists()){
                    db.runTransaction { transaction ->
                        val clientProfileRef = db.collection("users").document(client_uid)
                        val advertiserProfileRef = db.collection("users").document(advertiser_uid)
                        val clientProfile = transaction.get(clientProfileRef).toObject(User::class.java)
                        val advertiserProfile = transaction.get(advertiserProfileRef).toObject(User::class.java)

                        val newChat = Chat().apply {
                            this.advertisementID = advertisementID
                            this.clientUID = client_uid
                            this.clientName = clientProfile!!.fullname
                            this.clientNotifications = 0
                            this.advertiserUID = advertiser_uid
                            this.advertiserName = advertiserProfile!!.fullname
                            this.advertiserNotifications = 0
                            this.chatMessages = listOf()
                        }
                        val chatRef = db.collection("chats").document(chatID)
                        transaction.set(chatRef, newChat)
                    }
                        .addOnFailureListener {
                            println(it.message)
                        }
                        .addOnSuccessListener {
                            println("SUCCESS")
                        }
                    _chat.value = listOf()
                    return@addSnapshotListener
                }

                val chat = result.toObject(Chat::class.java)!!

                //I am the client - my notifications should be zeroed
                if(chat.clientUID==uid){
                    if(chat.clientNotifications > 0){
                        db.collection("chats").document(chatID).update("clientNotifications", 0)
                    }
                }
                //I am the advertised - my notifications should be zeroed
                else{
                    if(chat.advertiserNotifications > 0){
                        db.collection("chats").document(chatID).update("advertiserNotifications", 0)
                    }
                }

                addChatMessages(chat.chatMessages)

            }
    }

    fun addChatMessages(newChatMessages: List<ChatMessage>){
        val currChats = _chat.value!!.toMutableList()
        newChatMessages.forEach {
            if(currChats.find{ currChat -> currChat.timestamp==it.timestamp && currChat.senderUID==it.senderUID}==null){
                currChats.add(it)
            }
        }

        _chat.value = currChats
    }

    fun sendMessage(msg: String, client_uid: String, advertisementID: String){
        if(msg.isBlank()){
            return
        }

        val chatID = "${client_uid}_${advertisementID}"
        val senderUID = if(client_uid==Firebase.auth.currentUser!!.uid) client_uid else Firebase.auth.currentUser!!.uid

        db.runTransaction { transaction ->
            val profileRef = db.collection("users").document(senderUID)
            val profile = transaction.get(profileRef).toObject(User::class.java)

            val chatMessage = ChatMessage().apply{
                this.message = msg
                this.timestamp = System.currentTimeMillis()
                this.senderUID = senderUID
                this.senderName = profile!!.fullname
            }

            val chatRef = db.collection("chats").document(chatID)


            //I am the client requesting the advertised service - advertiser is notified
            if(senderUID==client_uid) {
                transaction.update(
                    chatRef,
                    "chatMessages", FieldValue.arrayUnion(chatMessage),
                    "advertiserNotifications", FieldValue.increment(1)
                )
            }
            //I am the advertiser - client is notified
            else{
                transaction.update(
                    chatRef,
                    "chatMessages", FieldValue.arrayUnion(chatMessage),
                    "clientNotifications", FieldValue.increment(1)
                )
            }
        }
    }



    override fun onCleared() {
        chatMessagesListener.remove()
        super.onCleared()
    }
}