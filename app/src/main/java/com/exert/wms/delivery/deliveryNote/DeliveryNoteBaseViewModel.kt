package com.exert.wms.delivery.deliveryNote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.delivery.api.*
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.transfer.api.TransferInSerialItemDto
import com.exert.wms.transfer.api.TransferSerialItemListDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DeliveryNoteBaseViewModel(
    private val stringProvider: StringProvider,
    private val deliveryRepo: DeliveryRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<DeliveryNoteItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<DeliveryNoteItemsDetailsDto>?> = _itemsList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _salesOrdersStringList = MutableLiveData<List<String>>()
    val salesOrdersStringList: LiveData<List<String>> = _salesOrdersStringList

    private val _branchesStringList = MutableLiveData<List<String>>()
    val branchesStringList: LiveData<List<String>> = _branchesStringList

    private val _customersStringList = MutableLiveData<List<String>>()
    val customersStringList: LiveData<List<String>> = _customersStringList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorBranch = MutableLiveData<Boolean>()
    val errorBranch: LiveData<Boolean> = _errorBranch

    private var selectedBranch: String = ""
    private var selectedCustomerName: String = ""
    private var selectedSalesInvoiceNo: String = ""

    private var salesOrdersList: List<SalesOrdersDto>? = null
    var branchesList: List<BranchDto>? = null
    var customersList: List<CustomerDto>? = null
    var deliveryNotesItemsList: List<DeliveryNoteItemsDetailsDto>? = null

    init {
        getBranchesAndCustomerList()
    }

    private fun getBranchesAndCustomerList() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + CoroutineExceptionHandler { _, _ ->
            hideProgressIndicator()
        }) {
            combine(
                getBranchesList(),
                getCustomersList()
            ) { wList, cList ->
                Pair(wList, cList)
            }.collect { result ->
                hideProgressIndicator()
                Log.v("WMS EXERT", "getBranchesAndCustomerList response $result")
                hideProgressIndicator()
                processBranchesList(result.first)
                processCustomersList(result.second)

            }
        }
    }

    private fun processBranchesList(dto: BranchesListDto) {
        if (dto.success && dto.Branches != null && dto.Branches.isNotEmpty()) {
            branchesList = dto.Branches
            val stringList = dto.Branches.map { it.BranchCode }.toMutableList()
            stringList.add(0, stringProvider.getString(R.string.select_branch))
            _branchesStringList.postValue(stringList)
        } else {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.branches_list_empty_message))
        }
    }

    private fun processCustomersList(dto: CustomersListDto) {
        if (dto.success && dto.Customers != null && dto.Customers.isNotEmpty()) {
            customersList = dto.Customers
            val stringList = dto.Customers.map { it.CustomerName }.toMutableList()
            stringList.add(0, stringProvider.getString(R.string.select_customer_name))
            _customersStringList.postValue(stringList)
        } else {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.customers_list_empty_message))
        }
    }

    private fun getBranchesList(): Flow<BranchesListDto> {
        return warehouseRepo.getBranchesList()
    }

    private fun getCustomersList(): Flow<CustomersListDto> {
        return warehouseRepo.getCustomersList()
    }

    fun selectedBranch(branchName: String) {
        if (branchName != stringProvider.getString(R.string.select_branch)) {
            resetItemsList()
            selectedBranch = branchName
            checkWarehouse()
        }

    }

    fun selectedCustomerName(customerName: String) {
        if (customerName != stringProvider.getString(R.string.select_customer_name)) {
            resetItemsList()
            selectedCustomerName = customerName
            checkWarehouse()
        }
    }

    fun selectedSalesInvoiceNo(salesInvoiceNo: String) {
        if (salesInvoiceNo != stringProvider.getString(R.string.select_sales_invoice_no)) {
            resetItemsList()
            selectedSalesInvoiceNo = salesInvoiceNo
            getDeliveryNotesItemsList()
        }
    }

    private fun getDeliveryNotesItemsList() {
        showProgressIndicator()
        val request = DeliveryNoteItemsListRequestDto(
            BranchID = getSelectedBranchId(),
            CustomerID = getSelectedCustomerNameIndex(),
            SalesOrderIDs = listOf(SalesOrderIDDto(SalesOrderID = getSelectedSalesOrdersId())),
            ItemsDetails = emptyList()
        )
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            deliveryRepo.getDeliveryNotesItemsList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getDeliveryNotesItemsList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Items != null && dto.Items.isNotEmpty()) {
                        deliveryNotesItemsList = dto.Items
                        _itemsList.postValue(dto.Items)
                        _enableUpdateButton.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.delivery_notes_items_list_empty_message))
                    }
                }
        }
    }

    private fun resetItemsList() {
        deliveryNotesItemsList = null
        _itemsList.postValue(null)
        _enableUpdateButton.postValue(false)
    }

    fun saveItems() {
        getItemList().takeIf { it?.isNotEmpty() == true }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(list: List<DeliveryNoteItemsDetailsDto>) {
        showProgressIndicator()
        val requestDto = processRequest(list)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            deliveryRepo.saveDeliveryNoteItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveDeliveryNoteItems response $response")
                    hideProgressIndicator()
                    if (response.Success) {
                        _enableUpdateButton.postValue(false)
                        _saveItemStatus.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_delivery_notes_items))
                    }
                }
        }
    }

    private fun processRequest(list: List<DeliveryNoteItemsDetailsDto>): DeliveryNoteItemsListRequestDto {

        val itemsDetailsList: MutableList<DeliveryNoteItemDto> = mutableListOf()
        list.map { dto ->
            itemsDetailsList.add(
                DeliveryNoteItemDto(
                    ItemSeqNumber = itemsDetailsList.size + 1,
                    ItemID = dto.ItemID,
                    WarehouseID = dto.WarehouseID,
                    UnitID = dto.UnitID,
                    SalesOrderItemID = dto.SalesOrderItemID,
                    TrackingTypes = dto.TrackingTypes,
                    Quantity = dto.Quantity,
                    SerialItems = getConvertedSerialItems(dto.SerialItems)
                )
            )
        }
        return DeliveryNoteItemsListRequestDto(
            BranchID = getSelectedBranchId(),
            CustomerID = getSelectedCustomerNameIndex(),
            SalesOrderIDs = listOf(SalesOrderIDDto(SalesOrderID = getSelectedSalesOrdersId())),
            ItemsDetails = itemsDetailsList
        )
    }

    private fun getConvertedSerialItems(list: List<TransferSerialItemListDto>?): List<TransferInSerialItemDto> {
        val newList = ArrayList<TransferInSerialItemDto>()
        if (list != null && list.isNotEmpty()) {
            list.map {
                newList.add(it.getConvertedTransferInSerialItemDto())
            }
        }
        return newList
    }

    private fun getItemList() = deliveryNotesItemsList

    fun checkWarehouse() {
        if (selectedBranch.isNotEmpty() && selectedBranch != stringProvider.getString(
                R.string.select_branch
            )
            && selectedCustomerName.isNotEmpty() && selectedCustomerName != stringProvider.getString(
                R.string.select_customer_name
            )
        ) {
            getSalesInvoiceNumbers(getSelectedBranchId(), getSelectedVendorId())
        }
    }

    private fun getSelectedBranchId(): Long {
        return branchesList?.filter { it.BranchCode == selectedBranch }.run {
            this?.get(0)?.BranchID
        } ?: 0
    }

    private fun getSelectedVendorId(): Long {
        return customersList?.filter { it.CustomerName == selectedCustomerName }.run {
            this?.get(0)?.CustomerID
        } ?: 0
    }

    private fun getSelectedSalesOrdersId(): Long {
        return salesOrdersList?.filter { it.SalesOrderNumber == selectedSalesInvoiceNo }.run {
            this?.get(0)?.SalesOrderID
        } ?: 0
    }

    private fun getSalesInvoiceNumbers(branchId: Long, customerId: Long) {
        showProgressIndicator()
        val requestDto = SalesOrdersRequestDto(BranchID = branchId, CustomerID = customerId)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            deliveryRepo.getSalesOrdersList(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "getSalesOrdersList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.SalesOrders != null && dto.SalesOrders.isNotEmpty()) {
                        salesOrdersList = dto.SalesOrders
                        val stringList = dto.SalesOrders.map { it.SalesOrderNumber }.toMutableList()
                        stringList.add(
                            0,
                            stringProvider.getString(R.string.select_sales_invoice_no)
                        )
                        _salesOrdersStringList.postValue(stringList)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.sales_invoice_list_empty_message))
                    }
                }
        }
    }

    fun getSelectedBranchIndex(): Int {
        return _branchesStringList.value?.indexOf(selectedBranch) ?: 0
    }

    fun getSelectedCustomerNameIndex(): Int {
        return _customersStringList.value?.indexOf(selectedCustomerName) ?: 0
    }

    fun getSelectedSalesInvoiceNoIndex(): Int {
        return _salesOrdersStringList.value?.indexOf(selectedSalesInvoiceNo) ?: 0
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }
}