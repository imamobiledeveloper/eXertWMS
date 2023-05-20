package com.exert.wms.itemStocks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.itemStocks.api.ItemStocksRequestDto
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ItemStocksViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<ItemsDto>>()
    val itemsList: LiveData<List<ItemsDto>> = _itemsList

    private val _getItemsStatus = MutableLiveData<Boolean>()
    val getItemsStatus: LiveData<Boolean> = _getItemsStatus

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    private val _itemStockStatus = MutableLiveData<Boolean>()
    val itemStockStatus: LiveData<Boolean> = _itemStockStatus

    private val _getItemsSerialNosStatus = MutableLiveData<Boolean>()
    val getItemsSerialNosStatus: LiveData<Boolean> = _getItemsSerialNosStatus

    private fun getOnlineSalesItems(itemCode: String) {
        var itemCode = "130000"
        showProgressIndicator()

        val request = ItemStocksRequestDto(itemCode)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getOnlineSalesItems(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getOnlineSalesItems response $dto")
                    hideProgressIndicator()
                    if (dto.success) {
//                        _getItemsStatus.postValue(true)
                        _itemsList.postValue(dto.itemsList)
                    } else {
                        _getItemsStatus.postValue(false)
                    }
                }

        }
    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        Log.v("WMS EXERT", "Error ${throwable.message}")
        _errorGetItemsStatusMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_get_items_message
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun checkItemStock(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo)) {
            _itemStockStatus.postValue(true)
        }
//        else{
//            _itemStockStatus.postValue(false)
//        }
    }

    private fun validateUserDetails(itemPartCode: String, itemSerialNo: String): Boolean {
        return if (itemPartCode.isNotEmpty() || itemSerialNo.isNotEmpty()) {
            _errorItemSelectionMessage.postValue(true)
            true
        } else {
            _errorItemSelectionMessage.postValue(false)
            false
        }
    }

    fun searchItemWithPartCode(partCode: String) {

    }

    fun searchItemWithSerialNumber(serialNo: String) {

    }

}