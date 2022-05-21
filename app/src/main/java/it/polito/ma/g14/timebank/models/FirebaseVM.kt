package it.polito.ma.g14.timebank.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FirebaseVM(application:Application) : AndroidViewModel(application) {

    private val _profile = MutableLiveData<User>()
    val profile : LiveData<User> = _profile

    private val _profileImageUpdated = MutableLiveData<Int>(0)
    val profileImageUpdated : LiveData<Int> = _profileImageUpdated

    private val _skills = MutableLiveData<List<SkillAdvertisement>>()
    val skills: LiveData<List<SkillAdvertisement>> = _skills

    private val _myAdvertisements = MutableLiveData<List<Advertisement>>()
    val myAdvertisements: LiveData<List<Advertisement>> = _myAdvertisements

    private val _onlineAdvertisements = MutableLiveData<Map<String, List<Advertisement>>>()
    val onlineAdvertisement: LiveData<Map<String, List<Advertisement>>> = _onlineAdvertisements


    private var skillAdvertisementListener: ListenerRegistration? = null
    private var profileListener : ListenerRegistration? = null
    private var myAdvertisementsListener: ListenerRegistration? = null

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    init {
        val uid = Firebase.auth.uid

        profileListener = db.collection("users").document(uid!!)
            .addSnapshotListener{ result, exception ->
                _profile.value = if (exception != null) {
                    User()
                }
                else{
                    if(result!=null){
                        result.toObject(User::class.java)
                    }
                    else{
                        throw Exception("Could not load user profile")
                    }
                }
            }

        skillAdvertisementListener = db.collection("skillAdvertisements")
            .whereGreaterThan("numAdvertisements",0)
            .addSnapshotListener { result, exception ->
                _skills.value = if (exception != null) {
                    emptyList()
                }
                else{
                    if(result!=null){
                        result.mapNotNull { skillAdvertisement ->
                            skillAdvertisement.toObject(
                                SkillAdvertisement::class.java
                            )
                        }
                    }
                    else{
                        throw Exception("Could not load homepage skills")
                    }
                }
            }

        db.collection("advertisements")
            .get()
            .addOnSuccessListener { querySnapshot ->
                var adsMap = mutableMapOf<String, MutableList<Advertisement>>()

                querySnapshot.mapNotNull { it.toObject(Advertisement::class.java) }.forEach { advertisement ->
                    advertisement.skills.forEach{ skill ->
                        adsMap.getOrPut(skill){
                            mutableListOf()
                        }.add(advertisement)
                    }
                }

                _onlineAdvertisements.value = adsMap
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

    fun updateProfile(fullname: String, nickname: String, email: String, location: String, description: String, skills: List<String>){
        val user = User().apply {
            this.fullname = fullname
            this.nickname = nickname
            this.email = email
            this.location = location
            this.description = description
            this.skills = skills
        }

        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .set(user)
    }

    fun uploadProfileImage(profileImage: ByteArray){
        val profileImageRef = storageRef.child(Firebase.auth.currentUser!!.uid)

        profileImageRef.putBytes(profileImage)
            .addOnFailureListener {
                Log.w("Timebank FBSTORAGE", "Profile image could not be uploaded")
            }.addOnSuccessListener { taskSnapshot ->
                setProfileImageUpdated()
                Log.d("Timebank FBSTORAGE", "Profile image successfully uploaded")
            }
    }

    fun setProfileImageUpdated(){
        _profileImageUpdated.value = profileImageUpdated.value?.plus(1);
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
                for(skill in advertisement.skills){
                    db.collection("skillAdvertisements").document(skill)
                        .update("numAdvertisements",FieldValue.increment(1))
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

        }
        .addOnFailureListener { e ->
            Log.w("Timebank FIREBASE", "Error deleting advertisement", e)
        }

    }

    override fun onCleared() {
        super.onCleared();
        skillAdvertisementListener?.remove();
        profileListener?.remove()
        myAdvertisementsListener?.remove()
    }
}
