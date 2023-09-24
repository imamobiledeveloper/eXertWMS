package com.exert.wms.transfer.transferOut.item

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.itemStocks.api.*
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.StockItemsDetailsDto
import com.exert.wms.transfer.api.TransferOutItemDetailsDto
import com.exert.wms.utils.StringProvider
import com.exert.wms.warehouse.WarehouseDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TransferOutItemViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
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

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    private val _warehouseSerialNosList = MutableLiveData<List<WarehouseSerialItemDetails>?>()
    val warehouseSerialNosList: LiveData<List<WarehouseSerialItemDetails>?> =
        _warehouseSerialNosList

    private val _isItemSerialized = MutableLiveData<Boolean>()
    val isItemSerialized: LiveData<Boolean> = _isItemSerialized

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _showAddItemButton = MutableLiveData<Boolean>()
    val showAddItemButton: LiveData<Boolean> = _showAddItemButton

    private val _checkedSerialItemsList = MutableLiveData<SerialItemsDtoList>()
    val checkedSerialItemsList: LiveData<SerialItemsDtoList> = _checkedSerialItemsList

    private val _quantityString = MutableLiveData<String>().apply { postValue("") }
    val quantityString: LiveData<String> = _quantityString

    private val _itemsList = MutableLiveData<List<StockItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<StockItemsDetailsDto>?> = _itemsList

    private val _errorQuantityType = MutableLiveData<Boolean>()
    val errorQuantityType: LiveData<Boolean> = _errorQuantityType

    private val _itemBarCodeData = MutableLiveData<ItemsBarCodeDto>()
    val itemBarCodeData: LiveData<ItemsBarCodeDto> = _itemBarCodeData

    private val _getItemsStatus = MutableLiveData<Boolean>()
    val getItemsStatus: LiveData<Boolean> = _getItemsStatus

    private var itemPartCode: String = ""
    var itemSerialNo: String = ""
    var quantity: Int = 0
    private var selectedWarehouse: String = ""

    private var selectedFromWarehouseID: Long? = null

    private var warehouseDto: WarehouseDto? = null
    private var showCheckBoxes: Boolean = false

    var stockItemsList: ArrayList<StockItemsDetailsDto> = ArrayList()
    var itemsDto: ItemsDto? = null
    var transferOutItemDetailsDto: TransferOutItemDetailsDto? = null

    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedItemId: Long? = null
    var alreadySelected: Boolean = true
    var isItemSerialize: Boolean = false

    fun checkItemDetailsEntered(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo, false)) {
            _navigateToSerialNo.postValue(true)
        }
    }

    private fun validateUserDetails(
        itemPartCode: String, itemSerialNo: String,
        checkQuantity: Boolean = false
    ): Boolean {
        return if (selectedWarehouse.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_empty_message))
            false
        } else if (checkQuantity && quantity <= 0) {
            _errorQuantityType.postValue(true)
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
        itemPartCode: String, itemSerialNo: String, warehouseId: Long
    ) {
        showProgressIndicator()
        val request = WarehouseSerialItemsRequestDto(
            ItemPartCode = itemPartCode, WarehouseID = warehouseId, ItemSerialNumber = itemSerialNo
        )
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getWarehouseSerialNosList(request).collect { dto ->
                Log.v("WMS EXERT", "Stock Adjustment: getWarehouseSerialNosList response $dto")
                hideProgressIndicator()

                if (dto.success) {
                    if (dto.Items != null && dto.Items.isNotEmpty()) {
                        itemsDto = dto.Items[0]
                        _itemDto.postValue(dto.Items[0])
                        isItemSerialize = dto.Items[0].IsSerialItem == 1
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
                        _errorGetItemsStatusMessage.postValue(stringProvider.getString(R.string.empty_items_list))
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
        if (validateUserDetails(itemPartCode, itemSerialNo, true)) {
            transferOutItemDetailsDto = TransferOutItemDetailsDto(
                ItemSeqNumber = 0,
                ItemID = getItemId(),
                ItemCode = getItemCode(),
                Quantity = quantity,
                SerialItems = userSelectedSerialItemsList,
                displayName = getItemDisplayName()
            )
            _saveItemStatus.postValue(true)
        }
    }

    private fun getItemId() = run {
        itemsDto?.let { it.ItemID } ?: 0
    }

    private fun getItemCode() = run {
        itemsDto?.let { it.ItemCode } ?: ""
    }

    private fun getItemDisplayName() = run {
        itemsDto?.let { it.getItemListName() } ?: ""
    }

    fun getSavedItemDto() = transferOutItemDetailsDto

    fun setSelectedWarehouseDto(
        warehouseId: Long?, fromWarehouseDto: WarehouseDto?
    ) {
        selectedFromWarehouseID = warehouseId
        fromWarehouseDto?.let {
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

    fun setSelectedSerialItemsList(dtoList: SerialItemsDtoList?) {
        dtoList?.takeIf { it.serialItemsDto != null }?.let { serialItemsList ->
            if (serialItemsList.serialItemsDto?.isNotEmpty() == true && serialItemsList.itemId != null) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                userSelectedItemId = dtoList.itemId
                quantity = getAdjustmentQuantity()
               _quantityString.postValue(getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(true)
            } else if (serialItemsList.itemId == userSelectedItemId && (serialItemsList.serialItemsDto == null || serialItemsList.serialItemsDto.isEmpty())) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                quantity = getAdjustmentQuantity()
               _quantityString.postValue(getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(false)
            }
        }
    }

    fun getItemDto(): ItemsDto? = itemsDto

    fun getWarehouseStockDetails(): WarehouseStockDetails? {
        return itemsDto?.wStockDetails?.find { it.WarehouseID == selectedFromWarehouseID }
    }

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    private fun getAdjustmentQuantity() = userSelectedSerialItemsList.size

    fun getCheckBoxStateValue() = showCheckBoxes

    fun setWarehouseAndItemDetails(
        itemsDto: ItemsDto?, warehouseStockDetails: WarehouseStockDetails?,
        serialItemsList: SerialItemsDtoList?
    ) {
        setCheckBoxState(true)
        _showAddItemButton.value = false
        getSerialNumbersList(itemsDto, warehouseStockDetails, serialItemsList)
    }

    private fun getSerialNumbersList(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?,
        serialItemsList: SerialItemsDtoList?
    ) {
        if (itemsDto != null) {
            if (warehouseStockDetails?.wSerialItemDetails != null && warehouseStockDetails.wSerialItemDetails.isNotEmpty()) {
                checkIsListHavingAnySelectedObjects(
                    itemsDto.ItemID, warehouseStockDetails.wSerialItemDetails, serialItemsList
                )
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
            } else {
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
                            ?.let {
                                warehouse.selected = true
                                alreadySelected = true
                                _enableSaveButton.postValue(true)
                            }
                    }
                }

        }
        _warehouseSerialNosList.postValue(wSerialItemDetails)
    }

    fun getSelectedItems(itemID: Long) {
        val dto = SerialItemsDtoList(userCheckedItems, itemId = itemID)
        _checkedSerialItemsList.postValue(dto)
    }

    fun setCheckedItems(checkedItems: ArrayList<SerialItemsDto>) {
        userCheckedItems = checkedItems
        if (userCheckedItems.isNotEmpty()) {
            _enableSaveButton.postValue(true)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    fun getUserSelectedSerialItemsList(): SerialItemsDtoList =
        SerialItemsDtoList(userSelectedSerialItemsList, userSelectedItemId)

    fun clearPreviousSearchedListItems() {
        userSelectedSerialItemsList.clear()
        userSelectedItemId = null
    }

    fun checkAndEnableSaveButton() {
        _enableSaveButton.postValue(alreadySelected)
    }

    fun setBarCodeData(barCode: String) {
        if (isItPartCodeScanRequest()) {
            setItemPartCodeValue(barCode)
            val itemBarCodeDto = ItemsBarCodeDto(
                isItItemPartCode = true, ItemPartCodeData = barCode, ItemSerialNoData = ""
            )
            _itemBarCodeData.postValue(itemBarCodeDto)
            searchItemWithPartCode()
        } else {
            setItemSerialNumberValue(barCode)
            val itemBarCodeDto = ItemsBarCodeDto(
                isItItemPartCode = false, ItemPartCodeData = "", ItemSerialNoData = barCode
            )
            _itemBarCodeData.postValue(itemBarCodeDto)
            searchItemWithSerialNumber()
        }
    }

    fun setQuantity(text: String) {
        if (!isItemSerialize && text.isNotEmpty()) {
            quantity = text.toInt()
            _enableSaveButton.postValue(text.toInt() > 0)
        } else if (!isItemSerialize && text.isEmpty()) {
            _enableSaveButton.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }
}