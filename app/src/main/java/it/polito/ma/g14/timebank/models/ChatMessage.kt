package it.polito.ma.g14.timebank.models

import java.sql.Timestamp

data class ChatMessage(
    val message: String,
    val timestamp: String,
    val sender: String
)
