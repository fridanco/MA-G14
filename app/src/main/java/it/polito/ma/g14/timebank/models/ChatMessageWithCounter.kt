package it.polito.ma.g14.timebank.models

import java.sql.Timestamp

data class ChatMessageWithCounter(
    val recipientUID: String,
    val recipientName: String,
    val lastMessage: String,
    val lastMessageSenderName: String,
    val timestamp: Long,
    val messageCounter: Int
)
