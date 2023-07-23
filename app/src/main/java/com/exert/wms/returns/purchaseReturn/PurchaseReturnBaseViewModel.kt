package com.exert.wms.returns.purchaseReturn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.returns.api.PurchaseItemsDetailsDto
import com.exert.wms.returns.api.PurchaseItemsRequestDto
import com.exert.wms.returns.api.ReturnsRepository
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import com.exert.wms.warehouse.WarehouseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PurchaseReturnBaseViewModel(
    private val stringProvider: StringProvider,
    private val returnsRepo: ReturnsRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<PurchaseItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<PurchaseItemsDetailsDto>?> = _itemsList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorBranch = MutableLiveData<Boolean>()
    val errorBranch: LiveData<Boolean> = _errorBranch

    private var selectedBranch: String = ""
    private var selectedCustomerName: String = ""
    private var selectedSalesInvoiceNo: String = ""
    var stockItemsList: ArrayList<PurchaseItemsDetailsDto> = ArrayList()
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
        return warehousesList?.filter { it.Warehouse == selectedBranch }.run {
            this?.get(0)
        }
    }

    fun selectedBranch(branchName: String) {
        if (branchName != stringProvider.getString(R.string.select_branch)) {
            selectedBranch = branchName
        }
        resetItemsList()
    }

    fun selectedCustomerName(customerName: String) {
        if (customerName != stringProvider.getString(R.string.select_vendor_name)) {
            selectedCustomerName = customerName
        }
        resetItemsList()
    }

//    fun selectedTransferOutNo(transferNo: String) {
//        if (transferNo != stringProvider.getString(R.string.select_transfer_out_no)) {
//            selectedTransferNo = transferNo
//        }
//        resetItemsList()
//    }

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

    private fun updatedItems(stockList: ArrayList<PurchaseItemsDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto =
                PurchaseItemsRequestDto(StockAdjustmentID = 0, ItemsDetails = stockList)
            returnsRepo.savePurchaseItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveDeliveryNoteItems response $response")
                    hideProgressIndicator()
                    if (response.Success) {
                        _enableUpdateButton.postValue(false)
                        _saveItemStatus.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_purchase_return_items))
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

    private fun addItemToList(item: PurchaseItemsDetailsDto) {
        stockItemsList.add(item)
    }

    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

    fun checkWarehouse() {
        if (selectedBranch.isNotEmpty() && selectedBranch != stringProvider.getString(
                R.string.select_branch
            )
            && selectedCustomerName.isNotEmpty() && selectedCustomerName != stringProvider.getString(
                R.string.select_vendor_name
            )
        ) {
            _errorBranch.postValue(true)
        } else {
            _errorBranch.postValue(false)
        }
    }

    fun getSelectedBranchIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedBranch) ?: 0
    }

    fun getSelectedCustomerNameIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedCustomerName) ?: 0
    }

    fun setPurchaseItemsDetails(item: PurchaseItemsDetailsDto?) {
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