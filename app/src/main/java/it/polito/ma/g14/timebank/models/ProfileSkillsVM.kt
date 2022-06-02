package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ProfileSkillsVM(application:Application) : AndroidViewModel(application) {

    private val _profileSkills = MutableLiveData<List<String>>(listOf())
    val profileSkills : LiveData<List<String>> = _profileSkills

    fun setSkills(skillList: List<String>){
        _profileSkills.value = skillList
    }

    fun setSkillsFromArgument(skillList: List<String>){
        if(_profileSkills.value?.isEmpty() == true) {
            _profileSkills.value = skillList
        }
    }

}
