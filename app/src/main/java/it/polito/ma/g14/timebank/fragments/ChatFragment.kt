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
import it.polito.ma.g14.timebank.models.Chat
import it.polito.ma.g14.timebank.models.ChatMessage
import it.polito.ma.g14.timebank.models.ChatVM

class ChatFragment : Fragment() {

    val chatsVM by viewModels<ChatVM>()

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    lateinit var profileListener : ListenerRegistration

    lateinit var uid: String
    lateinit var advertisementID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = Firebase.auth.currentUser!!.uid
        advertisementID = requireArguments().getString("advertisementID").toString()

    }

    fun getChatMessages(){
        val chatID = "${uid}_${advertisementID}"

        profileListener = db.collection("chats").document(chatID)
            .addSnapshotListener{ result, exception ->
                if(exception!=null){
                    Log.e("Timebank","Could not retrieve chat messages")
                }
                val chat = result!!.toObject(Chat::class.java)
                chat?.chatMessages?.let { chatsVM.addChatMessages(it) }
            }

    }

}