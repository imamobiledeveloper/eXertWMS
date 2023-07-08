package com.exert.wms.stockReconciliation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockReconciliation.api.ReconciliationItemsDetailsDto
import com.exert.wms.stockReconciliation.api.StockReconciliationRepository
import com.exert.wms.stockReconciliation.api.StockReconciliationRequestDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import com.exert.wms.warehouse.WarehouseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StockReconciliationBaseViewModel(
    private val stringProvider: StringProvider,
    private val stockReconciliationRepo: StockReconciliationRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

//    private val _enableSaveButton = MutableLiveData<Boolean>().apply { false }
//    val enableSaveButton: LiveData<Boolean> = _enableSaveButton

    private val _itemsList = MutableLiveData<List<ReconciliationItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<ReconciliationItemsDetailsDto>?> = _itemsList

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage
//
//    private val _navigateToSerialNo = MutableLiveData<Boolean>()
//    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo
//
//    private val _errorItemPartCode = MutableLiveData<Boolean>()
//    val errorItemPartCode: LiveData<Boolean> = _errorItemPartCode
//
//    private val _errorItemSerialNo = MutableLiveData<Boolean>()
//    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

//    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
//    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage
//
//    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
//    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    //    private var itemPartCode: String = ""
//    var itemSerialNo: String = ""
    private var selectedWarehouse: String = ""

    //    private var selectedLocation: String = ""
    private var warehouseDto: WarehouseDto? = null
    var warehousesList: List<WarehouseDto>? = null

    var itemsDto: ItemsDto? = null
    var stockItemsList: ArrayList<ReconciliationItemsDetailsDto> = ArrayList()

    init {
        getWarehouseList()
    }

    private fun getWarehouseList() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            warehouseRepo.getWarehouseList()
                .collect { dto ->
                    Log.v("WMS EXERT", "getWarehouseList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Warehouses.isNotEmpty()) {
                        warehousesList = dto.Warehouses
                        val stringList = dto.Warehouses.map { it.Warehouse }.toMutableList()
                        stringList.add(0, stringProvider.getString(R.string.select_warehouse))

                        _warehouseStringList.postValue(stringList)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_list_empty_message))
                    }
                }
        }
    }

    fun checkWarehouse() {
        if (selectedWarehouse.isNotEmpty() && selectedWarehouse != stringProvider.getString(R.string.select_warehouse)) {
            _errorWarehouse.postValue(true)
        } else {
            _errorWarehouse.postValue(false)
        }
    }

//    fun checkItemDetailsEntered(itemPartCode: String, itemSerialNo: String) {
//        if (validateUserDetails(itemPartCode, itemSerialNo)) {
//            _navigateToSerialNo.postValue(true)
//        }
//    }

//    private fun validateUserDetails(
//        itemPartCode: String,
//        itemSerialNo: String
//    ): Boolean {
//        return if (selectedWarehouse.isEmpty()) {
//            _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_empty_message))
//            false
//        } else if (selectedLocation.isEmpty()) {
//            _errorFieldMessage.postValue(stringProvider.getString(R.string.location_empty_message))
//            false
//        } else if (itemPartCode.isEmpty() && itemSerialNo.isEmpty()) {
//            _errorItemPartCode.postValue(true)
//            false
//        } else if (itemsDto == null) {
//            _errorFieldMessage.postValue(stringProvider.getString(R.string.invalid_details_message))
//            false
//        } else {
//            _errorItemSelectionMessage.postValue(true)
//            true
//        }
//    }
//    fun setItemPartCodeValue(partCode: String) {
//        itemPartCode = partCode
//    }
//
//    fun searchItemWithPartCode() {
//        if (itemPartCode.isNotEmpty() && warehouseDto != null) {
////            getWarehouseSerialNosList(itemPartCode, "", warehouseDto!!.WarehouseID)
//            _errorItemPartCode.postValue(false)
//        } else {
//            _errorItemPartCode.postValue(true)
//        }
//    }
//
//    fun setItemSerialNumberValue(serialNo: String) {
//        itemSerialNo = serialNo
//    }
//
//    fun searchItemWithSerialNumber() {
//        if (itemSerialNo.isNotEmpty() && warehouseDto != null) {
////            getWarehouseSerialNosList("", itemSerialNo, warehouseDto!!.WarehouseID)
//            _errorItemSerialNo.postValue(false)
//        } else {
//            _errorItemSerialNo.postValue(true)
//        }
//    }
//
//    fun checkWarehouse() {
//        if (selectedWarehouse.isNotEmpty() && selectedWarehouse != stringProvider.getString(R.string.select_warehouse)) {
//            _errorWarehouse.postValue(true)
//        } else {
//            _errorWarehouse.postValue(false)
//        }
//    }

    fun getWarehouseObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)
        }
    }

//    private fun getItemId() = run {
//        itemsDto?.let { it.ItemID } ?: 0
//    }
//
//    private fun getItemCode() = run {
//        itemsDto?.let { it.ItemCode } ?: ""
//    }

    private fun getWarehouseId() = run {
        warehouseDto?.let { it.WarehouseID } ?: 0
    }

    fun getItemDto(): ItemsDto? = itemsDto

    fun selectedWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_warehouse)) {
            selectedWarehouse = warehouseName
        }
        resetItemsList()
    }

    private fun resetItemsList() {
        stockItemsList.clear()
        _itemsList.postValue(null)
    }

    fun getSelectedWarehouseIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedWarehouse) ?: 0
    }

    private fun addItemToList(item: ReconciliationItemsDetailsDto) {
        stockItemsList.add(item)
    }

    fun setStockItemDetails(item: ReconciliationItemsDetailsDto?) {
        item?.let { dto ->
            val itemDto = dto.copy(ItemSeqNumber = (getItemListSize() + 1))
            addItemToList(itemDto)

            getItemList().takeIf { it.isNotEmpty() }?.let { list ->
                _itemsList.postValue(list)
            }
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

    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(stockList: ArrayList<ReconciliationItemsDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto =
                StockReconciliationRequestDto(StockAdjustmentID = 0, ItemsDetails = stockList)
            stockReconciliationRepo.saveStockReconciliationItems(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "saveStockReconciliationItems response $dto")
                    hideProgressIndicator()

                }
        }
    }

}