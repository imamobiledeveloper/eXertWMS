package com.exert.wms.delivery.deliveryReceipt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.delivery.api.DeliveryRepository
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.returns.api.DeliveryReceiptItemsDetailsDto
import com.exert.wms.returns.api.DeliveryReceiptItemsRequestDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import com.exert.wms.warehouse.WarehouseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DeliveryReceiptBaseViewModel(
    private val stringProvider: StringProvider,
    private val deliveryRepo: DeliveryRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<DeliveryReceiptItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<DeliveryReceiptItemsDetailsDto>?> = _itemsList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private var selectedFromWarehouse: String = ""
    private var selectedToWarehouse: String = ""
    var stockItemsList: ArrayList<DeliveryReceiptItemsDetailsDto> = ArrayList()
    var itemsDto: ItemsDto? = null
    var warehousesList: List<WarehouseDto>? = null

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

    fun getWarehouseFromObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedFromWarehouse }.run {
            this?.get(0)
        }
    }

    fun selectedFromWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_from_warehouse)) {
            selectedFromWarehouse = warehouseName
        }
        resetItemsList()
    }

    fun selectedToWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_to_warehouse)) {
            selectedToWarehouse = warehouseName
        }
        resetItemsList()
    }

    private fun resetItemsList() {
        stockItemsList.clear()
        _itemsList.postValue(null)
        _enableUpdateButton.postValue(false)
    }

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(stockList: ArrayList<DeliveryReceiptItemsDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto =
                DeliveryReceiptItemsRequestDto(StockAdjustmentID = 0, ItemsDetails = stockList)
            deliveryRepo.saveDeliveryReceiptItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveDeliveryReceiptItems response $response")
                    hideProgressIndicator()
                    if (response.Success) {
                        _enableUpdateButton.postValue(false)
                        _saveItemStatus.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_delivery_receipt_items))
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

    private fun addItemToList(item: DeliveryReceiptItemsDetailsDto) {
        stockItemsList.add(item)
    }

    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

    fun checkWarehouse() {
        if (selectedFromWarehouse.isNotEmpty() && selectedFromWarehouse != stringProvider.getString(
                R.string.select_warehouse
            )
            && selectedToWarehouse.isNotEmpty() && selectedToWarehouse != stringProvider.getString(R.string.select_warehouse)
        ) {
            _errorWarehouse.postValue(true)
        } else {
            _errorWarehouse.postValue(false)
        }
    }

    fun getSelectedFromWarehouseIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedFromWarehouse) ?: 0
    }

    fun getSelectedToWarehouseIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedToWarehouse) ?: 0
    }

    fun setDeliveryReceiptItemsDetailsDto(item: DeliveryReceiptItemsDetailsDto?) {
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