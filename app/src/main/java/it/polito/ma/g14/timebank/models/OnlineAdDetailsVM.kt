package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class OnlineAdDetailsVM(application:Application) : AndroidViewModel(application) {

    private val _advertisement = MutableLiveData<Advertisement>()
    val advertisement : LiveData<Advertisement> = _advertisement

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    fun getAdvertisement(advertisementID: String){
        db.collection("advertisements").document(advertisementID)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    println("ADVERTISEMENT LISTENER ERROR: ${error.message}")
                    return@addSnapshotListener
                }

                val advertisement = value!!.toObject(Advertisement::class.java)
                advertisement?.let {
                    _advertisement.postValue(it)
                }
            }
    }

}
