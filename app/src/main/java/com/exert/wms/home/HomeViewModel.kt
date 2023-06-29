package com.exert.wms.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.UserDefaults

class HomeViewModel(
    private val userDefaults: UserDefaults
) : BaseViewModel() {

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> = _loggedOut

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    init {
        _userName.postValue(userDefaults.getUserName())
    }
    fun logoutUser() {
//        userDefaults.clear()
        _loggedOut.postValue(true)
    }

}