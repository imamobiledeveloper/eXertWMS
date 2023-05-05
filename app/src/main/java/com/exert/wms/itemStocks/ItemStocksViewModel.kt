package com.exert.wms.itemStocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.mvvmbase.BaseViewModel

class ItemStocksViewModel : BaseViewModel() {

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> = _loggedOut

//    fun logoutUser() {
//        userDefaults.clear()
//        _loggedOut.postValue(true)
//    }

}