package com.exert.wms.stockReconciliation.item

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.itemStocks.api.*
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockReconciliation.api.ReconciliationItemsDetailsDto
import com.exert.wms.stockReconciliation.api.StockReconciliationRepository
import com.exert.wms.stockReconciliation.api.StockReconciliationRequestDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

class StockReconciliationItemViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val stockReconciliationRepo: StockReconciliationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _enableSaveButton = MutableLiveData<Boolean>().apply { false }
    val enableSaveButton: LiveData<Boolean> = _enableSaveButton

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _navigateToSerialNo = MutableLiveData<Boolean>()
    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo

    private val _errorItemPartCode = MutableLiveData<Boolean>()
    val errorItemPartCode: LiveData<Boolean> = _errorItemPartCode

    private val _errorItemSerialNo = MutableLiveData<Boolean>()
    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

    private val _errorQuantity = MutableLiveData<Boolean>()
    val errorQuantity: LiveData<Boolean> = _errorQuantity

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    private val _warehouseSerialNosList = MutableLiveData<List<WarehouseSerialItemDetails>?>()
    val warehouseSerialNosList: LiveData<List<WarehouseSerialItemDetails>?> =
        _warehouseSerialNosList

    private val _isItemSerialized = MutableLiveData<Boolean>()
    val isItemSerialized: LiveData<Boolean> = _isItemSerialized

    private val _quantityString = MutableLiveData<String>().apply { postValue("") }
    val quantityString: LiveData<String> = _quantityString

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _showAddItemButton = MutableLiveData<Boolean>()
    val showAddItemButton: LiveData<Boolean> = _showAddItemButton

    private val _checkedSerialItemsList = MutableLiveData<SerialItemsDtoList>()
    val checkedSerialItemsList: LiveData<SerialItemsDtoList> = _checkedSerialItemsList

    private val _showDialogToAddItem = MutableLiveData<Boolean>()
    val showDialogToAddItem: LiveData<Boolean> = _showDialogToAddItem

    private val _itemBarCodeData = MutableLiveData<ItemsBarCodeDto>()
    val itemBarCodeData: LiveData<ItemsBarCodeDto> = _itemBarCodeData

    private var itemPartCode: String = ""
    var itemSerialNo: String = ""
    private var selectedWarehouse: String = ""
    private var selectedLocation: String = ""
    private var quantity: Double = 0.0
    private var warehouseDto: WarehouseDto? = null
    private var warehousesList: List<WarehouseDto>? = null
    private var stockItemsDetailsDto: ReconciliationItemsDetailsDto? = null
    private var itemsDto: ItemsDto? = null
    var showCheckBoxes: Boolean = false
    var numberOfItemsToCheckOrAddValue: Double = 0.0

    private var stockItemsList: ArrayList<ReconciliationItemsDetailsDto> = ArrayList()
    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()

    fun checkItemDetailsEntered(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo)) {
            _navigateToSerialNo.postValue(true)
        }
    }

    private fun validateUserDetails(
        itemPartCode: String,
        itemSerialNo: String
    ): Boolean {
        return if (selectedWarehouse.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_empty_message))
            false
        } else if (selectedLocation.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.location_empty_message))
            false
        } else if (itemPartCode.isEmpty() && itemSerialNo.isEmpty()) {
            _errorItemPartCode.postValue(true)
            false
        } else if (quantity < 0) {
            _errorQuantity.postValue(true)
            false
        } else if (itemsDto == null) {
            _errorQuantity.postValue(false)
            _errorFieldMessage.postValue(stringProvider.getString(R.string.invalid_details_message))
            false
        } else {
            _errorQuantity.postValue(true)
            _errorItemSelectionMessage.postValue(true)
            true
        }
    }

    fun setItemPartCodeValue(partCode: String) {
        itemPartCode = partCode
    }

    fun searchItemWithPartCode() {
        if (itemPartCode.isNotEmpty() && warehouseDto != null) {
            getWarehouseSerialNosList(itemPartCode, "", warehouseDto!!.WarehouseID)
            _errorItemPartCode.postValue(false)
        } else {
            _errorItemPartCode.postValue(true)
        }
    }

    private fun getWarehouseSerialNosList(
        itemPartCode: String,
        itemSerialNo: String,
        warehouseId: Long
    ) {
        showProgressIndicator()
        val request = WarehouseSerialItemsRequestDto(
            ItemPartCode = itemPartCode,
            WarehouseID = warehouseId,
            ItemSerialNumber = itemSerialNo
        )
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getWarehouseSerialNosList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "Stock Adjustment: getWarehouseSerialNosList response $dto")
                    hideProgressIndicator()

                    if (dto.success && dto.Items != null && dto.Items.isNotEmpty()) {
                        itemsDto = dto.Items[0]
                        _itemDto.postValue(dto.Items[0])
                        _isItemSerialized.postValue(dto.Items[0].IsSerialItem == 1)

                        val warehouseList = dto.Items[0].wStockDetails
                        if (warehouseList != null && warehouseList.isNotEmpty() && warehouseList[0].wSerialItemDetails != null) {
                            warehouseList[0].wSerialItemDetails?.let {
                                _warehouseSerialNosList.postValue(it)
                            } ?: _errorGetItemsStatusMessage.postValue(
                                stringProvider.getString(
                                    R.string.error_warehouse_serials_nos_message
                                )
                            )

                        } else {
                            _errorGetItemsStatusMessage.postValue(
                                stringProvider.getString(
                                    R.string.error_warehouse_serials_nos_message
                                )
                            )
                        }
                    } else {
                        _errorGetItemsStatusMessage.postValue(
                            stringProvider.getString(
                                R.string.error_failed_warehouse_serials_nos_message
                            )
                        )
                    }
                }
        }

    }

    fun saveItemStock(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo)) {
            stockItemsDetailsDto = ReconciliationItemsDetailsDto(
                WarehouseID = getWarehouseId(),
                ItemID = getItemId(),
                ItemCode = getItemCode(),
                location = selectedLocation,
                quantity = quantity,
                SerialItems = userSelectedSerialItemsList
            )
            _saveItemStatus.postValue(true)
        }
    }

    private fun getWarehouseId() = run {
        warehouseDto?.let { it.WarehouseID } ?: 0
    }

    private fun getItemId() = run {
        itemsDto?.let { it.ItemID } ?: 0
    }

    private fun getItemCode() = run {
        itemsDto?.let { it.ItemCode } ?: ""
    }

    fun getSavedItemDto() = stockItemsDetailsDto

    fun setSelectedWarehouseDto(warehouse: WarehouseDto?) {
        warehouse?.let {
            warehouseDto = it
            selectedWarehouse = it.Warehouse
        }
    }

    fun setItemSerialNumberValue(serialNo: String) {
        itemSerialNo = serialNo
    }

    fun searchItemWithSerialNumber() {
        if (itemSerialNo.isNotEmpty() && warehouseDto != null) {
            getWarehouseSerialNosList("", itemSerialNo, warehouseDto!!.WarehouseID)
            _errorItemSerialNo.postValue(false)
        } else {
            _errorItemSerialNo.postValue(true)
        }
    }

    fun getItemDto(): ItemsDto? = itemsDto


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
                .collect { dto ->
                    Log.v("WMS EXERT", "saveStockReconciliationItems response $dto")
                    hideProgressIndicator()

                }
        }
    }

    fun setSelectedSerialItemsList(dto: SerialItemsDtoList?) {
        dto?.takeIf { it.serialItemsDto!= null }?.let{ serialItemsList->
            if (serialItemsList.serialItemsDto?.isNotEmpty() == true) {
                userSelectedSerialItemsList = serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                quantity = getQuantity().toDouble()
                _quantityString.postValue(getQuantity().toString())
                _enableSaveButton.postValue(true)
            }
        }

    }

    private fun getQuantity() =
        userSelectedSerialItemsList.size

    fun getWarehouseStockDetails(): WarehouseStockDetails? {
        return itemsDto?.wStockDetails?.find { it.WarehouseID == getWarehouseObject()?.WarehouseID }
    }

    fun getWarehouseObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)
        }
    }

    private fun setQuantityValue(qty: Double) {
        quantity = qty
//        reSetQuantityAndTotalCost()
//        _errorAdjustmentType.postValue(false)
    }

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    fun getCheckBoxStateValue() = showCheckBoxes

    fun setWarehouseAndItemDetails(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?,
        qty: String
    ) {
        if (itemsDto != null && itemsDto.Stock > 0 && qty.isNotEmpty() ) {
            val qtyValue=qty.toDoubleOrNull()?: 0.0
            setQuantityValue(qtyValue)
            val difference= qtyValue-itemsDto.Stock
            setNumberOfItemsToCheckOrAdd(difference)
            if (difference >0) {// quantity - system quanty = +ve number) then add those many serialized numbers
                setCheckBoxState(false)
                _showAddItemButton.value = true
            } else {//negative type-get serial numbers list
                setCheckBoxState(true)
                _showAddItemButton.value = false
                getSerialNumbersList(itemsDto, warehouseStockDetails)
            }
        }
    }

    private fun setNumberOfItemsToCheckOrAdd(difference: Double) {
        numberOfItemsToCheckOrAddValue=difference
    }

    private fun getSerialNumbersList(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?
    ) {
        if (itemsDto != null && warehouseStockDetails != null) {
            if (warehouseStockDetails.wSerialItemDetails != null && warehouseStockDetails.wSerialItemDetails.isNotEmpty()) {
                _warehouseSerialNosList.postValue(warehouseStockDetails.wSerialItemDetails)
            } else if ((itemsDto.ItemPartCode != null || itemsDto.ItemSerialNumber != null) && warehouseStockDetails.WarehouseID > 0) {
                getWarehouseSerialNosList(
                    itemsDto.ItemPartCode!!,
                    itemsDto.ItemSerialNumber!!,
                    warehouseStockDetails.WarehouseID
                )
            } else {
                _errorFieldMessage.postValue(stringProvider.getString(R.string.invalid_details_message))
            }

        }
    }

    fun getSelectedItems() {
        if (userCheckedItems.isNotEmpty()) {
            val dto= SerialItemsDtoList(userCheckedItems)
            _checkedSerialItemsList.postValue(dto)
        } else {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.check_serial_items_empty_message))
        }
    }

    fun setCheckedItems(checkedItems: ArrayList<SerialItemsDto>) {
        userCheckedItems = checkedItems
        if (userCheckedItems.isNotEmpty()) {
            if (numberOfItemsToCheckOrAddValue > 0) {
                setConvertedWarehouseSerialNoList()
            }
            _enableSaveButton.postValue(true)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    private fun setConvertedWarehouseSerialNoList() {
        val newList: List<WarehouseSerialItemDetails>? =
            userCheckedItems.takeIf { it.isNotEmpty() }?.let { list ->
                list.map { dto ->
                    WarehouseSerialItemDetails(
                        SerialNumber = dto.SerialNumber,
                        MFGDate = dto.ManufactureDate,
                        WarentyDays = dto.WarrantyPeriod,
                        selected = true
                    )
                }
            }

        newList?.let {
            _warehouseSerialNosList.postValue(it)
        }
    }
    fun checkAddedItemsCount() {
        if(numberOfItemsToCheckOrAddValue > 0 && userCheckedItems.size < numberOfItemsToCheckOrAddValue){
            _showDialogToAddItem.postValue(true)
        }else{
            _showDialogToAddItem.postValue(false)
        }
    }

    fun checkNoOfItemsToSelect(): Boolean {
        return numberOfItemsToCheckOrAddValue < 0 && userCheckedItems.size < abs(numberOfItemsToCheckOrAddValue)
    }

    fun setBarCodeData(barCode: String) {
        if(isItPartCodeScanRequest()){
            setItemPartCodeValue(barCode)
            val itemBarCodeDto=ItemsBarCodeDto(isItItemPartCode = true, ItemPartCodeData=barCode, ItemSerialNoData="")
            _itemBarCodeData.postValue(itemBarCodeDto)
            searchItemWithPartCode()
        }else{
            setItemSerialNumberValue(barCode)
            val itemBarCodeDto=ItemsBarCodeDto(isItItemPartCode = false, ItemPartCodeData="", ItemSerialNoData=barCode)
            _itemBarCodeData.postValue(itemBarCodeDto)
            searchItemWithSerialNumber()
        }
    }
    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }
}