package it.polito.ma.g14.timebank.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ShowProfileRatingVM : ViewModel() {

    private val _profile = MutableLiveData<User>()
    val profile : LiveData<User> = _profile

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    fun getProfileReviews(uid: String){
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                _profile.value = documentSnapshot.toObject(User::class.java)
            }
    }

}