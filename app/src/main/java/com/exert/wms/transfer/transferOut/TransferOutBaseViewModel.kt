package com.exert.wms.transfer.transferOut

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.transfer.api.TransferOutItemDetailsDto
import com.exert.wms.transfer.api.TransferOutRequestDto
import com.exert.wms.transfer.api.TransferRepository
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import com.exert.wms.warehouse.WarehouseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TransferOutBaseViewModel(
    private val stringProvider: StringProvider,
    private val transferRepo: TransferRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<TransferOutItemDetailsDto>?>()
    val itemsList: MutableLiveData<List<TransferOutItemDetailsDto>?> = _itemsList

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
    var transferOutItemsList: ArrayList<TransferOutItemDetailsDto> = ArrayList()
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
        if (warehouseName != stringProvider.getString(R.string.select_warehouse)) {
            selectedFromWarehouse = warehouseName
        }
        resetItemsList()
    }

    fun selectedToWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_warehouse)) {
            selectedToWarehouse = warehouseName
        }
        resetItemsList()
    }

    private fun resetItemsList() {
        transferOutItemsList.clear()
        _itemsList.postValue(null)
        _enableUpdateButton.postValue(false)
    }

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(stockList: ArrayList<TransferOutItemDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto =
                TransferOutRequestDto(
                    TransferOutID = 0,
                    FromWarehouseID = getSelectedFromWarehouseId(),
                    ToWarehouseID = getSelectedToWarehouseId(),
                    ItemsDetails = stockList.toList()
                )
            transferRepo.saveTransferOutItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveTransferOutItems response $response")
                    hideProgressIndicator()
                    if (response.Success) {
                        _enableUpdateButton.postValue(false)
                        _saveItemStatus.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_transfer_out_items))
                    }
                }
        }
    }

    private fun checkAndEnableUpdateButton() {
        if (transferOutItemsList.size > 0) {
            _enableUpdateButton.postValue(true)
        } else {
            _enableUpdateButton.postValue(false)
        }
    }

    private fun addItemToList(item: TransferOutItemDetailsDto) {
        transferOutItemsList.add(item)
    }

    private fun getItemList() = transferOutItemsList

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
        return if (selectedFromWarehouse != selectedToWarehouse) {
            _warehouseStringList.value?.indexOf(selectedFromWarehouse) ?: 0
        } else if (selectedFromWarehouse == selectedToWarehouse && selectedFromWarehouse != stringProvider.getString(
                R.string.select_warehouse
            ) && selectedFromWarehouse.isNotEmpty()
        ) {
            selectedFromWarehouse = ""
            _errorFieldMessage.postValue(stringProvider.getString(R.string.from_and_to_warehouse_should_not_be_same))
            0
        } else 0
    }

    fun getSelectedToWarehouseIndex(): Int {
        return if (selectedFromWarehouse != selectedToWarehouse) {
            _warehouseStringList.value?.indexOf(selectedToWarehouse) ?: 0
        } else if (selectedFromWarehouse == selectedToWarehouse && selectedToWarehouse != stringProvider.getString(
                R.string.select_warehouse
            ) && selectedToWarehouse.isNotEmpty()
        ) {
            selectedToWarehouse = ""
            _errorFieldMessage.postValue(stringProvider.getString(R.string.from_and_to_warehouse_should_not_be_same))
            0
        } else 0
    }

    fun setTransferOutItemDetails(item: TransferOutItemDetailsDto?) {
        item?.let { dto ->
            val itemDto = dto.copy(ItemSeqNumber = (getItemListSize() + 1))
            addItemToList(itemDto)

            getItemList().takeIf { it.isNotEmpty() }?.let { list ->
                _itemsList.postValue(list)
            }
            checkAndEnableUpdateButton()
        }
    }

    fun getItemDto(): ItemsDto? = itemsDto

    fun getSelectedFromWarehouseId(): Long {
        return warehousesList?.filter { it.Warehouse == selectedFromWarehouse }.run {
            this?.get(0)?.WarehouseID
        } ?: 0
    }

    fun getSelectedFromWarehouseDto(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedFromWarehouse }.run {
            this?.get(0)
        }
    }

    fun getSelectedToWarehouseId(): Long {
        return warehousesList?.filter { it.Warehouse == selectedToWarehouse }.run {
            this?.get(0)?.WarehouseID
        } ?: 0
    }
}