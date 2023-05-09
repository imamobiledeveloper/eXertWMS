package com.exert.wms.itemStocks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.itemStocks.api.ItemStocksRequestDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ItemStocksViewModel (
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _apiAccessStatus = MutableLiveData<Boolean>()
    val apiAccessStatus: LiveData<Boolean> = _apiAccessStatus

    private val _errorApiAccessMessage = MutableLiveData<String>()
    val errorApiAccessMessage: LiveData<String> = _errorApiAccessMessage

    private fun getOnlineSalesItems(itemCode: String) {
        var itemCode = "130000"
        showProgressIndicator()

        val request = ItemStocksRequestDto(itemCode)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getOnlineSalesItems(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "itemStocksRepo response $dto")

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