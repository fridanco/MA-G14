package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel


class Rating {
    var advertisement = Advertisement()
    var rating : Float = 0f
    var textRating : String = ""
    var raterUid : String = ""
    var raterName : String = ""
}