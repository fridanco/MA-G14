package it.polito.ma.g14.timebank.models

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class FirebaseVM(application:Application) : AndroidViewModel(application) {

    private val _profile = MutableLiveData<User>()
    val profile : LiveData<User> = _profile

    private val _editProfile = MutableLiveData<User>()
    val editProfile : LiveData<User> = _editProfile
    private val _editProfileImage = MutableLiveData<ByteArray>()
    val editProfileImage : LiveData<ByteArray> = _editProfileImage

    private val _skills = MutableLiveData<List<SkillAdvertisement>>()
    val skills: LiveData<List<SkillAdvertisement>> = _skills

    private val _myAdvertisements = MutableLiveData<List<Advertisement>>()
    val myAdvertisements: LiveData<List<Advertisement>> = _myAdvertisements

    private val _onlineAdvertisements = MutableLiveData<Map<String, List<Advertisement>>>()
    val onlineAdvertisements: LiveData<Map<String, List<Advertisement>>> = _onlineAdvertisements

    private var profileListener : ListenerRegistration? = null
    private var myAdvertisementsListener: ListenerRegistration? = null

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    init {
        val uid = Firebase.auth.uid

        db.collection("users").document(uid!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _editProfile.value = querySnapshot.toObject(User::class.java)
            }


        profileListener = db.collection("users").document(uid!!)
            .addSnapshotListener{ result, exception ->
                if (exception != null) {
                    User()
                }
                else{
                    result?.let {
                        _profile.postValue(it.toObject(User::class.java))
                    } ?: throw Exception("Could not load user profile")
                }
            }

        myAdvertisementsListener = db.collection("advertisements")
            .whereEqualTo("uid",uid)
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    _myAdvertisements.value = emptyList()
                }
                else{
                    if(result!=null){
                        val myAds = result.mapNotNull { advertisement ->
                            advertisement.toObject(
                                Advertisement::class.java
                            )
                        }
                        _myAdvertisements.value = myAds
                    }
                    else{
                        throw Exception("Could not retrieve user advertisements")
                    }
                }
            }
    }

    fun updateProfile(fullname: String, nickname: String, email: String, location: String, description: String, skills: List<String>,
                      profileImage: ByteArray,ratingProfile : Float, n_rating:Int){
        val user = User().apply {
            this.fullname = fullname
            this.nickname = nickname
            this.email = email
            this.location = location
            this.description = description
            this.skills = skills
            this.ratings = ratingProfile
            this.n_ratings = n_rating
        }


        if(profileImage.isNotEmpty()){
            val profileImageRef = storageRef.child(Firebase.auth.currentUser!!.uid)
            profileImageRef.putBytes(profileImage)
                .addOnFailureListener {
                    Log.w("Timebank FBSTORAGE", "Profile image could not be uploaded")
                }.addOnSuccessListener { taskSnapshot ->
                    db.collection("users").document(Firebase.auth.currentUser!!.uid)
                        .set(user)
                    db.collection("users").document(Firebase.auth.currentUser!!.uid)
                        .update("timestamp",Date().toString())
                    Log.d("Timebank FBSTORAGE", "Profile image successfully uploaded")
                }
        }
        else{
            db.collection("users").document(Firebase.auth.currentUser!!.uid)
                .set(user)
        }
    }

    fun updateProfileSkills(skills: List<String>){
        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .update("skills",skills)
    }

    fun removeProfileSkill(skills: List<String>, skillToRemove: String){
        val newSkills = skills as MutableList
        newSkills.remove(skillToRemove)
        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .update("skills", newSkills)
    }

    fun updateAdvertisementSkillsList(){
        db.collection("skillAdvertisements")
            .whereGreaterThan("numAdvertisements",0)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _skills.value = querySnapshot.mapNotNull { skillAdvertisement ->
                    skillAdvertisement.toObject(
                        SkillAdvertisement::class.java
                    )
                }
            }
    }

    fun updateAdvertisementList(){
        db.collection("advertisements")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val adsMap = mutableMapOf<String, MutableList<Advertisement>>()

                querySnapshot.mapNotNull { it.toObject(Advertisement::class.java) }.forEach { advertisement ->
                    advertisement.skills.forEach{ skill ->
                        adsMap.getOrPut(skill){
                            mutableListOf()
                        }.add(advertisement)
                    }
                }

                _onlineAdvertisements.value = adsMap
            }
    }

    fun addAdvertisement(advertisement: Advertisement){
        advertisement.apply {
            this.uid = Firebase.auth.currentUser!!.uid
            this.user.fullname = _profile.value!!.fullname
            this.user.nickname = _profile.value!!.nickname
            this.user.email = _profile.value!!.email
            this.user.location = _profile.value!!.location
            this.user.description = _profile.value!!.description
        }

        db.collection("advertisements").add(advertisement)
            .addOnSuccessListener {
                Log.d("Timebank FIREBASE", "Advertisement posted with ID: ${it.id}")
                db.runTransaction { transaction ->
                    for (skill in advertisement.skills) {
                        val ref = db.collection("skillAdvertisements").document(skill)
                        transaction.update(ref, "numAdvertisements", FieldValue.increment(1))
                    }
                }
                .addOnSuccessListener{
                    updateAdvertisementSkillsList()
                    updateAdvertisementList()
                }
            }
            .addOnFailureListener { e ->
                Log.w("Timebank FIREBASE", "Error adding advertisement", e)
            }
    }

    fun updateAdvertisement(advertisement: Advertisement){
        advertisement.apply{
            this.uid = Firebase.auth.currentUser!!.uid
            this.user.fullname = _profile.value!!.fullname
            this.user.nickname = _profile.value!!.nickname
            this.user.email = _profile.value!!.email
            this.user.location = _profile.value!!.location
            this.user.description = _profile.value!!.description
        }

        db.runTransaction { transaction ->
            val advertisementRef = db.collection("advertisements").document(advertisement.id)
            val oldAdvertisement = transaction.get(advertisementRef).toObject(Advertisement::class.java)

            transaction.set(advertisementRef, advertisement)

            //Remove skills that were removed from the advertisement
            var diff = oldAdvertisement?.skills?.filterNot { advertisement.skills.contains(it) }
            diff?.forEach {
                val skillRef = db.collection("skillAdvertisements").document(it)
                transaction.update(skillRef, "numAdvertisements", FieldValue.increment(-1))
            }

            //Add skills that were added to the advertisement
            oldAdvertisement?.skills?.let {
                diff = advertisement.skills.filterNot { oldAdvertisement.skills.contains(it) }
                diff?.forEach {
                    val skillRef = db.collection("skillAdvertisements").document(it)
                    transaction.update(skillRef, "numAdvertisements", FieldValue.increment(1))
                }
            }
        }
        .addOnSuccessListener {
            updateAdvertisementSkillsList()
            updateAdvertisementList()
        }
    }

    fun deleteAdvertisement(advertisementID: String){
        db.runTransaction { transaction ->
            val advertisementRef = db.collection("advertisements").document(advertisementID)
            val advertisement = transaction.get(advertisementRef).toObject(Advertisement::class.java)
            transaction.delete(advertisementRef)
            advertisement?.skills?.let {
                for(skill in it){
                    val skillRef = db.collection("skillAdvertisements").document(skill)
                    transaction.update(skillRef, "numAdvertisements", FieldValue.increment(-1))
                }
            }
            null
        }
        .addOnSuccessListener {
            Log.d("Timebank FIREBASE", "Advertisement with ID: ${advertisementID} deleted & numAdvertisements decremented")
            updateAdvertisementSkillsList()
            updateAdvertisementList()
        }
        .addOnFailureListener { e ->
            Log.w("Timebank FIREBASE", "Error deleting advertisement", e)
        }

    }

    fun getProfileValue(): User {
        return _profile.value ?: User()
    }

    override fun onCleared() {
        super.onCleared();
        profileListener?.remove()
        myAdvertisementsListener?.remove()
    }
}
