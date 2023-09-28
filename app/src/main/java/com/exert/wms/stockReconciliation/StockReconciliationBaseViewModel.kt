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

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private var selectedWarehouse: String = ""
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
                    if (dto.success && dto.Warehouses!=null && dto.Warehouses.isNotEmpty()) {
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

    fun getWarehouseObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)
        }
    }

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
                .collect { response ->
                    Log.v("WMS EXERT", "saveStockReconciliationItems response $response")
                    hideProgressIndicator()
//                    if (response.Success) {
//                        _enableUpdateButton.postValue(false)
//                        _saveItemStatus.postValue(true)
//                    } else {
//                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_stock_reconciliation_items))
//                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }
}