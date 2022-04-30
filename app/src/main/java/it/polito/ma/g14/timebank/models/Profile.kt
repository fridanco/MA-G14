package it.polito.ma.g14.timebank.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
class Profile {
    @PrimaryKey(autoGenerate = false)
    var id : Int = 1
    var fullname : String = ""
    var nickname : String = ""
    var email : String = ""
    var location : String = ""
    var description : String = ""

    override fun toString() = "fullname:$fullname nickname:$nickname email:$email location:$location description:$description"
}