package com.exert.wms.returns.salesReturn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.returns.api.*
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SalesReturnBaseViewModel(
    private val stringProvider: StringProvider,
    private val returnsRepo: ReturnsRepository,
    private val warehouseRepo: WarehouseRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<SalesItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<SalesItemsDetailsDto>?> = _itemsList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _branchesStringList = MutableLiveData<List<String>>()
    val branchesStringList: LiveData<List<String>> = _branchesStringList

    private val _pInvoiceStringList = MutableLiveData<List<String>>()
    val pInvoiceStringList: LiveData<List<String>> = _pInvoiceStringList

    private val _customersStringList = MutableLiveData<List<String>>()
    val customersStringList: LiveData<List<String>> = _customersStringList

    private var selectedBranch: String = ""
    private var selectedCustomerName: String = ""
    private var selectedSInvoiceNo: String = ""

    var stockItemsList: ArrayList<SalesItemsDetailsDto> = ArrayList()

    var branchesList: List<BranchDto>? = null
    var customersList: List<CustomerDto>? = null
    private var salesInvoiceList: List<SalesDto>? = null
    var salesItemsList: List<SalesItemsDetailsDto>? = null

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
        if (branchName.isNotEmpty() && branchName != stringProvider.getString(R.string.select_branch)) {
            resetItemsList()
            selectedBranch = branchName
            checkDetails()
        }
    }

    fun selectedCustomerName(customerName: String) {
        if (customerName != stringProvider.getString(R.string.select_customer_name)) {
            resetItemsList()
            selectedCustomerName = customerName
            checkDetails()
        }
    }

    private fun resetItemsList() {
        stockItemsList.clear()
        _itemsList.postValue(null)
        _enableUpdateButton.postValue(false)
    }

    private fun checkDetails() {
        if (selectedBranch.isNotEmpty() && selectedBranch != stringProvider.getString(
                R.string.select_branch
            )
            && selectedCustomerName.isNotEmpty() && selectedCustomerName != stringProvider.getString(
                R.string.select_customer_name
            )
        ) {
            getSalesInvoiceNumbers(getSelectedBranchId(), getSelectedCustomerId())
        }
    }

    private fun getSelectedBranchId(): Long {
        return branchesList?.filter { it.BranchCode == selectedBranch }.run {
            this?.get(0)?.BranchID
        } ?: 0
    }

    private fun getSelectedCustomerId(): Long {
        return customersList?.filter { it.CustomerName == selectedCustomerName }.run {
            this?.get(0)?.CustomerID
        } ?: 0
    }

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun getSalesInvoiceNumbers(branchId: Long, customerId: Long) {
        showProgressIndicator()
        val requestDto = SalesReturnInvoiceRequestDto(BranchID = branchId, CustomerID = customerId)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            returnsRepo.getSalesInvoiceNoList(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "getSalesInvoiceNoList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.SalesList != null && dto.SalesList.isNotEmpty()) {
                        salesInvoiceList = dto.SalesList
                        val stringList = dto.SalesList.map { it.SalesNumber }.toMutableList()
                        stringList.add(
                            0,
                            stringProvider.getString(R.string.select_sales_invoice_no)
                        )
                        _pInvoiceStringList.postValue(stringList)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.sales_invoice_list_empty_message))
                    }
                }
        }
    }

    private fun updatedItems(itemList: ArrayList<SalesItemsDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto = processRequestDto(itemList)
            returnsRepo.saveSalesItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "saveSalesItems response $response")
                    hideProgressIndicator()
                    if (response.Success) {
                        _enableUpdateButton.postValue(false)
                        _saveItemStatus.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.error_save_sales_return_items))
                    }
                }
        }
    }

    private fun processRequestDto(itemList: ArrayList<SalesItemsDetailsDto>): SalesItemsRequestDto {
        val list = ArrayList<SalesSaveItemsDetailsDto>()
        itemList.map {
            val dto = SalesSaveItemsDetailsDto(
                ItemSeqNumber = it.ItemSeqNumber,
                ItemID = it.ItemID,
                WarehouseID = it.WarehouseID,
                UnitID = it.UnitID,
                CategoryID = it.CategoryID,
                Quantity = it.userReturningQty,
                Price = it.Price,
                Factor = it.Factor,
                ExchangeRate = it.ExchangeRate,
                LCYPrice = it.LCYPrice,
                DiscountAmount = it.DiscountAmount,
                DiscountPercentage = it.DiscountPercentage,
                BonusPercentageQuantity = it.BonusPercentageQuantity,
                ItemDiscountPercentage = it.ItemDiscountPercentage,
                ItemDiscount = it.ItemDiscount,
                UnitPrice = it.UnitPrice,
                VATPercentage = it.VATPercentage,
                VATAmount = it.VATAmount,
                SalesItemID = it.SalesItemID,
                TrackingTypes = it.TrackingTypes,
                SerialItems = getSelectedItems(it.SerialItems)
            )
            list.add(dto)
        }

        return SalesItemsRequestDto(
            BranchID = getSelectedBranchId(),
            CustomerID = getSelectedCustomerId(),
            SalesID = getSelectedSInvoiceId(),
            ItemsDetails = list
        )
    }

    private fun getSelectedItems(serialItems: List<SerialItemsDto>?): List<SerialItemsDto> {
        return serialItems?.filter { it.selected } ?: emptyList()
    }

    private fun checkAndEnableUpdateButton() {
        if (stockItemsList.size > 0) {
            val allReturnQtyNotEmpty = stockItemsList.any { it.userReturningQty > 0 }
            _enableUpdateButton.postValue(allReturnQtyNotEmpty)
        } else {
            _enableUpdateButton.postValue(false)
        }
    }

    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

    fun getSelectedBranchIndex(): Int {
        return _branchesStringList.value?.indexOf(selectedBranch) ?: 0
    }

    fun getSelectedCustomerNameIndex(): Int {
        return _customersStringList.value?.indexOf(selectedCustomerName) ?: 0
    }

    fun setSalesItemsDetails(item: SalesItemsDetailsDto?) {
        item?.let { dto ->
            val itemDto = dto.copy(ItemSeqNumber = (getItemListSize() + 1))
            updateItemToList(itemDto)

            getItemList().takeIf { it.isNotEmpty() }?.let { list ->
                _itemsList.postValue(list)
            }
            checkAndEnableUpdateButton()
        }

    }

    private fun updateItemToList(item: SalesItemsDetailsDto) {
        val indexOfObjectToUpdate =
            stockItemsList.indexOfFirst { it.ItemID == item.ItemID && it.ItemCode == item.ItemCode }
        if (indexOfObjectToUpdate != -1) {
            // Replace the object with the updated object
            stockItemsList[indexOfObjectToUpdate] = item
        }
    }

    fun getSelectedSalesInvoiceNoIndex(): Int {
        return _pInvoiceStringList.value?.indexOf(selectedSInvoiceNo) ?: 0
    }

    fun selectedSalesInvoiceNo(invoice: String) {
        if (invoice.isNotEmpty() && invoice != stringProvider.getString(R.string.select_sales_invoice_no)) {
            resetItemsList()
            selectedSInvoiceNo = invoice
            getSalesItemsList()
        }
    }

    private fun getSelectedSInvoiceId(): Long {
        return salesInvoiceList?.filter { it.SalesNumber == selectedSInvoiceNo }.run {
            this?.get(0)?.SalesID
        } ?: 0
    }

    private fun getSalesItemsList() {
        showProgressIndicator()
        val request =
            SalesItemsListItemsRequestDto(SalesID = getSelectedSInvoiceId())//1)//getSelectedPInvoiceId())
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            returnsRepo.getSalesItemsList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getSalesItemsList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Items != null && dto.Items.isNotEmpty()) {
                        salesItemsList = dto.Items
                        stockItemsList.addAll(dto.Items)
                        _itemsList.postValue(dto.Items)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.sales_returns_items_list_empty_message))
                    }
                }
        }
    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        _errorFieldMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_api_access_message
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }
}