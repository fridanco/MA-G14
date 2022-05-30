package it.polito.ma.g14.timebank.models

import java.sql.Timestamp

data class ChatMessage(
    val message: String,
    val timestamp: Long,
    val senderUID: String,
    val senderName: String
)
