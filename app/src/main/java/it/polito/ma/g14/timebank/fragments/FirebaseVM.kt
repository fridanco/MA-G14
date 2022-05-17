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

    private val _profile  = MutableLiveData<List<User>>()
    val profile : LiveData<List<User>> = _profile;

    private val skillAdvertisementListener: ListenerRegistration
    private val profileUserListener : ListenerRegistration

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

        profileUserListener = db.collection("users").addSnapshotListener{result,exception ->
            _profile.value = if (exception != null){
                emptyList()
            }
            else{
                result?.let { result.mapNotNull { user ->
                    user.toObject(User::class.java)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared(); skillAdvertisementListener.remove();
    }
}
