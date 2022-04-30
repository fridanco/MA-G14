package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.LiveData

class ProfileREPO(application: Application) {
    private val profileDAO = ProfileDB.getDatabase(application).profileDAO()

    fun profile() : LiveData<Profile> = profileDAO.getProfile()
    fun isProfileInitialized() : LiveData<Int> = profileDAO.isProfileCreated()
    fun skills() : LiveData<List<Skill>> = profileDAO.getSkills()

    fun initProfile(){
        val profile = Profile().apply {
            this.id = 1
            this.fullname = "Sample fullname"
            this.nickname = "Sample nickname"
            this.email = "sample@email.com"
            this.location = "Sample, 123/A, SP"
            this.description = "Just some sample description...in order to update your profile click the pencil icon above."
        }
        profileDAO.createProfile(profile)
    }

    fun updateProfile(fullname: String, nickname: String, email: String, location: String, description: String){
        val profile = Profile().apply {
            this.id = 1
            this.fullname = fullname
            this.nickname = nickname
            this.email = email
            this.location = location
            this.description = description
        }
        profileDAO.updateProfile(profile)
    }

    fun addSkill(skill: String){
        profileDAO.addSkill(Skill().apply { this.skill = skill })
    }

    fun removeSkill(skill: String){
        profileDAO.removeSkill(Skill().apply {this.skill = skill })
    }
}