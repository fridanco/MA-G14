package it.polito.ma.g14.timebank.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.ma.g14.timebank.models.Profile
import it.polito.ma.g14.timebank.models.ProfileREPO
import kotlin.concurrent.thread

class ProfileVM(application: Application) : AndroidViewModel(application) {

    private val _inMemoryProfile = Profile()

    private val _inMemoryProfileValue = MutableLiveData<Profile>().apply {
        value = _inMemoryProfile
    }

    val inMemoryProfile: LiveData<Profile> = _inMemoryProfileValue

    fun updateInMemoryProfile(fullname: String, nickname: String, email: String, location: String, description: String){
        _inMemoryProfile.fullname = fullname
        _inMemoryProfile.nickname = nickname
        _inMemoryProfile.email = email
        _inMemoryProfile.location = location
        _inMemoryProfile.description = description
    }

    val repo = ProfileREPO(application)

    val profile = repo.profile()
    val isProfileInitalized = repo.isProfileInitialized()
    val skills = repo.skills()

    fun initProfile(){
        thread {
            repo.initProfile()
        }
    }

    fun updateProfile(fullname: String, nickname: String, email: String, location: String, description: String){
        thread {
            repo.updateProfile(fullname, nickname, email, location, description)
        }
    }

    fun addSkill(skill: String){
        thread {
            repo.addSkill(skill)
        }
    }

    fun removeSkill(skill: String){
        thread {
            repo.removeSkill(skill)
        }
    }

    fun removeAllSkills(){
        thread {
            repo.removeAllSkills()
        }
    }

}