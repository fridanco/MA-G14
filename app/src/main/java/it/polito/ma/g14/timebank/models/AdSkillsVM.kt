package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AdSkillsVM(application:Application) : AndroidViewModel(application) {

    private val _showWelcome = MutableLiveData(true)
    val showWelcome : LiveData<Boolean> = _showWelcome

    private val _sortBy = MutableLiveData("skill_asc")
    val sortBy : LiveData<String> = _sortBy

    fun hideWelcome(){
        _showWelcome.value = false
    }

    fun getWelcomeStatus(): Boolean? {
        return _showWelcome.value
    }

    fun setSortBy(sort: String){
        _sortBy.value = sort
    }
    fun getSortBy() : String{
        return _sortBy.value ?: "skill_asc"
    }

}
