package it.polito.ma.g14.timebank.models

import android.content.Context
import android.util.Log
import android.widget.Toast
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

    private val _chatWithClient = MutableLiveData<Pair<List<ChatMessage>, User>>(Pair(listOf(), User()))
    val chatWithClient : LiveData<Pair<List<ChatMessage>, User>> = _chatWithClient

    private val _adBooked = MutableLiveData<Boolean>()
    val adBooked : LiveData<Boolean> = _adBooked

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    lateinit var chatMessagesListener : ListenerRegistration

    fun getChatMessages(chatID: String, client_uid: String, advertiser_uid: String, advertisementID: String, interestedSkill: String){
        val uid = Firebase.auth.currentUser!!.uid
        chatMessagesListener = db.collection("chats").document(chatID)
            .addSnapshotListener{ result, exception ->
                if(exception!=null){
                    Log.e("Timebank","Could not retrieve chat messages & created chat document")
                    return@addSnapshotListener
                }

                db.collection("users").document(client_uid).get().addOnSuccessListener { userResult ->
                    val user = userResult!!.toObject(User::class.java)!!

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
                                this.interestedSkill = interestedSkill
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
                        _chatWithClient.value = Pair(listOf(), user)
                        return@addOnSuccessListener
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
    }

    fun addChatMessages(newChatMessages: List<ChatMessage>){
        val currChats = _chatWithClient.value!!.first.toMutableList()
        newChatMessages.forEach {
            if(currChats.find{ currChat -> currChat.timestamp==it.timestamp && currChat.senderUID==it.senderUID}==null){
                currChats.add(it)
            }
        }

        val user = _chatWithClient.value!!.second
        _chatWithClient.value = Pair(currChats, user)
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

    fun updateAdvertisementBooked(advertisement: Advertisement, advertisementSkill: String, context: Context?){
        db.runTransaction { transaction ->
            val clientRef = db.collection("users").document(Firebase.auth.currentUser!!.uid)
            val client = transaction.get(clientRef).toObject(User::class.java)

            client?.let {
                if(it.credits==0){
                    Toast.makeText(context, "Could not book advertisement", Toast.LENGTH_SHORT).show()
                    return@runTransaction
                }

                val advertisementRef = db.collection("advertisements").document(advertisement.id)
                val adSkills = advertisement.skills

                val ad = transaction.get(advertisementRef).toObject(Advertisement::class.java)
                if(ad!!.status!="free"){
                    return@runTransaction
                }

                advertisement.apply {
                    this.bookedSkill = advertisementSkill
                    this.bookedByUID = Firebase.auth.currentUser!!.uid
                    this.bookedByName = client.fullname
                    this.bookedTimestamp = System.currentTimeMillis()
                    this.status = "booked"
                }

                transaction.set(advertisementRef,advertisement)

                client.credits--
                transaction.set(clientRef, client)

                val advertiserRef = db.collection("users").document(advertisement.uid)
                val advertiser = transaction.get(advertiserRef).toObject(User::class.java)!!

                advertiser.credits++
                transaction.set(advertiserRef, advertiser)

                adSkills.forEach { adSkill ->
                    val skillRef = db.collection("skillAdvertisements").document(adSkill)
                    transaction.update(skillRef,"numAdvertisements",FieldValue.increment(-1))
                }

                _adBooked.postValue(true)
            }
        }
    }

    fun resetAdBooked(){
        _adBooked.value = false
    }

    override fun onCleared() {
        chatMessagesListener.remove()
        super.onCleared()
    }
}