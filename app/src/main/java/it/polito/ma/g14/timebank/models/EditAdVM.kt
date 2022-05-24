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

class EditAdVM(application:Application) : AndroidViewModel(application) {

    private val _myAdvertisements = MutableLiveData<List<Advertisement>>()
    val myAdvertisements: LiveData<List<Advertisement>> = _myAdvertisements

    private val _newAdvertisement = MutableLiveData<Advertisement>()
    val newAdvertisement: LiveData<Advertisement> = _newAdvertisement

    private val _newAdvertisementSkillList = MutableLiveData<Pair<String, Boolean>>()
    val newAdvertisementSkillList: LiveData<Pair<String, Boolean>> = _newAdvertisementSkillList

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    init {
        val uid = Firebase.auth.uid



        db.collection("advertisements")
            .whereEqualTo("uid",uid)
            .get()
            .addOnSuccessListener {
                val myAds = it.mapNotNull { advertisement ->
                    advertisement.toObject(
                        Advertisement::class.java
                    )
                }
                _myAdvertisements.value = myAds
            }
    }

    fun setAdvertisement(id: String, title: String, description: String, date: String, from: String, to: String, location: String, skillList: List<String>){
        val ad = _myAdvertisements.value?.find { it.id==id }
        val index = _myAdvertisements.value?.indexOf(ad)
        val ads = _myAdvertisements.value?.toMutableList()
        if (index != null && index!=-1) {
            ads?.set(index, Advertisement().apply {
                this.title = title
                this.description = description
                this.date = date
                this.from = from
                this.to = to
                this.location = location
                this.skills = skillList
            })
        }
        _myAdvertisements.value = ads?.toList()

    }

    fun setNewAdvertisement(title: String, description: String, date: String, from: String, to: String, location: String, skillList: List<String>){
        _newAdvertisement.value = Advertisement().apply {
            this.title = title
            this.description = description
            this.date = date
            this.from = from
            this.to = to
            this.location = location
            this.skills = skillList
        }
    }

    fun getNewAdvertisementSkills() : List<String> {
        if (_newAdvertisement.value != null){
            return _newAdvertisement.value!!.skills
        }
        return listOf()
    }

}
