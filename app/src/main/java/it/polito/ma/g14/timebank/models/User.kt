package it.polito.ma.g14.timebank.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

class User : Serializable {
    @DocumentId
    var id : String = ""
    var fullname : String = ""
    var nickname : String = ""
    var email : String = ""
    var location : String = ""
    var description : String = ""
    var skills : List<String> = emptyList()
    var ratingsAsClient : List<Rating> = emptyList()
    var ratingsAsAdvertiser : List<Rating> = emptyList()

}