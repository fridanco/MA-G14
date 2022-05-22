package it.polito.ma.g14.timebank.models

import com.google.firebase.firestore.DocumentId

class User {
    @DocumentId
    var id : String = ""
    var fullname : String = ""
    var nickname : String = ""
    var email : String = ""
    var location : String = ""
    var description : String = ""
    var skills : List<String> = emptyList()
    var imageURL : String = ""
}