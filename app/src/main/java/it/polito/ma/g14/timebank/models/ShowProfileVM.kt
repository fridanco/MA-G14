package it.polito.ma.g14.timebank.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ShowProfileVM : ViewModel() {

    private val _numPostedAds = MutableLiveData<Int>()
    val numPostedAds : LiveData<Int> = _numPostedAds

    private val _numBookedAds = MutableLiveData<Int>()
    val numBookedAds : LiveData<Int> = _numBookedAds

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    fun getNumPostedAds(uid: String){
        db.collection("advertisements")
            .whereEqualTo("uid",uid)
            .get()
            .addOnSuccessListener { result ->
                _numPostedAds.postValue(result.size())
            }
    }

    fun getNumBookedAds(uid: String){
        db.collection("advertisements")
            .whereEqualTo("bookedByUID",uid)
            .get()
            .addOnSuccessListener { result ->
                _numBookedAds.postValue(result.size())
            }
    }

}