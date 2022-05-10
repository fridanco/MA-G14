package it.polito.ma.g14.timebank.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.FirebaseFirestore

class GenericVM(application:Application) : AndroidViewModel(application) {


    private var l : ListenerRegistration
    private val db : FirebaseFirestore

    init {
        db = FirebaseFirestore.getInstance()
    }

}