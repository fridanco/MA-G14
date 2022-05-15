package it.polito.ma.g14.timebank.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FirebaseVM(application:Application) : AndroidViewModel(application) {

    private val _skills = MutableLiveData<List<SkillAdvertisement>>()
    val skills: LiveData<List<SkillAdvertisement>> = _skills

    private val _ads = MutableLiveData<List<Advertisement>>()
    val ads: LiveData<List<Advertisement>> = _ads

    private val skillAdvertisementListener: ListenerRegistration

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
    }

    override fun onCleared() {
        super.onCleared(); skillAdvertisementListener.remove();
    }
}
