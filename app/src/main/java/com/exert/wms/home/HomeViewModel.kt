package com.exert.wms.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.login.LoginRepository
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.UserDefaults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class HomeViewModel(
    private val userDefaults: UserDefaults
) : BaseViewModel() {

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> = _loggedOut

    fun logoutUser() {
        userDefaults.clear()
        _loggedOut.postValue(true)
    }

}