package com.exert.wms.stockAdjustment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.StockAdjustmentRepository
import com.exert.wms.stockAdjustment.api.StockAdjustmentRequestDto
import com.exert.wms.stockAdjustment.api.StockItemsDetailsDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import com.exert.wms.warehouse.WarehouseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StockAdjustmentBaseViewModel(
    private val stringProvider: StringProvider,
    private val warehouseRepo: WarehouseRepository,
    private val stockAdjustmentRepo: StockAdjustmentRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<StockItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<StockItemsDetailsDto>?> = _itemsList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private var selectedWarehouse: String = ""

    var stockItemsList: ArrayList<StockItemsDetailsDto> = ArrayList()

    var itemsDto: ItemsDto? = null
    var warehousesList: List<WarehouseDto>? = null


    init {
        getWarehouseList()
    }

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(stockList: ArrayList<StockItemsDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto =
                StockAdjustmentRequestDto(StockAdjustmentID = 0, ItemsDetails = stockList)
            stockAdjustmentRepo.saveStockAdjustmentItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveStockAdjustmentItems response $response")
                    hideProgressIndicator()
                    if (response.Success) {
                        _saveItemStatus.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_stock_adjustment_items))
                    }
                }
        }
    }

    private fun checkAndEnableUpdateButton() {
        if (stockItemsList.size > 0) {
            _enableUpdateButton.postValue(true)
        } else {
            _enableUpdateButton.postValue(false)
        }
    }

    private fun addItemToList(item: StockItemsDetailsDto) {
        stockItemsList.add(item)
    }

    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

    fun checkWarehouse() {
        if (selectedWarehouse.isNotEmpty() && selectedWarehouse != stringProvider.getString(R.string.select_warehouse)) {
            _errorWarehouse.postValue(true)
        } else {
            _errorWarehouse.postValue(false)
        }
    }

    private fun getWarehouseList() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            warehouseRepo.getWarehouseList()
                .collect { dto ->
                    Log.v("WMS EXERT", "getWarehouseList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Warehouses != null && dto.Warehouses.isNotEmpty()) {
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

    fun getWarehouseObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)
        }
    }

    fun getSelectedWarehouseId(): Long {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)?.WarehouseID
        } ?: 0
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

    fun setStockItemDetails(item: StockItemsDetailsDto?) {
        item?.let { dto ->
            val itemDto = dto.copy(ItemSeqNumber = (getItemListSize() + 1))
            addItemToList(itemDto)

            getItemList().takeIf { it.isNotEmpty() }?.let { list ->
                _itemsList.postValue(list)
            }
            checkAndEnableUpdateButton()
        }
    }

}