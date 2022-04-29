package it.polito.ma.g14.timebank.models

class Profile {
    var fullname : String = ""
    var nickname : String = ""
    var email : String = ""
    var location : String = ""
    var description : String = ""
    var skills = mutableListOf<Skill>()
}