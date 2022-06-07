package it.polito.ma.g14.timebank.models

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class OnlineAdDetailsVM(application:Application) : AndroidViewModel(application) {

    private val _advertisement = MutableLiveData<Pair<Advertisement, User>>()
    val advertisement : LiveData<Pair<Advertisement, User>> = _advertisement

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    fun getAdvertisement(advertisementID: String, uid: String){
        db.collection("advertisements").document(advertisementID)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    println("ADVERTISEMENT LISTENER ERROR: ${error.message}")
                    return@addSnapshotListener
                }

                val advertisement = value!!.toObject(Advertisement::class.java)
                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { result ->
                        val user = result.toObject(User::class.java)

                        advertisement?.let {
                            user?.let {
                                _advertisement.postValue(Pair(advertisement, user))
                            }
                        }

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
        db.collection ("users").document(rating.advertisement.bookedByUID).update("ratingsAsClient",
            FieldValue.arrayUnion(rating))
    }

    fun updateAdvertisementCompleted(advertisement: Advertisement){
        db.collection("advertisements").document(advertisement.id).update("status","complete","completedTimestamp", System.currentTimeMillis())
    }

    fun updateAdvertisementBooked(advertisement: Advertisement, advertisementSkill: String, context: Context?){
        db.runTransaction { transaction ->
            val clientRef = db.collection("users").document(Firebase.auth.currentUser!!.uid)
            val client = transaction.get(clientRef).toObject(User::class.java)

            client?.let {
                if(it.credits==0){
                    Toast.makeText(context, "Could not book advertisement", Toast.LENGTH_SHORT).show()
                    return@runTransaction
                }

                val advertisementRef = db.collection("advertisements").document(advertisement.id)
                val adSkills = advertisement.skills

                val ad = transaction.get(advertisementRef).toObject(Advertisement::class.java)
                if(ad!!.status!="free"){
                    return@runTransaction
                }

                val advertiserRef = db.collection("users").document(advertisement.uid)
                val advertiser = transaction.get(advertiserRef).toObject(User::class.java)!!

                advertisement.apply {
                    this.bookedSkill = advertisementSkill
                    this.bookedByUID = Firebase.auth.currentUser!!.uid
                    this.bookedByName = client.fullname
                    this.bookedTimestamp = System.currentTimeMillis()
                    this.status = "booked"
                }

                transaction.set(advertisementRef,advertisement)

                client.credits--
                transaction.set(clientRef, client)

                advertiser.credits++
                transaction.set(advertiserRef, advertiser)

                adSkills.forEach { adSkill ->
                    val skillRef = db.collection("skillAdvertisements").document(adSkill)
                    transaction.update(skillRef,"numAdvertisements",FieldValue.increment(-1))
                }
            }
        }
            .addOnFailureListener{
                println(it.message)
            }
    }

}
