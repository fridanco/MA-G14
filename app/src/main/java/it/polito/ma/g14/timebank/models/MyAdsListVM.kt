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

class MyAdsListVM(application:Application) : AndroidViewModel(application) {

    private val _sortBy = MutableLiveData<String>("date_desc")
    val sortBy : LiveData<String> = _sortBy

    fun setSortBy(sort: String){
        _sortBy.value = sort
    }
    fun getSortBy() : String{
        return _sortBy.value ?: "date_desc"
    }

}
