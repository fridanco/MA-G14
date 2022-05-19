package it.polito.ma.g14.timebank.models

import com.google.firebase.firestore.DocumentId

class Advertisement {
    @DocumentId
    var id : String = ""

    var uid : String = ""
    var title : String = ""
    var description : String = ""
    var date : String = ""
    var from : String = ""
    var to : String = ""
    var location : String = ""
    var skills : List<String> = emptyList()
    var user : AdvertisementUser = AdvertisementUser()
}
