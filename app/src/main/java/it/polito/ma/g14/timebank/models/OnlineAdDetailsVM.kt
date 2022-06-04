package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
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

    fun rateAdvertiser(rating: Rating){
        db.collection("advertisements").document(rating.advertisement.id).update("clientRating",rating)
        db.collection ("users").document(rating.advertisement.uid).update("ratingsAsAdvertiser",
            FieldValue.arrayUnion(rating))
    }

    fun rateClient(rating: Rating){
        db.collection("advertisements").document(rating.advertisement.id).update("advertiserRating",rating)
        db.collection ("users").document(rating.advertisement.uid).update("ratingsAsClient",
            FieldValue.arrayUnion(rating))
    }


    fun updateAdvertisementStatus(advertisement: Advertisement,status: String){
        db.collection("advertisements").document(advertisement.id).update("status",status)
    }

    fun updateAdvertisementsBooked(advertisement: Advertisement, advertisementSkill: String, uid:String){

        db.runTransaction { transaction ->
            val clientRef = db.collection("users").document(Firebase.auth.currentUser!!.uid)
            val client = transaction.get(clientRef).toObject(User::class.java)

            client?.let {
                val advertisementRef = db.collection("advertisements").document(advertisement.id)
                val adSkills = advertisement.skills

                advertisement.apply {
                    this.bookedSkill = advertisementSkill
                    this.bookedByUID = Firebase.auth.currentUser!!.uid
                    this.bookedByName = client.fullname
                    this.status = "booked"
                }

                transaction.set(advertisementRef,advertisement)

                adSkills.forEach { adSkill ->
                    val skillRef = db.collection("skillAdvertisements").document(adSkill)
                    transaction.update(skillRef,"numAdvertisements",FieldValue.increment(-1))
                }
            }
        }
    }

}
