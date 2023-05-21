package com.exert.wms.stockAdjustment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.item.StockItemAdjustmentDto
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class StockAdjustmentBaseViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _enableSaveButton = MutableLiveData<Boolean>().apply { false }
    val enableSaveButton: LiveData<Boolean> = _enableSaveButton

    private val _itemsList = MutableLiveData<List<String>>()
    val itemsList: LiveData<List<String>> = _itemsList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _getItemsStatus = MutableLiveData<Boolean>()
    val getItemsStatus: LiveData<Boolean> = _getItemsStatus

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _navigateToSerialNo = MutableLiveData<Boolean>()
    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo

    private val _errorItemPartCode = MutableLiveData<Boolean>()
    val errorItemPartCode: LiveData<Boolean> = _errorItemPartCode

    private val _errorItemSerialNo = MutableLiveData<Boolean>()
    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

    var itemPartCode: String = ""
    var itemSerialNo: String = ""
    var warehouse: String = ""
    var stockItemsList = mutableListOf<StockItemAdjustmentDto>()

    var adjustmentTypeValue: String = ""

    fun saveItems() {

    }

    fun saveItemStock(warehouse: String, itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(warehouse, itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            val item = StockItemAdjustmentDto(
                warehouse,
                itemPartCode,
                itemSerialNo,
                "",
                "",
                "",
                0,
                adjustmentTypeValue,
                0,
                0.0,
                0.0
            )
            addItemToList(item)
            _saveItemStatus.postValue(true)
            checkAndEnableUpdateButton()
        }
    }

    private fun checkAndEnableUpdateButton() {
        if (stockItemsList.size > 0) {
            _enableUpdateButton.postValue(true)
        } else {
            _enableUpdateButton.postValue(false)
        }
    }

    private fun addItemToList(item: StockItemAdjustmentDto) {
        stockItemsList.add(item)
    }

    private fun validateUserDetails(
        warehouse: String,
        itemPartCode: String,
        itemSerialNo: String,
        adjustmentType: String
    ): Boolean {
        return if (warehouse.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_empty_message))
            false
        } else if (adjustmentType.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.adjustment_type_empty_message))
            false
        } else if (itemPartCode.isNotEmpty()) {
            _errorItemPartCode.postValue(true)
            false
        } else if (itemSerialNo.isNotEmpty()) {
            _errorItemSerialNo.postValue(true)
            true
        } else {
            _errorItemSelectionMessage.postValue(true)
            false
        }
    }

    fun setAdjustmentType(adjustment: String) {
        adjustmentTypeValue = adjustment
    }

    fun checkItemDetailsEntered(warehouse: String, itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(warehouse, itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            _navigateToSerialNo.postValue(true)
        } else {
            _navigateToSerialNo.postValue(false)
        }
    }

    fun searchItemWithPartCode(partCode: String) {
        itemPartCode = partCode
        if (itemPartCode.isNotEmpty()) {
            // api call
            _errorItemPartCode.postValue(false)
        } else {
            _errorItemPartCode.postValue(true)
        }
        checkAndEnableStatusButton()
    }

    private fun  checkAndEnableStatusButton() {
        if (validateUserDetails(warehouse,itemPartCode, itemSerialNo,adjustmentTypeValue)) {
            _enableSaveButton.postValue(true)
            _errorItemPartCode.postValue(false)
            _errorItemSerialNo.postValue(false)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    fun searchItemWithSerialNumber(serialNo: String) {
        itemSerialNo = serialNo
        if (itemSerialNo.isNotEmpty()) {
            // api call
            _errorItemSerialNo.postValue(false)
        } else {
            _errorItemSerialNo.postValue(true)
        }
        checkAndEnableStatusButton()

    }
}