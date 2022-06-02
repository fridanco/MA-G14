package it.polito.ma.g14.timebank.models

class Chat{
    var advertisementID = ""
    var clientUID = ""
    var clientName= ""
    var clientNotifications = 0
    var advertiserUID= ""
    var advertiserName= ""
    var advertiserNotifications = 0
    var chatMessages = listOf<ChatMessage>()
}
