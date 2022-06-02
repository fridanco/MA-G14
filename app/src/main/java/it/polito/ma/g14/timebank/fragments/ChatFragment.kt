package it.polito.ma.g14.timebank.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
import it.polito.ma.g14.timebank.utils.Utils

class ChatFragment : Fragment() {

    val chatsVM by viewModels<ChatVM>()
    val firebaseVM by viewModels<FirebaseVM>()

    lateinit var et_message : EditText
    lateinit var btn_send_message : Button

    var message = ""

    var client_uid = ""
    var advertiser_uid = ""
    var advertisementID = ""
    var advertiser_name = ""

    lateinit var adapter: ChatAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        advertisementID = requireArguments().getString("advertisementID").toString()
        client_uid = requireArguments().getString("clientUID") ?: Firebase.auth.currentUser!!.uid
        advertiser_uid = requireArguments().getString("advertiserUID") ?: Firebase.auth.currentUser!!.uid
        advertiser_name = requireArguments().getString("advertiserName").toString()

        requireActivity().invalidateOptionsMenu()

        val chatID = "${client_uid}_${advertisementID}"

        chatsVM.getChatMessages(chatID, client_uid, advertiser_uid, advertisementID)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                //rv.isGone = true
                emptyRv.isVisible = true
                adapter.updateChat(listOf())
            }
            else {
                //rv.isVisible = true
                emptyRv.isGone = true
                val previousLastAdvertiserMsgIndex = adapter.updateChat(chatList)
                if(previousLastAdvertiserMsgIndex!=-1){
                    val item = rv.findViewHolderForAdapterPosition(previousLastAdvertiserMsgIndex)
                    item?.let {
                        val bookPanelView = it.itemView.findViewById<LinearLayout>(R.id.book_panel)
                        if(bookPanelView!=null){
                            bookPanelView.isGone = true
                        }
                    }
                }
                rv.scrollToPosition(adapter.displayData.size-1)
            }
        }

        et_message.doOnTextChanged { text, _, _, _ ->
            message = text.toString()
        }

        btn_send_message.setOnClickListener {
            chatsVM.sendMessage(message, client_uid, advertisementID)
            message = ""
            et_message.text = message.toEditable()
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}