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

class AdSkillsVM(application:Application) : AndroidViewModel(application) {

    private val _showWelcome = MutableLiveData<Boolean>(true)
    val showWelcome : LiveData<Boolean> = _showWelcome

    fun hideWelcome(){
        _showWelcome.value = false
    }

    fun getWelcomeStatus(): Boolean? {
        return _showWelcome.value
    }

}
