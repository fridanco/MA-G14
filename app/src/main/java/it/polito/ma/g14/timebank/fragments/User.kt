package it.polito.ma.g14.timebank.fragments

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
    //var advertisments : List<Advertisement> = emptyList()
}