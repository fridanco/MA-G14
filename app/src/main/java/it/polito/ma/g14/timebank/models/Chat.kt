package it.polito.ma.g14.timebank.models

import java.sql.Timestamp

data class Chat(
    val advertisementID: String,
    val clientUID: String,
    val advertiserUID: String,
    val chatMessages: List<ChatMessage>
)
