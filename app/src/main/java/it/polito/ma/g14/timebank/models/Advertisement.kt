package it.polito.ma.g14.timebank.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

class Advertisement : Serializable {
    @DocumentId
    var id : String = ""
    var uid : String = ""
    var title : String = ""
    var bookedSkill: String = ""
    var bookedByUID : String = ""
    var bookedByName : String = ""
    var bookedTimestamp: Long = -1
    var completedTimestamp: Long = -1
    var description : String = ""
    var date : String = ""
    var from : String = ""
    var to : String = ""
    var location : String = ""
    var status : String = "free"
    var skills : List<String> = emptyList()
    var user : AdvertisementUser = AdvertisementUser()
    var advertiserRating: Rating? = null
    var clientRating: Rating? = null
}
