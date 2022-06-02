package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileVM(application:Application) : AndroidViewModel(application) {

    private val _profile = MutableLiveData<User>()
    val profile : LiveData<User> = _profile

    private val _editProfile = MutableLiveData<User>()
    val editProfile : LiveData<User> = _editProfile
    private val _editProfileImage = MutableLiveData(byteArrayOf())
    val editProfileImage : LiveData<ByteArray> = _editProfileImage

    private var profileListener : ListenerRegistration? = null

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    init {
        val uid = Firebase.auth.uid

        db.collection("users").document(uid!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _editProfile.value = querySnapshot.toObject(User::class.java)
            }

        storageRef.child(uid).getBytes(5*1024*1024)
            .addOnSuccessListener { byteArray ->
                if(_editProfileImage.value?.isEmpty() == true) {
                    _editProfileImage.value = byteArray
                }
            }
            .addOnFailureListener {
                _editProfileImage.value = byteArrayOf()
            }



    }

    fun setProfileData(fullname: String, nickname: String, email: String, location: String, description: String, skills: List<String>){
        _editProfile.value = User().apply {
            this.fullname = fullname
            this.nickname = nickname
            this.email = email
            this.location = location
            this.description = description
            this.skills = skills
        }
    }

    fun setProfileImage(profileImage: ByteArray){
        _editProfileImage.value = profileImage
    }

    fun getProfileImage() : ByteArray? {
        return _editProfileImage.value
    }

    fun updateProfileSkills(skills: List<String>){
        _editProfile.value?.skills = skills
    }

    override fun onCleared() {
        super.onCleared()
        profileListener?.remove()
    }
}
