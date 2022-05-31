package it.polito.ma.g14.timebank.models

data class AdvertisementWithChat (
    val advertisement: Advertisement,
    val messageList: List<ChatMessageWithCounter>
){
    var containsUnreadMessage: Boolean = false
    var lastUnreadMessageTimestamp: Long = 0
    var lastReadMessageTimestamp: Long = 0
}