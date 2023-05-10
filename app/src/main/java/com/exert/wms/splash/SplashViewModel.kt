package com.exert.wms.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.login.api.LoginRepository
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SplashViewModel(
    private val stringProvider: StringProvider,
    private val loginRepo: LoginRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _apiAccessStatus = MutableLiveData<Boolean>()
    val apiAccessStatus: LiveData<Boolean> = _apiAccessStatus

    private val _errorApiAccessMessage = MutableLiveData<String>()
    val errorApiAccessMessage: LiveData<String> = _errorApiAccessMessage

    init {
        getApiAccess()
    }

    private fun getApiAccess() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            loginRepo.getApiAccess()
                .collect { response ->
                    Log.v("WMS EXERT", "response $response")
                    _apiAccessStatus.postValue(true)
                }

        }
    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        Log.v("WMS EXERT", "Error ${throwable.message}")
        _errorApiAccessMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_api_access_message
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

}