package it.polito.ma.g14.timebank.models

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class FirebaseVM(application:Application) : AndroidViewModel(application) {

    private val _profile = MutableLiveData<User>()
    val profile : LiveData<User> = _profile

    private val _editProfile = MutableLiveData<User>()
    private val _editProfileImage = MutableLiveData<ByteArray>()

    private val _skills = MutableLiveData<List<SkillAdvertisement>>()
    val skills: LiveData<List<SkillAdvertisement>> = _skills

    private val _myAdvertisements = MutableLiveData<List<Advertisement>>()
    val myAdvertisements: LiveData<List<Advertisement>> = _myAdvertisements

    private val _onlineAdvertisements = MutableLiveData<Map<String, List<Advertisement>>>()
    val onlineAdvertisements: LiveData<Map<String, List<Advertisement>>> = _onlineAdvertisements

    private val _followedAdvertisements = MutableLiveData<List<Advertisement>>()
    val followedAdvertisements : LiveData<List<Advertisement>> = _followedAdvertisements

    private val _completedAdvertisements = MutableLiveData<List<Advertisement>>()
    val completedAdvertisements: LiveData<List<Advertisement>> = _completedAdvertisements

    private val _numMessageNotifications = MutableLiveData<Int>()
    val numMessageNotifications: LiveData<Int> = _numMessageNotifications

    private var profileListener : ListenerRegistration? = null
    private var myAdvertisementsListener: ListenerRegistration? = null

    private var followedAdvertisementsListener: ListenerRegistration? = null
    private var completedAdvertisementsListener: ListenerRegistration? = null

    private var numAdNotificationsListener: ListenerRegistration? = null

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage("gs://mad2022-g14.appspot.com").reference

    init {
        val uid = Firebase.auth.currentUser!!.uid

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _editProfile.value = querySnapshot.toObject(User::class.java)
            }
        //As advertiser
        followedAdvertisementsListener = db.collection("advertisements")
            .whereEqualTo("bookedByUID",uid).addSnapshotListener { result, exception ->
                if(exception!=null){
                    _followedAdvertisements.value = emptyList()}
                else{
                    if(result != null){
                        val followedAds = result.mapNotNull { ads->
                            ads.toObject(Advertisement::class.java)
                        }
                        _followedAdvertisements.value = followedAds
                    }
                }



            }
        //As client
        completedAdvertisementsListener = db.collection("advertisements")
            .whereEqualTo("uid",uid)
            .whereNotEqualTo("status","free")
            .addSnapshotListener { result,exception ->
                if(exception!=null){
                    _completedAdvertisements.value = emptyList()
                }
                else{
                    if(result!=null){
                        val completedads = result.mapNotNull { ads->
                            ads.toObject(Advertisement::class.java)
                        }

                        _completedAdvertisements.value = completedads
                    }
                }

        }



        profileListener = db.collection("users").document(uid)
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
                      profileImage: ByteArray,ratingsAsClient : List<Rating>, ratingsAsAdvertiser: List<Rating>){
        val user = User().apply {
            this.fullname = fullname
            this.nickname = nickname
            this.email = email
            this.location = location
            this.description = description
            this.skills = skills
            this.ratingsAsClient = ratingsAsClient
            this.ratingsAsAdvertiser = ratingsAsAdvertiser
        }


        if(profileImage.isNotEmpty()){
            val profileImageRef = storageRef.child(Firebase.auth.currentUser!!.uid)
            profileImageRef.putBytes(profileImage)
                .addOnFailureListener {
                    Log.w("Timebank FBSTORAGE", "Profile image could not be uploaded")
                }.addOnSuccessListener { _ ->
                    db.collection("users").document(Firebase.auth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            //Update advertiser profile
                            db.collection("advertisements").whereEqualTo("uid",Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val advertisements = result.mapNotNull { it.toObject(Advertisement::class.java) }
                                    advertisements.forEach { advertisement ->
                                        db.runTransaction { transaction ->
                                            val advertisementRef = db.collection("advertisements").document(advertisement.id)
                                            advertisement.user = AdvertisementUser().apply {
                                                this.fullname = user.fullname
                                                this.nickname = user.nickname
                                                this.email = user.email
                                                this.location = user.location
                                                this.description = user.description
                                            }
                                            if(advertisement.advertiserRating!=null){
                                                advertisement.advertiserRating!!.raterName = user.fullname
                                            }
                                            transaction.set(advertisementRef, advertisement)
                                        }

                                    }
                                }

                            //Update advertisement client
                            db.collection("advertisements").whereEqualTo("bookedByUID",Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val advertisements = result.mapNotNull { it.toObject(Advertisement::class.java) }
                                    advertisements.forEach { advertisement ->
                                        db.runTransaction { transaction ->
                                            val advertisementRef = db.collection("advertisements").document(advertisement.id)
                                            advertisement.bookedByName = user.fullname
                                            if(advertisement.clientRating!=null){
                                                advertisement.clientRating!!.raterName = user.fullname
                                            }
                                            transaction.set(advertisementRef, advertisement)
                                        }
                                    }
                                }

                            //Update chat details
                            db.collection("chats").whereEqualTo("advertiserUID",Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val chats = result.mapNotNull { it.toObject(Chat::class.java) }
                                    chats.forEach { chat ->
                                        db.runTransaction { transaction ->
                                            val chatRef = db.collection("chats").document("${chat.clientUID}_${chat.advertisementID}")
                                            chat.advertiserName = user.fullname
                                            chat.chatMessages.forEach {
                                                if(it.senderUID==Firebase.auth.currentUser!!.uid){
                                                    it.senderName = user.fullname
                                                }
                                            }
                                            transaction.set(chatRef, chat)
                                        }
                                    }
                                }

                            //Update chat details
                            db.collection("chats").whereEqualTo("clientUID",Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val chats = result.mapNotNull { it.toObject(Chat::class.java) }
                                    chats.forEach { chat ->
                                        db.runTransaction { transaction ->
                                            val chatRef = db.collection("chats").document("${chat.clientUID}_${chat.advertisementID}")
                                            chat.advertiserName = user.fullname
                                            chat.chatMessages.forEach {
                                                if(it.senderUID==Firebase.auth.currentUser!!.uid){
                                                    it.senderName = user.fullname
                                                }
                                            }
                                            transaction.set(chatRef, chat)
                                        }
                                    }
                                }

                            //Update reviews
                            db.collection("users").get()
                                .addOnSuccessListener { result ->
                                    val users = result.mapNotNull { it.toObject(User::class.java) }
                                    users.forEach { _user ->
                                        _user.ratingsAsAdvertiser.forEach {
                                            if(it.raterUid==Firebase.auth.currentUser!!.uid ){
                                                it.raterName = user.fullname
                                            }
                                        }
                                        _user.ratingsAsClient.forEach {
                                            if(it.raterUid==Firebase.auth.currentUser!!.uid ){
                                                it.raterName = user.fullname
                                            }
                                        }
                                        db.collection("users").document(_user.id).set(_user)
                                    }
                                }
                        }
                    db.collection("users").document(Firebase.auth.currentUser!!.uid)
                        .update("timestamp", Date().toString())
                    Log.d("Timebank FBSTORAGE", "Profile image successfully uploaded")
                }
        }
        else{
            db.collection("users").document(Firebase.auth.currentUser!!.uid)
                .set(user)
                .addOnSuccessListener { _ ->
                    db.collection("users").document(Firebase.auth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            //Update advertiser profile
                            db.collection("advertisements")
                                .whereEqualTo("uid", Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val advertisements =
                                        result.mapNotNull { it.toObject(Advertisement::class.java) }
                                    advertisements.forEach { advertisement ->
                                        db.runTransaction { transaction ->
                                            val advertisementRef = db.collection("advertisements")
                                                .document(advertisement.id)
                                            advertisement.user = AdvertisementUser().apply {
                                                this.fullname = user.fullname
                                                this.nickname = user.nickname
                                                this.email = user.email
                                                this.location = user.location
                                                this.description = user.description
                                            }
                                            if (advertisement.advertiserRating != null) {
                                                advertisement.advertiserRating!!.raterName =
                                                    user.fullname
                                            }
                                            transaction.set(advertisementRef, advertisement)
                                        }

                                    }
                                }

                            //Update advertisement client
                            db.collection("advertisements")
                                .whereEqualTo("bookedByUID", Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val advertisements =
                                        result.mapNotNull { it.toObject(Advertisement::class.java) }
                                    advertisements.forEach { advertisement ->
                                        db.runTransaction { transaction ->
                                            val advertisementRef = db.collection("advertisements")
                                                .document(advertisement.id)
                                            advertisement.bookedByName = user.fullname
                                            if (advertisement.clientRating != null) {
                                                advertisement.clientRating!!.raterName =
                                                    user.fullname
                                            }
                                            transaction.set(advertisementRef, advertisement)
                                        }
                                    }
                                }

                            //Update chat details
                            db.collection("chats")
                                .whereEqualTo("advertiserUID", Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val chats = result.mapNotNull { it.toObject(Chat::class.java) }
                                    chats.forEach { chat ->
                                        db.runTransaction { transaction ->
                                            val chatRef = db.collection("chats")
                                                .document("${chat.clientUID}_${chat.advertisementID}")
                                            chat.advertiserName = user.fullname
                                            chat.chatMessages.forEach {
                                                if (it.senderUID == Firebase.auth.currentUser!!.uid) {
                                                    it.senderName = user.fullname
                                                }
                                            }
                                            transaction.set(chatRef, chat)
                                        }
                                    }
                                }

                            //Update chat details
                            db.collection("chats")
                                .whereEqualTo("clientUID", Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { result ->
                                    val chats = result.mapNotNull { it.toObject(Chat::class.java) }
                                    chats.forEach { chat ->
                                        db.runTransaction { transaction ->
                                            val chatRef = db.collection("chats")
                                                .document("${chat.clientUID}_${chat.advertisementID}")
                                            chat.advertiserName = user.fullname
                                            chat.chatMessages.forEach {
                                                if (it.senderUID == Firebase.auth.currentUser!!.uid) {
                                                    it.senderName = user.fullname
                                                }
                                            }
                                            transaction.set(chatRef, chat)
                                        }
                                    }
                                }

                            //Update reviews
                            db.collection("users").get()
                                .addOnSuccessListener { result ->
                                    val users = result.mapNotNull { it.toObject(User::class.java) }
                                    users.forEach { _user ->
                                        _user.ratingsAsAdvertiser.forEach {
                                            if (it.raterUid == Firebase.auth.currentUser!!.uid) {
                                                it.raterName = user.fullname
                                            }
                                        }
                                        _user.ratingsAsClient.forEach {
                                            if (it.raterUid == Firebase.auth.currentUser!!.uid) {
                                                it.raterName = user.fullname
                                            }
                                        }
                                        db.collection("users").document(_user.id).set(_user)
                                    }
                                }
                        }
                }
        }
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
            .whereEqualTo("status","free")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val adsMap = mutableMapOf<String, MutableList<Advertisement>>()

                querySnapshot.mapNotNull { it.toObject(Advertisement::class.java) }.forEach { advertisement ->
                    if(advertisement.status == "free"){
                        advertisement.skills.forEach{ skill ->
                            adsMap.getOrPut(skill){
                                mutableListOf()
                            }.add(advertisement)
                        }
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




    fun updateAdvertisement(advertisement: Advertisement, context: Context?){
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

            if(oldAdvertisement==null){
                Toast.makeText(context, "Ad could not be updated", Toast.LENGTH_SHORT).show()
                return@runTransaction
            }

            if(oldAdvertisement.status!="free"){
                Toast.makeText(context, "Ad could not be updated as it was booked in the meantime", Toast.LENGTH_SHORT).show()
                return@runTransaction
            }

            transaction.set(advertisementRef, advertisement)

            //Remove skills that were removed from the advertisement
            var diff = oldAdvertisement.skills.filterNot { advertisement.skills.contains(it) }
            diff.forEach {
                val skillRef = db.collection("skillAdvertisements").document(it)
                transaction.update(skillRef, "numAdvertisements", FieldValue.increment(-1))
            }

            //Add skills that were added to the advertisement
            oldAdvertisement.skills.let {
                diff = advertisement.skills.filterNot { oldAdvertisement.skills.contains(it) }
                diff.forEach {
                    val skillRef = db.collection("skillAdvertisements").document(it)
                    transaction.update(skillRef, "numAdvertisements", FieldValue.increment(1))
                }
            }
        }
        .addOnSuccessListener {
            Toast.makeText(context, "Advertisement correctly edited", Toast.LENGTH_SHORT).show()
            updateAdvertisementSkillsList()
            updateAdvertisementList()
        }
    }

    fun deleteAdvertisement(advertisementID: String, context: Context?){
        db.runTransaction { transaction ->
            val advertisementRef = db.collection("advertisements").document(advertisementID)
            val advertisement = transaction.get(advertisementRef).toObject(Advertisement::class.java)

            if(advertisement==null){
                Toast.makeText(context, "Ad could not be deleted", Toast.LENGTH_SHORT).show()
                return@runTransaction
            }

            if(advertisement.status!="free"){
                Toast.makeText(context, "Ad could not be deleted as it was booked in the meantime", Toast.LENGTH_SHORT).show()
                return@runTransaction
            }

            transaction.delete(advertisementRef)
            advertisement.skills.let {
                for(skill in it){
                    val skillRef = db.collection("skillAdvertisements").document(skill)
                    transaction.update(skillRef, "numAdvertisements", FieldValue.increment(-1))
                }
            }

        }
        .addOnSuccessListener {
            Toast.makeText(context, "Advertisement successfully deleted", Toast.LENGTH_SHORT).show()
            Log.d("Timebank FIREBASE", "Advertisement with ID: $advertisementID deleted & numAdvertisements decremented")
            updateAdvertisementSkillsList()
            updateAdvertisementList()
        }
        .addOnFailureListener { e ->
            Log.w("Timebank FIREBASE", "Error deleting advertisement", e)
        }
    }

    fun getMessageNotifications(){
        val uid = Firebase.auth.currentUser!!.uid
        db.collection("chats").addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            }
            var numNotifications = 0
            value?.let {

                val chats1 = value.mapNotNull { it.toObject(Chat::class.java) }
                numNotifications += chats1.filter { it.advertiserUID==uid }.sumOf { it.advertiserNotifications }
                numNotifications += chats1.filter { it.clientUID==uid }.sumOf { it.clientNotifications }

                _numMessageNotifications.postValue(numNotifications)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        profileListener?.remove()
        myAdvertisementsListener?.remove()
    }
}
