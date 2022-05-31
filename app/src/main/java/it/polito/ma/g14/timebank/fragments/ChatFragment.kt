package it.polito.ma.g14.timebank.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.ChatAdapter
import it.polito.ma.g14.timebank.models.*

class ChatFragment : Fragment() {

    val chatsVM by viewModels<ChatVM>()
    val firebaseVM by viewModels<FirebaseVM>()

    lateinit var et_message : EditText
    lateinit var btn_send_message : Button

    var message = ""

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    lateinit var chatMessagesListener : ListenerRegistration

    lateinit var client_uid: String
    lateinit var advertiser_uid: String
    lateinit var advertisementID: String

    lateinit var adapter: ChatAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getChatMessages()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        advertisementID = requireArguments().getString("advertisementID").toString()
        client_uid = requireArguments().getString("clientUID") ?: Firebase.auth.currentUser!!.uid
        advertiser_uid = requireArguments().getString("advertiserUID") ?: Firebase.auth.currentUser!!.uid

        et_message = view.findViewById(R.id.chat_message)
        btn_send_message = view.findViewById(R.id.button5)

        val rv = view.findViewById<RecyclerView>(R.id.chatRV)
        val emptyRv = view.findViewById<TextView>(R.id.emptyChatRV)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        rv.layoutManager = linearLayoutManager
        rv.layoutManager
        adapter = ChatAdapter(view, firebaseVM, requireContext(), advertiser_uid)
        rv.adapter = adapter

        chatsVM.chat.observe(viewLifecycleOwner) { chatList ->
            if(chatList.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
            }
            else {
                rv.isVisible = true
                emptyRv.isGone = true
                adapter.updateChat(chatList)
            }
        }

        et_message.doOnTextChanged { text, _, _, _ ->
            message = text.toString()
        }

        btn_send_message.setOnClickListener {
            sendMessage(message)
            message = ""
            et_message.text = message.toEditable()
        }

    }

    fun sendMessage(msg: String){
        if(msg.isBlank()){
            return
        }

        val chatID = "${client_uid}_${advertisementID}"
        val senderUID = if(client_uid==Firebase.auth.currentUser!!.uid) client_uid else Firebase.auth.currentUser!!.uid
        val chatMessage = ChatMessage(msg, System.currentTimeMillis(), senderUID, firebaseVM.getProfileValue().fullname)


        //I am the advertiser - client is notified
        if(senderUID==client_uid) {
            db.collection("chats").document(chatID).update(
                "chatMessages", FieldValue.arrayUnion(chatMessage),
                "advertiserNotifications", FieldValue.increment(1)
            )
        }
        //I am the client requesting the advertised service - advertiser is notified
        else{
            db.collection("chats").document(chatID).update(
                "chatMessages", FieldValue.arrayUnion(chatMessage),
                "clientNotifications", FieldValue.increment(1)
            )
        }
    }

    fun getChatMessages(){
        val chatID = "${client_uid}_${advertisementID}"
        val uid = Firebase.auth.currentUser!!.uid
        chatMessagesListener = db.collection("chats").document(chatID)
            .addSnapshotListener{ result, exception ->
                if(exception!=null){
                    db.runTransaction { transaction ->
                        val clientProfileRef = db.collection("users").document(client_uid)
                        val advertiserProfileRef = db.collection("users").document(client_uid)
                        val clientProfile = transaction.get(clientProfileRef).toObject(User::class.java)
                        val advertiserProfile = transaction.get(advertiserProfileRef).toObject(User::class.java)

                        val chat = Chat(
                            advertisementID,
                            client_uid,
                            clientProfile!!.fullname,
                            0,
                            advertiser_uid,
                            advertiserProfile!!.fullname,
                            0,
                            listOf()
                        )
                        val chatRef = db.collection("chats").document(chatID)
                        transaction.set(chatRef, chat)
                    }
                    Log.e("Timebank","Could not retrieve chat messages & created chat document")
                    return@addSnapshotListener
                }
                val chat = result!!.toObject(Chat::class.java)
                chat?.let {
                    //I am the client - my notifications should be zeroed
                    if(it.clientUID==uid){
                        if(it.clientNotifications > 0){
                            db.collection("chats").document(chatID).update("clientNotifications", 0)
                        }
                    }
                    //I am the advertised - my notifications should be zeroed
                    else{
                        if(it.advertiserNotifications > 0){
                            db.collection("chats").document(chatID).update("advertiserNotifications", 0)
                        }
                    }
                    it.chatMessages.let { chatMessagesList ->
                        chatsVM.addChatMessages(chatMessagesList)
                    }
                }
            }
    }


    override fun onDetach() {
        chatMessagesListener.remove()
        super.onDetach()
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}