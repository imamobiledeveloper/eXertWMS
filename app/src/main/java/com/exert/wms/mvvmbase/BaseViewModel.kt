package com.exert.wms.mvvmbase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseViewModel : ViewModel() {

    val disposable = CompositeDisposable()
    val hyphensStr = "------"
    val getData = MutableLiveData<Result>().apply {
        value = Result.Loading
    }

    private val _isLoadingData = MutableLiveData<Boolean>()
    val isLoadingData: LiveData<Boolean> = _isLoadingData

    val exceptionHandler= CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    open fun handleException(throwable: Throwable) {
        hideProgressIndicator()
    }

     fun hideProgressIndicator() {
        _isLoadingData.postValue(false)
    }

     fun showProgressIndicator() {
        _isLoadingData.postValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}