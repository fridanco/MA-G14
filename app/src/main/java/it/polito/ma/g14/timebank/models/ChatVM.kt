package it.polito.ma.g14.timebank.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatVM : ViewModel() {

    private val _chat = MutableLiveData<List<ChatMessage>>(listOf())
    val chat : LiveData<List<ChatMessage>> = _chat

    fun setChat(chat: List<ChatMessage>){
        _chat.value = chat
    }

    fun addChatMessages(newChatMessages: List<ChatMessage>){
        val currChats = _chat.value!!.toMutableList()
        newChatMessages.forEach {
            if(!currChats.contains(it)){
                currChats.add(it)
            }
        }

        _chat.value = currChats
    }
}