package it.polito.ma.g14.timebank.fragments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class FirebaseVM(application:Application) : AndroidViewModel(application) {

    private val _profile = MutableLiveData<User>()
    val profile : LiveData<User> = _profile

    private val _skills = MutableLiveData<List<SkillAdvertisement>>()
    val skills: LiveData<List<SkillAdvertisement>> = _skills

    private val _myAdvertisements = MutableLiveData<List<Advertisement>>()
    val myAdvertisements: LiveData<List<Advertisement>> = _myAdvertisements

    private val _ads = MutableLiveData<List<Advertisement>>()
    val ads: LiveData<List<Advertisement>> = _ads

    private var profileListener : ListenerRegistration? = null
    private var skillAdvertisementListener: ListenerRegistration? = null
    private var myAdvertisementsListener: ListenerRegistration? = null

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

        myAdvertisementsListener = db.collection("advertisements")
            .whereEqualTo("user.id",uid)
            .addSnapshotListener { result, exception ->
                _myAdvertisements.value = if (exception != null) {
                    emptyList()
                }
                else{
                    if(result!=null){
                        result.mapNotNull { advertisement ->
                            advertisement.toObject(
                                Advertisement::class.java
                            )
                        }
                    }
                    else{
                        throw Exception("Could not retrieve user advertisements")
                    }
                }
            }
    }

    fun destroyListeners(){
        skillAdvertisementListener?.remove();
        profileListener?.remove()
        myAdvertisementsListener?.remove()
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

    fun updateProfileSkills(skills: List<String>){
        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .update("skills",skills)
    }

    fun removeProfileSkill(skills: List<String>, skillToRemove: String){
        val newSkills = skills as MutableList
        newSkills.remove(skillToRemove)
        println("OLD size: ${skills.size} vs NEW size: ${newSkills.size}")
        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .update("skills", newSkills)
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
        super.onCleared();
        skillAdvertisementListener?.remove();
        profileListener?.remove()
        myAdvertisementsListener?.remove()
    }
}
