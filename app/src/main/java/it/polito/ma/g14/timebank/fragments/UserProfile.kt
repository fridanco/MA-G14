package it.polito.ma.g14.timebank.fragments

class UserProfile {
    var id : Int = 1
    var fullname : String = ""
    var nickname : String = ""
    var email : String = ""
    var location : String = ""
    var description : String = ""
    var skills : List<String> = emptyList()
    var advertisments : List<Advertisement> = emptyList()
}