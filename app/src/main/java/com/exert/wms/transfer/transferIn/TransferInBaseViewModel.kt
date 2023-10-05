package com.exert.wms.transfer.transferIn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.transfer.api.*
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import com.exert.wms.warehouse.WarehouseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TransferInBaseViewModel(
    private val stringProvider: StringProvider,
    private val transferRepo: TransferRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null
    private var coroutineJobTransferOut: Job? = null

    private val _itemsList = MutableLiveData<List<ExternalTransferItemsDto>?>()
    val itemsList: MutableLiveData<List<ExternalTransferItemsDto>?> = _itemsList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _transferOutNumberStringList = MutableLiveData<List<String>>()
    val transferOutNumberStringList: LiveData<List<String>> = _transferOutNumberStringList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private var selectedFromWarehouse: String = ""
    private var selectedToWarehouse: String = ""
    private var selectedTransferNo: String = ""

    var itemsDto: ItemsDto? = null
    var warehousesList: List<WarehouseDto>? = null
    var externalTransfersOutList: List<ExternalTransfersDto>? = null
    private var transfersInItemsList: ExternalTransferDetailsDto? = null
    var externalTransfersInItemsList: List<ExternalTransferItemsDto>? = null

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
        if (selectedFromWarehouse.isNotEmpty() && selectedFromWarehouse != stringProvider.getString(
                R.string.select_warehouse
            )
            && selectedToWarehouse.isNotEmpty() && selectedToWarehouse != stringProvider.getString(R.string.select_warehouse)
            && selectedFromWarehouse != selectedToWarehouse
        ) {
            getTransferOutNumbers(getSelectedToWarehouseId())
        }
    }

    fun selectedFromWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_warehouse)) {
            resetItemsList()
            selectedFromWarehouse = warehouseName
            checkWarehouse()
        }
    }

    fun selectedToWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_warehouse)) {
            resetItemsList()
            selectedToWarehouse = warehouseName
            checkWarehouse()
        }
    }

    fun selectedTransferOutNo(transferNo: String) {
        if (transferNo != stringProvider.getString(R.string.select_transfer_out_no)) {
            resetItemsList()
            selectedTransferNo = transferNo
            getTransferInItems(getSelectedTransferNoId())
        }
    }

    private fun getTransferInItems(externalTransferID: Long) {
        showProgressIndicator()
        val requestDto = TransferOutItemsRequestDto(ExternalTransferID = externalTransferID)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            transferRepo.getTransferInItemsList(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "getTransferInItemsList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.ExternalTransferDetails != null && dto.ExternalTransferDetails.isNotEmpty()) {
                        transfersInItemsList = dto.ExternalTransferDetails[0]

                        externalTransfersInItemsList = dto.ExternalTransferDetails[0].ItemList
                        _itemsList.postValue(externalTransfersInItemsList)
                        _enableUpdateButton.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.transfer_in_items_list_empty_message))
                    }
                }
        }
    }

    private fun getTransferOutNumbers(toWarehouseId: Long) {
        showProgressIndicator()
        val requestDto = TransferOutNumbersRequestDto(toWarehouseId)
        coroutineJobTransferOut = viewModelScope.launch(dispatcher + exceptionHandler) {
            transferRepo.getTransferOutNumbers(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "getTransferOutNumbers response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.ExternalTransfers?.isNotEmpty() == true) {
                        externalTransfersOutList = dto.ExternalTransfers
                        val stringList =
                            externalTransfersOutList?.map { it.ExternalTransferNumber }?.let {
                                it.toMutableList()
                            } ?: mutableListOf()
                        stringList.add(0, stringProvider.getString(R.string.select_transfer_out_no))
                        _transferOutNumberStringList.postValue(stringList)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.transfer_out_numbers_list_empty_message))
                    }
                }
        }
    }

    private fun resetItemsList() {
        transfersInItemsList = null
        _itemsList.postValue(null)
        _enableUpdateButton.postValue(false)
    }

    fun saveItems() {
        getItemList()?.let { dto ->
            updatedItems(dto)
        }
    }

    private fun updatedItems(dto: ExternalTransferDetailsDto) {
        showProgressIndicator()
        val requestDto = processRequest(dto)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            transferRepo.saveTransferInItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveTransferInItems response $response")
                    hideProgressIndicator()
                    if (response.Success && response.ErrorMessage.isEmpty()) {
                        _enableUpdateButton.postValue(false)
                        _saveItemStatus.postValue(true)
                    } else {
                        if (response.ErrorMessage.isNotEmpty()) {
                            _errorFieldMessage.postValue(response.ErrorMessage)
                        } else {
                            _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_transfer_in_items))
                        }
                    }
                }
        }
    }

    private fun processRequest(externalDto: ExternalTransferDetailsDto): SaveTransferInRequestDto {
        val itemsDetailsList: MutableList<TransferInItemDto> = mutableListOf()
        externalDto.ItemList?.map { dto ->
            itemsDetailsList.add(
                TransferInItemDto(
                    ItemID = dto.ItemID,
                    ItemCode = (dto.ItemCode ?: ""),
                    Quantity = dto.Quantity,
                    SerialItems = dto.SerialItems?.let { it } ?: emptyList()//getConvertedSerialItems(dto.SerialItems)
            ))
        }
        return SaveTransferInRequestDto(
            ToWarehouseID = getSelectedToWarehouseId(),
            FromWarehouseID = getSelectedFromWarehouseId(),
            TransferOutID = getSelectedTransferNoId(),
            ItemsDetails = itemsDetailsList
        )
    }

//    private fun getConvertedSerialItems(list: List<TransferSerialItemListDto>?): List<TransferInSerialItemDto> {
//        val newList=ArrayList<TransferInSerialItemDto>()
//        if(list!=null && list.isNotEmpty()){
//            list.map {
//                newList.add(it.getConvertedTransferInSerialItemDto())
//            }
//        }
//        return newList
//    }

    private fun getItemList() = transfersInItemsList

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

    fun selectedTransferOutNoIndex(): Int {
        return if (selectedFromWarehouse != selectedToWarehouse) {
            _transferOutNumberStringList.value?.indexOf(selectedTransferNo) ?: 0
        } else 0
    }

    fun getItemDto(): ItemsDto? = itemsDto

    private fun getSelectedTransferNoId(): Long {
        return externalTransfersOutList?.filter { it.ExternalTransferNumber == selectedTransferNo }
            .run {
                this?.get(0)?.ExternalTransferID
            } ?: 0
    }

    fun getSelectedToWarehouseId(): Long {
        return warehousesList?.filter { it.Warehouse == selectedToWarehouse }.run {
            this?.get(0)?.WarehouseID
        } ?: 0
    }

    private fun getSelectedFromWarehouseId(): Long {
        return warehousesList?.filter { it.Warehouse == selectedFromWarehouse }.run {
            this?.get(0)?.WarehouseID
        } ?: 0
    }

    fun getSelectedToWarehouseDto(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedToWarehouse }.run {
            this?.get(0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
        coroutineJobTransferOut?.cancel()
    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        _errorFieldMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_api_access_message
            )
        )
    }
}