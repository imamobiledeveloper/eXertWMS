package com.exert.wms.delivery.deliveryReceipt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.delivery.api.*
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

    private val _vendorsStringList = MutableLiveData<List<String>>()
    val vendorsStringList: LiveData<List<String>> = _vendorsStringList

    private val _salesOrdersStringList = MutableLiveData<List<String>>()
    val salesOrdersStringList: LiveData<List<String>> = _salesOrdersStringList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private var selectedToWarehouse: String = ""
    var stockItemsList: ArrayList<DeliveryReceiptItemsDetailsDto> = ArrayList()
    var itemsDto: ItemsDto? = null
    var warehousesList: List<WarehouseDto>? = null

    private var selectedVendor: String = ""
    private var selectedPurchaseOrderNo: String = ""
    private var salesOrdersList: List<PurchaseOrdersDto>? = null

    var vendorsList: List<VendorDto>? = null
    var deliveryReceiptItemsList: List<DeliveryReceiptItemsDetailsDto>? = null

    init {
        getWarehousesAndVendorsList()
    }

    private fun getWarehousesAndVendorsList() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + CoroutineExceptionHandler { _, _ ->
            hideProgressIndicator()
        }) {
            combine(
                getWarehousesList(),
                getVendorsList()
            ) { wList, cList ->
                Pair(wList, cList)
            }.collect { result ->
                hideProgressIndicator()
                Log.v("WMS EXERT", "getWarehousesAndVendorsList response $result")
                hideProgressIndicator()
                processWarehousesList(result.first)
                processVendorsList(result.second)

            }
        }
    }

    private fun getWarehousesList(): Flow<WarehouseListDto> {
        return warehouseRepo.getWarehouseList()
    }

    private fun getVendorsList(): Flow<VendorsListDto> {
        return warehouseRepo.getVendorsList()
    }

    fun selectedVendorName(vendorName: String) {
        if (vendorName != stringProvider.getString(R.string.select_vendor_name)) {
            resetItemsList()
            selectedVendor = vendorName
            checkDetails()
        }
    }

    private fun processWarehousesList(dto: WarehouseListDto) {
        if (dto.success && dto.Warehouses != null && dto.Warehouses.isNotEmpty()) {
            warehousesList = dto.Warehouses
            val stringList = dto.Warehouses.map { it.Warehouse }.toMutableList()
            stringList.add(0, stringProvider.getString(R.string.select_warehouse))
            _warehouseStringList.postValue(stringList)
        } else {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_list_empty_message))
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

    fun selectedToWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select_to_warehouse)) {
            resetItemsList()
            selectedToWarehouse = warehouseName
            checkDetails()
        }
    }

    private fun resetItemsList() {
        stockItemsList.clear()
        deliveryReceiptItemsList = null
        _itemsList.postValue(null)
        _enableUpdateButton.postValue(false)
    }

    fun saveItems() {
        getItemList().takeIf { it.isNotEmpty() }?.let { list ->
            updatedItems(list)
        }
    }

    private fun updatedItems(list: ArrayList<DeliveryReceiptItemsDetailsDto>) {
        showProgressIndicator()
        val requestDto = processRequest(list)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
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

    private fun processRequest(list: ArrayList<DeliveryReceiptItemsDetailsDto>): DeliveryReceiptItemsRequestDto {
        val itemsDetailsList: MutableList<DeliveryReceiptItemsDetailsRequestDto> = mutableListOf()
        list.map { dto ->
            itemsDetailsList.add(
                DeliveryReceiptItemsDetailsRequestDto(
                    ItemSeqNumber = itemsDetailsList.size + 1,
                    ItemID = dto.ItemID,
                    ItemCode = dto.ItemCode,
                    UnitID = dto.UnitID,
                    Quantity = dto.Quantity,
                    OrderedQty = dto.OrderedQty,
                    LCYPrice = dto.LCYPrice,
                    Price = dto.Price,
                    DiscountAmount = dto.DiscountAmount,
                    DiscountPercentage = dto.DiscountPercentage,
                    Factor = dto.Factor,
                    PurchaseOrderID = dto.PurchaseOrderID,
                    PurchaseOrderItemID = dto.PurchaseOrderItemID,
                    ItemDiscountPercentage = dto.ItemDiscountPercentage,
                    ItemDiscount = dto.ItemDiscount,
                    VendorDiscountPercentage = dto.VendorDiscountPercentage,
                    VendorDiscount = dto.VendorDiscount,
                    TrackingTypes = 14,
                    SerialItems = dto.SerialItems ?: emptyList(),
                )
            )
        }
        return DeliveryReceiptItemsRequestDto(
            BranchID = getSelectedToWarehouseId(),
            VendorID = getSelectedVendorId(),
            PurchaseOrderIDs = listOf(PurchaseOrderIDDto(PurchaseOrderID = getSelectedPurchaseOrdersId())),
            ItemsDetails = itemsDetailsList
        )
    }

    private fun checkAndEnableUpdateButton() {
        if (stockItemsList.size > 0) {
            _enableUpdateButton.postValue(true)
        } else {
            _enableUpdateButton.postValue(false)
        }
    }

    private fun updateItemToList(item: DeliveryReceiptItemsDetailsDto) {
        val indexOfObjectToUpdate =
            stockItemsList.indexOfFirst { it.ItemID == item.ItemID && it.ItemCode == item.ItemCode }
        if (indexOfObjectToUpdate != -1) {
            // Replace the object with the updated object
            stockItemsList[indexOfObjectToUpdate] = item
        }
    }

    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

    private fun checkDetails() {
        if (selectedToWarehouse.isNotEmpty() && selectedToWarehouse != stringProvider.getString(
                R.string.select_warehouse
            )
            && selectedVendor.isNotEmpty() && selectedVendor != stringProvider.getString(
                R.string.select_vendor_name
            )
        ) {
            getPurchaseOrderNumbers(getSelectedToWarehouseId(), getSelectedVendorId())
        }
    }

    private fun getPurchaseOrderNumbers(warehouseId: Long, vendorId: Long) {
        showProgressIndicator()
        val requestDto = SalesOrdersRequestDto(BranchID = warehouseId, VendorID = vendorId)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            deliveryRepo.getPurchaseOrdersList(requestDto)
                .collect { dto ->
                    Log.v("WMS EXERT", "getPurchaseOrdersList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.PurchaseOrders != null && dto.PurchaseOrders.isNotEmpty()) {
                        salesOrdersList = dto.PurchaseOrders
                        val stringList =
                            dto.PurchaseOrders.map { it.PurchaseOrderNumber }.toMutableList()
                        stringList.add(
                            0,
                            stringProvider.getString(R.string.select_purchase_order_no)
                        )
                        _salesOrdersStringList.postValue(stringList)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.purchase_orders_list_empty_message))
                    }
                }
        }
    }

    fun getSelectedToWarehouseIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedToWarehouse) ?: 0
    }

    fun setDeliveryReceiptItemsDetailsDto(item: DeliveryReceiptItemsDetailsDto?) {
        item?.let { dto ->
            val itemDto = dto.copy(ItemSeqNumber = (getItemListSize() + 1))
            updateItemToList(itemDto)

            getItemList().takeIf { it.isNotEmpty() }?.let { list ->
                _itemsList.postValue(list)
            }
            checkAndEnableUpdateButton()
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun getSelectedVendorNameIndex(): Int {
        return _vendorsStringList.value?.indexOf(selectedVendor) ?: 0
    }

    private fun getSelectedPurchaseOrdersId(): Long {
        return salesOrdersList?.filter { it.PurchaseOrderNumber == selectedPurchaseOrderNo }.run {
            this?.get(0)?.PurchaseOrderID
        } ?: 0
    }

    private fun getSelectedVendorId(): Long {
        return vendorsList?.filter { it.Vendor == selectedVendor }.run {
            this?.get(0)?.ID
        } ?: 0
    }

    private fun getSelectedToWarehouseId(): Long {
        return warehousesList?.filter { it.Warehouse == selectedToWarehouse }.run {
            this?.get(0)?.WarehouseID
        } ?: 0
    }

    fun selectedPurchaseOrder(pOrderNo: String) {
        if (pOrderNo != stringProvider.getString(R.string.select_purchase_order_no)) {
            resetItemsList()
            selectedPurchaseOrderNo = pOrderNo
            getDeliveryReceiptItemsList()
        }
    }

    fun getSelectedPurchaseOrderIndex(): Int {
        return _salesOrdersStringList.value?.indexOf(selectedPurchaseOrderNo) ?: 0
    }

    private fun getDeliveryReceiptItemsList() {
        showProgressIndicator()
        val request = DeliveryReceiptItemsListWithOutItemsRequestDto(
            BranchID = getSelectedToWarehouseId(),
            VendorID = getSelectedVendorId(),
            PurchaseOrderIDs = listOf(PurchaseOrderIDDto(PurchaseOrderID = getSelectedPurchaseOrdersId()))
        )
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            deliveryRepo.getDeliveryReceiptItemsList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getDeliveryReceiptItemsList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Items != null && dto.Items.isNotEmpty()) {
                        deliveryReceiptItemsList = dto.Items
                        stockItemsList.addAll(dto.Items)
                        _itemsList.postValue(dto.Items)
                        _enableUpdateButton.postValue(true)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.delivery_receipt_items_list_empty_message))
                    }
                }
        }
    }


//    private fun getSelectedBranchId(): Long {
//        return warehousesList?.filter { it.Warehouse == selectedToWarehouse }.run {
//            this?.get(0)?.WarehouseID
//        } ?: 0
//    }
//
//    private fun getSelectedVendorId(): Long {
//        return vendorsList?.filter { it.Vendor == selectedVendor }.run {
//            this?.get(0)?.ID
//        } ?: 0
//    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        _errorFieldMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_api_access_message
            )
        )
    }
}