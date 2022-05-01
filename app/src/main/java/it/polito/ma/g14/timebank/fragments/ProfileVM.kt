package it.polito.ma.g14.timebank.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.polito.ma.g14.timebank.models.ProfileREPO
import kotlin.concurrent.thread

class ProfileVM(application: Application) : AndroidViewModel(application) {

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

}