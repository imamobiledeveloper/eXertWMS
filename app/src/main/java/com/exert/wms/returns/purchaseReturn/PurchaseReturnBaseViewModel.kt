package com.exert.wms.returns.purchaseReturn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.returns.api.*
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorBranch = MutableLiveData<Boolean>()
    val errorBranch: LiveData<Boolean> = _errorBranch

    private val _branchesStringList = MutableLiveData<List<String>>()
    val branchesStringList: LiveData<List<String>> = _branchesStringList

    private val _vendorsStringList = MutableLiveData<List<String>>()
    val vendorsStringList: LiveData<List<String>> = _vendorsStringList

    private val _pInvoiceStringList = MutableLiveData<List<String>>()
    val pInvoiceStringList: LiveData<List<String>> = _pInvoiceStringList

    private var selectedBranch: String = ""
    private var selectedVendor: String = ""
    private var selectedPInvoiceNo: String = ""
    var stockItemsList: ArrayList<PurchaseItemsDetailsDto> = ArrayList()
    var itemsDto: ItemsDto? = null

    var branchesList: List<BranchDto>? = null
    var vendorsList: List<VendorDto>? = null
    private var purchaseInvoiceList: List<PurchaseDto>? = null
    var purchaseItemsList: List<PurchaseItemsDetailsDto>? = null

    init {
        getBranchesAndVendorList()
    }

    private fun getBranchesAndVendorList() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + CoroutineExceptionHandler { _, _ ->
            hideProgressIndicator()
        }) {
            combine(
                getBranchesList(),
                getVendorsList()
            ) { wList, cList ->
                Pair(wList, cList)
            }.collect { result ->
                hideProgressIndicator()
                Log.v("WMS EXERT", "getBranchesAndVendorList response $result")
                hideProgressIndicator()
                processBranchesList(result.first)
                processVendorsList(result.second)

            }
        }
    }

    private fun getBranchesList(): Flow<BranchesListDto> {
        return warehouseRepo.getBranchesList()
    }

    private fun getVendorsList(): Flow<VendorsListDto> {
        return warehouseRepo.getVendorsList()
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

    private fun processVendorsList(dto: VendorsListDto) {
        if (dto.success && dto.Vendors != null && dto.Vendors.isNotEmpty()) {
            vendorsList = dto.Vendors
            val stringList = dto.Vendors.map { it.Vendor }.toMutableList()
            stringList.add(0, stringProvider.getString(R.string.select_vendor_name))
            _vendorsStringList.postValue(stringList)
        } else {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.vendors_list_empty_message))
        }
    }

    fun selectedBranch(branchName: String) {
        if (branchName.isNotEmpty() && branchName != stringProvider.getString(R.string.select_branch)) {
            resetItemsList()
            selectedBranch = branchName
            checkDetails()
        }
    }

    fun selectedVendorName(vendorName: String) {
        if (vendorName.isNotEmpty() && vendorName != stringProvider.getString(R.string.select_vendor_name)) {
            resetItemsList()
            selectedVendor = vendorName
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
            && selectedVendor.isNotEmpty() && selectedVendor != stringProvider.getString(
                R.string.select_vendor_name
            )
        ) {
            getPurchaseInvoiceNumbers(getSelectedBranchId(), getSelectedVendorId())
        }
    }

    private fun getSelectedBranchId(): Long {
        return branchesList?.filter { it.BranchCode == selectedBranch }.run {
            this?.get(0)?.BranchID
        } ?: 0
    }

    private fun getSelectedVendorId(): Long {
        return vendorsList?.filter { it.Vendor == selectedVendor }.run {
            this?.get(0)?.ID
        } ?: 0
    }

    private fun getPurchaseInvoiceNumbers(branchId: Long, vendorId: Long) {
        showProgressIndicator()
        val requestDto = PurchaseReturnInvoiceRequestDto(BranchID = branchId, VendorID = vendorId)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            returnsRepo.getPurchaseInvoiceNoList(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "getPurchaseInvoiceNoList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.PurchasesList != null && dto.PurchasesList.isNotEmpty()) {
                        purchaseInvoiceList = dto.PurchasesList
                        val stringList = dto.PurchasesList.map { it.PurchaseNumber }.toMutableList()
                        stringList.add(
                            0,
                            stringProvider.getString(R.string.select_purchase_invoice_no)
                        )
                        _pInvoiceStringList.postValue(stringList)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.purchase_invoice_list_empty_message))
                    }
                }
        }
    }

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(itemList: ArrayList<PurchaseItemsDetailsDto>) {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            val requestDto = processRequestDto(itemList)
            returnsRepo.savePurchaseItems(requestDto)
                .collect { response ->
                    Log.v("WMS EXERT", "savePurchaseItems response $response")
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

    private fun processRequestDto(itemList: ArrayList<PurchaseItemsDetailsDto>): PurchaseItemsRequestDto {
        return PurchaseItemsRequestDto(
            BranchID = getSelectedBranchId(),
            VendorID = getSelectedVendorId(),
            PurchaseID = getSelectedPInvoiceId(),
            ItemsDetails = itemList
        )
    }

    private fun checkAndEnableUpdateButton() {
        if (stockItemsList.size > 0) {
            val allReturnQtyNotEmpty = stockItemsList.any { it.userReturningQty > 0 }
            _enableUpdateButton.postValue(allReturnQtyNotEmpty)
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
            && selectedVendor.isNotEmpty() && selectedVendor != stringProvider.getString(
                R.string.select_vendor_name
            )
        ) {
            _errorBranch.postValue(true)
        } else {
            _errorBranch.postValue(false)
        }
    }

    fun getSelectedBranchIndex(): Int {
        return _branchesStringList.value?.indexOf(selectedBranch) ?: 0
    }

    fun getSelectedVendorNameIndex(): Int {
        return _vendorsStringList.value?.indexOf(selectedVendor) ?: 0
    }

    fun setPurchaseReturnItemsDetails(item: PurchaseItemsDetailsDto?) {
        item?.let { dto ->
            val itemDto = dto.copy(ItemSeqNumber = (getItemListSize() + 1))
            updateItemToList(itemDto)

            getItemList().takeIf { it.isNotEmpty() }?.let { list ->
                _itemsList.postValue(list)
            }
            checkAndEnableUpdateButton()
        }
    }

    private fun updateItemToList(item: PurchaseItemsDetailsDto) {
        val indexOfObjectToUpdate =
            stockItemsList.indexOfFirst { it.ItemID == item.ItemID && it.ItemCode == item.ItemCode }
        if (indexOfObjectToUpdate != -1) {
            // Replace the object with the updated object
            stockItemsList[indexOfObjectToUpdate] = item
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun getSelectedPurchaseInvoiceNoIndex(): Int {
        return _pInvoiceStringList.value?.indexOf(selectedPInvoiceNo) ?: 0
    }

    fun selectedPurchaseInvoiceNo(invoice: String) {
        if (invoice.isNotEmpty() && invoice != stringProvider.getString(R.string.select_purchase_invoice_no)) {
            resetItemsList()
            selectedPInvoiceNo = invoice
            getPurchaseItemsList()
        }
    }

    private fun getSelectedPInvoiceId(): Long {
        return purchaseInvoiceList?.filter { it.PurchaseNumber == selectedPInvoiceNo }.run {
            this?.get(0)?.PurchaseID
        } ?: 0
    }

    private fun getPurchaseItemsList() {
        showProgressIndicator()
        val request = PurchaseItemsListItemsRequestDto(PurchaseID = getSelectedPInvoiceId())//1)//getSelectedPInvoiceId())
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            returnsRepo.getPurchaseItemsList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getPurchaseItemsList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Items != null && dto.Items.isNotEmpty()) {
                        purchaseItemsList = dto.Items
                        stockItemsList.addAll(dto.Items)
                        _itemsList.postValue(dto.Items)
//                        _enableUpdateButton.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.purchase_returns_items_list_empty_message))
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
}