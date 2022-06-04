package it.polito.ma.g14.timebank.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyMessagesVM : ViewModel() {

    private val _sortBy = MutableLiveData<String>()
    val sortBy : LiveData<String> = _sortBy

    fun setSortBy(sort: String){
        _sortBy.value = sort
    }
    fun getSortBy() : String{
        return _sortBy.value ?: "msg_desc"
    }

    private val _filterBy = MutableLiveData<String>()
    val filterBy : LiveData<String> = _filterBy

    fun getFilterBy() : String{
        return _filterBy.value ?: ""
    }

    fun setFilterBy(sortKey: String) {
        _filterBy.value = sortKey
    }

}