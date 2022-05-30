package it.polito.ma.g14.timebank.models

import java.sql.Timestamp

data class Chat(
    val advertisementID: String,
    val clientUID: String,
    val clientNotifications: Int,
    val advertiserUID: String,
    val advertiserNotifications: Int,
    val chatMessages: List<ChatMessage>
)
