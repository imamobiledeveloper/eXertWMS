package com.exert.wms.stockAdjustment.item

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.itemStocks.api.*
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.StockAdjustmentRepository
import com.exert.wms.stockAdjustment.api.StockAdjustmentRequestDto
import com.exert.wms.stockAdjustment.api.StockItemsDetailsDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

class StockItemAdjustmentViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val stockAdjustmentRepo: StockAdjustmentRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {
    private var coroutineJob: Job? = null

    private val _enableSaveButton = MutableLiveData<Boolean>().apply { false }
    val enableSaveButton: LiveData<Boolean> = _enableSaveButton

//    private val _itemsList = MutableLiveData<List<StockItemsDetailsDto>?>()
//    val itemsList: MutableLiveData<List<StockItemsDetailsDto>?> = _itemsList

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

//    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
//    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton
//
//    private val _warehouseStringList = MutableLiveData<List<String>>()
//    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _navigateToSerialNo = MutableLiveData<Boolean>()
    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo

    private val _errorItemPartCode = MutableLiveData<Boolean>()
    val errorItemPartCode: LiveData<Boolean> = _errorItemPartCode

    private val _errorItemSerialNo = MutableLiveData<Boolean>()
    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

//    private val _errorQuantity = MutableLiveData<Boolean>()
//    val errorQuantity: LiveData<Boolean> = _errorQuantity
//
//    private val _errorWarehouse = MutableLiveData<Boolean>()
//    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

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

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _costString = MutableLiveData<String>()
    val costString: LiveData<String> = _costString

    private val _adjustmentQuantityString = MutableLiveData<String>().apply { postValue("") }
    val adjustmentQuantityString: LiveData<String> = _adjustmentQuantityString

    private val _adjustmentTotalCostString = MutableLiveData<String>().apply { postValue("") }
    val adjustmentTotalCostString: LiveData<String> = _adjustmentTotalCostString

    private val _itemsList = MutableLiveData<List<StockItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<StockItemsDetailsDto>?> = _itemsList

    private val _errorAdjustmentType = MutableLiveData<Boolean>()
    val errorAdjustmentType: LiveData<Boolean> = _errorAdjustmentType

    private var itemPartCode: String = ""
    var itemSerialNo: String = ""
    private var selectedWarehouse: String = ""
    private var selectedWarehouseID: Long? = null

    //    private var selectedLocation: String = ""
    private var quantity: Double = 0.0
    private var warehouseDto: WarehouseDto? = null

    //    private var warehousesList: List<WarehouseDto>? = null
//    private var stockItemsDetailsDto: ReconciliationItemsDetailsDto? = null
//    private var itemsDto: ItemsDto? = null
    var showCheckBoxes: Boolean = false
    var numberOfItemsToCheckOrAddValue: Double = 0.0

    //    var stockItemsList: ArrayList<StockItemsDetailsDto> = ArrayList()
//    private var stockItemsList: ArrayList<ReconciliationItemsDetailsDto> = ArrayList()

//    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
//    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()


    var stockItemsList: ArrayList<StockItemsDetailsDto> = ArrayList()

    //    var showCheckBoxes: Boolean = false
    private var adjustmentQuantity: Double = 0.0
    var cost: Double = 0.0
    var adjustmentTypeValue: String = ""
    var itemsDto: ItemsDto? = null
    var warehousesList: List<WarehouseDto>? = null
    var stockItemsDetailsDto: StockItemsDetailsDto? = null

    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedItemId: Long? = null
    var alreadySelected: Boolean = true

