package it.polito.ma.g14.timebank.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.ChatAdapter
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.ChatVM
import it.polito.ma.g14.timebank.utils.Utils

class ChatFragment : Fragment() {

    val chatsVM by viewModels<ChatVM>()

    lateinit var et_message : EditText
    lateinit var btn_send_message : Button

    var message = ""

    lateinit var advertisement: Advertisement
    lateinit var advertisementSkill: String
    var client_uid = ""

    lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        advertisement = requireArguments().getSerializable("advertisement") as Advertisement
        advertisementSkill = requireArguments().getString("advertisementSkill").toString()
        client_uid = requireArguments().getString("clientUID") ?: Firebase.auth.currentUser!!.uid

        requireActivity().invalidateOptionsMenu()

        val chatID = "${client_uid}_${advertisement.id}"

        chatsVM.getChatMessages(chatID, client_uid, advertisement.uid, advertisement.id, advertisementSkill)

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
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
        adapter = ChatAdapter(view, chatsVM, requireContext(), advertisement, advertisementSkill)
        rv.adapter = adapter

        chatsVM.chat.observe(viewLifecycleOwner) { chatList ->
            if(chatList.isEmpty()){
                emptyRv.isVisible = true
                adapter.updateChat(listOf())
            }
            else {
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

        chatsVM.adBooked.observe(viewLifecycleOwner){
            if(!it){
                return@observe
            }
            Toast.makeText(requireContext(), "Advertisement booked", Toast.LENGTH_SHORT).show()
            adapter.advertisement.status = "booked"
            adapter.notifyItemChanged(adapter.lastAdvertiserMsgIndex)
        }

        et_message.doOnTextChanged { text, _, _, _ ->
            message = text.toString()
        }

        btn_send_message.setOnClickListener {
            chatsVM.sendMessage(message, client_uid, advertisement.id)
            message = ""
            et_message.text = message.toEditable()
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    override fun onDestroy() {
        chatsVM.resetAdBooked()
        super.onDestroy()
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}