package it.polito.ma.g14.timebank.models

data class ChatMessageWithCounter(
    val recipientUID: String,
    val recipientName: String,
    val lastMessage: String,
    val lastMessageSenderName: String,
    val lastMessageSenderUID: String,
    val timestamp: Long,
    val interestedSkill: String,
    val messageCounter: Int
)