    fun checkItemDetailsEntered(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            _navigateToSerialNo.postValue(true)
        }
    }

    private fun validateUserDetails(
        itemPartCode: String,
        itemSerialNo: String,
        adjustmentType: String
    ): Boolean {
        return if (selectedWarehouse.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_empty_message))
            false
        } else if (adjustmentType.isEmpty()) {
            _errorAdjustmentType.postValue(true)
            false
        } else if (itemPartCode.isEmpty() && itemSerialNo.isEmpty()) {
            _errorItemPartCode.postValue(true)
            false
        } else if (itemsDto == null) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.invalid_details_message))
            false
        } else {
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
                        cost = dto.Items[0].SalesPrice
                        _costString.postValue(dto.Items[0].SalesPrice.toString())
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
        if (validateUserDetails(itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            stockItemsDetailsDto = StockItemsDetailsDto(
                WarehouseID = getWarehouseId(),
                ItemID = getItemId(),
                ItemCode = getItemCode(),
                AdjustmentType = getAdjustmentTypeIntValue(),
                AdjustmentQty = adjustmentQuantity,
                SerialItems = userSelectedSerialItemsList
            )
            _saveItemStatus.postValue(true)
        }
    }

    private fun getAdjustmentTypeIntValue() =
        if (adjustmentTypeValue == stringProvider.getString(R.string.positive)) 0 else 1

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

    fun setSelectedWarehouseDto(warehouseId: Long?, warehouse: WarehouseDto?) {
        selectedWarehouseID = warehouseId
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

    fun getItemDto1(): ItemsDto? = itemsDto


    private fun getItemList() = stockItemsList

    private fun getItemListSize() = getItemList().takeIf { it.isNotEmpty() }?.let { list ->
        list.size
    } ?: 0

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
                .collect { dto ->
                    Log.v("WMS EXERT", "saveStockAdjustmentItems response $dto")
                    hideProgressIndicator()
                }
        }
    }

    fun setSelectedSerialItemsList(dtoList: SerialItemsDtoList?) {
        dtoList?.takeIf { it.serialItemsDto != null }?.let { serialItemsList ->
            if (serialItemsList.serialItemsDto?.isNotEmpty() == true && serialItemsList.itemId!= null) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                userSelectedItemId = dtoList.itemId
                adjustmentQuantity = getAdjustmentQuantity().toDouble()
                _adjustmentQuantityString.postValue(getAdjustmentQuantity().toString())
                _adjustmentTotalCostString.postValue(getAdjustmentTotalCostInString())
                _enableSaveButton.postValue(true)
            }else if(serialItemsList.itemId== userSelectedItemId && (serialItemsList.serialItemsDto == null || serialItemsList.serialItemsDto.isEmpty()) ){
                userSelectedSerialItemsList= serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                adjustmentQuantity = getAdjustmentQuantity().toDouble()
                _adjustmentQuantityString.postValue(getAdjustmentQuantity().toString())
                _adjustmentTotalCostString.postValue(getAdjustmentTotalCostInString())
                _enableSaveButton.postValue(false)
            }
        }
    }

    private fun getAdjustmentTotalCostInString(): String {
        return (cost * userSelectedSerialItemsList.size).toString()
    }

    private fun getQuantity() =
        userSelectedSerialItemsList.size

    fun getWarehouseStockDetails1(): WarehouseStockDetails? {
        return itemsDto?.wStockDetails?.find { it.WarehouseID == getWarehouseObject()?.WarehouseID }
    }

    private fun getWarehouseObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)
        }
    }

    private fun setQuantityValue(qty: Double) {
        quantity = qty
//        reSetQuantityAndTotalCost()
//        _errorAdjustmentType.postValue(false)
    }

    private fun setCheckBoxState1(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    fun getCheckBoxStateValue1() = showCheckBoxes

    fun setWarehouseAndItemDetails(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?,
        qty: String
    ) {
        if (itemsDto != null && itemsDto.Stock > 0 && qty.isNotEmpty()) {
            val qtyValue = qty.toDoubleOrNull() ?: 0.0
            setQuantityValue(qtyValue)
            val difference = qtyValue - itemsDto.Stock
            setNumberOfItemsToCheckOrAdd(difference)
            if (difference > 0) {// quantity - system quanty = +ve number) then add those many serialized numbers
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
        numberOfItemsToCheckOrAddValue = difference
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
            val dto = SerialItemsDtoList(userCheckedItems)
            _checkedSerialItemsList.postValue(dto)
        } else {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.check_serial_items_empty_message))
        }
    }

    fun setCheckedItems1(checkedItems: ArrayList<SerialItemsDto>) {
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
        if (numberOfItemsToCheckOrAddValue > 0 && userCheckedItems.size < numberOfItemsToCheckOrAddValue) {
            _showDialogToAddItem.postValue(true)
        } else {
            _showDialogToAddItem.postValue(false)
        }
    }

    fun checkNoOfItemsToSelect(): Boolean {
        return numberOfItemsToCheckOrAddValue < 0 && userCheckedItems.size < abs(
            numberOfItemsToCheckOrAddValue
        )
    }


    ///////////

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

    fun getWarehouseStockDetailsOld(): WarehouseStockDetails? {
        return itemsDto?.wStockDetails?.find { it.WarehouseID == getWarehouseObject()?.WarehouseID }
    }

    fun getWarehouseStockDetails(): WarehouseStockDetails? {
        return itemsDto?.wStockDetails?.find { it.WarehouseID == selectedWarehouseID }
    }

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    private fun getAdjustmentQuantity() =
        userSelectedSerialItemsList.size

    fun getCheckBoxStateValue() = showCheckBoxes

    fun setWarehouseAndItemDetails(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?,
        adjustmentType: String,
        serialItemsList: SerialItemsDtoList?
    ) {
        if (adjustmentType.isNotEmpty()) {
            setAdjustmentType(adjustmentType)
            if (adjustmentType == stringProvider.getString(R.string.positive)) {// positive adjustment
                setCheckBoxState(false)
                _showAddItemButton.value = true
            } else {//negative type-get serial numbers list
                setCheckBoxState(true)
                _showAddItemButton.value = false
                getSerialNumbersList(itemsDto, warehouseStockDetails, serialItemsList)
            }
        }
    }

    private fun getSerialNumbersList(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?,
        serialItemsList: SerialItemsDtoList?
    ) {
        if (itemsDto != null) {
            if (warehouseStockDetails?.wSerialItemDetails != null && warehouseStockDetails.wSerialItemDetails.isNotEmpty()) {
                checkIsListHavingAnySelectedObjects(
                    itemsDto.ItemID,
                    warehouseStockDetails.wSerialItemDetails,
                    serialItemsList
                )
//                _warehouseSerialNosList.postValue(warehouseStockDetails.wSerialItemDetails)
            } else if (warehouseStockDetails != null) {
                if ((itemsDto.ItemPartCode != null || itemsDto.ItemSerialNumber != null) && warehouseStockDetails.WarehouseID > 0) {
                    getWarehouseSerialNosList(
                        itemsDto.ItemPartCode!!,
                        itemsDto.ItemSerialNumber!!,
                        warehouseStockDetails.WarehouseID
                    )
                } else {
                    _errorFieldMessage.postValue(stringProvider.getString(R.string.error_warehouse_empty_message))
                }
            } else { //
                _errorFieldMessage.postValue(stringProvider.getString(R.string.invalid_details_message))
            }

        }
    }

    private fun checkIsListHavingAnySelectedObjects(
        itemId: Long,
        wSerialItemDetails: List<WarehouseSerialItemDetails>,
        serialItemsList: SerialItemsDtoList?
    ) {
        val alreadySelectedList = getSavedItemDto()
        if (alreadySelectedList != null && alreadySelectedList.ItemCode == itemId.toString() && alreadySelectedList.SerialItems.isNotEmpty()) {
            wSerialItemDetails.forEach { warehouse ->
                alreadySelectedList.SerialItems.find { it.SerialNumber == warehouse.SerialNumber }
                    .run {
                        warehouse.selected = true
                        _enableSaveButton.postValue(true)
                    }
            }
        } else {
            serialItemsList?.takeIf { it.itemId == itemId && it.serialItemsDto != null && it.serialItemsDto.isNotEmpty() }
                ?.let { list ->
                    wSerialItemDetails.forEach { warehouse ->
                        list.serialItemsDto?.find { it.SerialNumber == warehouse.SerialNumber }
                            .run {
                                warehouse.selected = true
                                alreadySelected = true
                                _enableSaveButton.postValue(true)
                            }
                    }
                }
//            if (serialItemsList?.serialItemsDto != null) {
//                wSerialItemDetails.forEach { warehouse ->
//                    userSelectedSerialItemsList.find { it.SerialNumber == warehouse.SerialNumber }
//                        .run {
//                            warehouse.selected = true
//                        }
//                }
//            }

        }
        _warehouseSerialNosList.postValue(wSerialItemDetails)
    }

    fun getSelectedItems(itemID: Long) {
//        if (userCheckedItems.isNotEmpty()) {
            val dto = SerialItemsDtoList(userCheckedItems, itemId = itemID)
            _checkedSerialItemsList.postValue(dto)
//        } else {
//            _errorFieldMessage.postValue(stringProvider.getString(R.string.check_serial_items_empty_message))
//        }
    }

    fun setCheckedItems(checkedItems: ArrayList<SerialItemsDto>) {
        userCheckedItems = checkedItems
        if (userCheckedItems.isNotEmpty()) {
            if (getAdjustmentType() == stringProvider.getString(R.string.positive)) {
                setConvertedWarehouseSerialNoList()
            }
            _enableSaveButton.postValue(true)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    fun setAdjustmentType(adjustment: String) {
        adjustmentTypeValue = adjustment
        reSetQuantityAndTotalCost()
        _errorAdjustmentType.postValue(false)
    }

    private fun reSetQuantityAndTotalCost() {
        _adjustmentQuantityString.value = ""
        _adjustmentTotalCostString.value = ""
        _enableSaveButton.postValue(false)
    }

    fun getAdjustmentType() = adjustmentTypeValue

    fun getUserSelectedSerialItemsList(): SerialItemsDtoList =
        SerialItemsDtoList(userSelectedSerialItemsList, userSelectedItemId)

    fun clearPreviousSearchedListItems() {
        userSelectedSerialItemsList.clear()
        userSelectedItemId = null
    }

    fun checkAndEnableSaveButton() {
        _enableSaveButton.postValue(alreadySelected)
    }
}