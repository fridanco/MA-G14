package it.polito.ma.g14.timebank.fragments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class FirebaseVM(application:Application) : AndroidViewModel(application) {

    private val _skills = MutableLiveData<List<SkillAdvertisement>>()
    val skills: LiveData<List<SkillAdvertisement>> = _skills

    private val _myAdvertisements = MutableLiveData<List<Advertisement>>()
    val myAdvertisements: LiveData<List<Advertisement>> = _myAdvertisements

    private val _ads = MutableLiveData<List<Advertisement>>()
    val ads: LiveData<List<Advertisement>> = _ads

    private val skillAdvertisementListener: ListenerRegistration
    private val myAdvertisementsListener: ListenerRegistration

    private val db: FirebaseFirestore

    init {
        db = FirebaseFirestore.getInstance()

        skillAdvertisementListener = db.collection("skillAdvertisements")
        .addSnapshotListener { result, exception ->
            _skills.value = if (exception != null) {
                emptyList()
            }
            else{
                result?.let {
                    result.mapNotNull { skillAdvertisement ->
                        skillAdvertisement.toObject(
                            SkillAdvertisement::class.java
                        )
                    }
                }
            }
        }

        myAdvertisementsListener = db.collection("advertisements")
            .whereEqualTo("user.id","abcdef")
            .addSnapshotListener { result, exception ->
                _myAdvertisements.value = if (exception != null) {
                    emptyList()
                }
                else{
                    result?.let {
                        result.mapNotNull { advertisement ->
                            advertisement.toObject(
                                Advertisement::class.java
                            )
                        }
                    }
                }
            }
    }

    fun getAdvertisement(advertisementID: String) = _ads.value?.find { it.id == advertisementID }

    fun postAdvertisement(advertisement: Advertisement){
        db.collection("advertisements").add(advertisement)
            .addOnSuccessListener {
                Log.d("Timebank FIREBASE", "Advertisement posted with ID: ${it.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Timebank FIREBASE", "Error adding advertisement", e)
            }
    }

    fun updateAdvertisement(advertisement: Advertisement){
        db.collection("advertisements").document(advertisement.id)
            .set(advertisement, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Timebank FIREBASE", "Advertisement with ID: ${advertisement.id} updated")
            }
            .addOnFailureListener { e ->
                Log.w("Timebank FIREBASE", "Error updating advertisement", e)
            }
    }

    fun deleteAdvertisement(advertisementID: String){
        db.collection("advertisements").document(advertisementID)
            .delete()
            .addOnSuccessListener {
                Log.d("Timebank FIREBASE", "Advertisement with ID: ${advertisementID} deleted")
            }
            .addOnFailureListener { e ->
                Log.w("Timebank FIREBASE", "Error deleting advertisement", e)
            }
    }

    override fun onCleared() {
        super.onCleared(); skillAdvertisementListener.remove();
    }
}
