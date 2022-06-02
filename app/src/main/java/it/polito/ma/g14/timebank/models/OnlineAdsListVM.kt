package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class OnlineAdsListVM(application:Application) : AndroidViewModel(application) {

    private val _sortBy = MutableLiveData("date_desc")
    val sortBy : LiveData<String> = _sortBy

    fun setSortBy(sort: String){
        _sortBy.value = sort
    }
    fun getSortBy() : String{
        return _sortBy.value ?: "date_desc"
    }

}
