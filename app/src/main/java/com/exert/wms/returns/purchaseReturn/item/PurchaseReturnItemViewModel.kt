package com.exert.wms.returns.purchaseReturn.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.returns.api.PurchaseItemsDetailsDto
import com.exert.wms.stockAdjustment.api.StockItemsDetailsDto
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class PurchaseReturnItemViewModel(
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

    private val _returningQuantityString = MutableLiveData<String>()//.apply { postValue("") }
    val returningQuantityString: LiveData<String> = _returningQuantityString

    private val _itemsList = MutableLiveData<List<StockItemsDetailsDto>?>()
    val itemsList: MutableLiveData<List<StockItemsDetailsDto>?> = _itemsList

    private val _errorReturningQty = MutableLiveData<Boolean>()
    val errorReturningQty: LiveData<Boolean> = _errorReturningQty

    private val _convertedItemsDto = MutableLiveData<ItemsDto>()
    val convertedItemsDto: LiveData<ItemsDto> = _convertedItemsDto

    private var selectedItemDtoInSerialNoScreen: PurchaseItemsDetailsDto? = null
    private var showCheckBoxes: Boolean = false

    var stockItemsList: ArrayList<StockItemsDetailsDto> = ArrayList()
    var itemsDto: ItemsDto? = null
    var stockItemsDetailsDto: PurchaseItemsDetailsDto? = null

    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedItemId: Long? = null
    var alreadySelected: Boolean = true

    private val _dnSerialItems = MutableLiveData<List<WarehouseSerialItemDetails>>()
    val dnSerialItems: LiveData<List<WarehouseSerialItemDetails>> = _dnSerialItems
    private var selectedItemDto: PurchaseItemsDetailsDto? = null
    private var enteredQuantity: Double = 0.0


    private fun validateUserDetails(
        returningQty: String
    ): Boolean {
        return if (returningQty.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.return_qty_empty_message))
            false
        } else if (returningQty.isNotEmpty() && (returningQty.toDouble() > (selectedItemDto?.OrderedQty
                ?: 0.0))
        ) {
            _errorReturningQty.postValue(true)
            false
        } else if (selectedItemDto == null) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.item_empty_message))
            false
        } else {
            true
        }
    }

    fun setSelectedItemDto(item: PurchaseItemsDetailsDto?) {
        item?.let { pDto ->
            selectedItemDto = pDto
            val dto = getConvertedItemDto(pDto)
            itemsDto = dto
            _itemDto.postValue(dto)
            enteredQuantity = pDto.userReturningQty
            _returningQuantityString.postValue(if (pDto.userReturningQty > 0) pDto.getUserReturningQtyString() else "")
            _isItemSerialized.postValue(pDto.IsSerialItem == 1)
        }
    }

    private fun getConvertedItemDto(it: PurchaseItemsDetailsDto): ItemsDto =
        ItemsDto(
            ItemName = it.ItemName,
            ItemNameAlias = it.ItemNameArabic,
            Manufacturer = it.Manfacturer,
            ItemPartCode = it.ItemCode,
            Stock = it.Quantity,
            Warehouse = it.Warehouse,
            convertedStockDetails = emptyList(),
            wStockDetails = emptyList()
        )

    fun checkSerialItems() {
        selectedItemDto?.let {
            _navigateToSerialNo.postValue(true)
        } ?: _navigateToSerialNo.postValue(false)
    }

    fun setSelectedSerialItemsList(dtoList: SerialItemsDtoList?) {
        dtoList?.takeIf { it.serialItemsDto != null }?.let { serialItemsList ->
            if (serialItemsList.serialItemsDto?.isNotEmpty() == true && serialItemsList.itemId != null) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                userSelectedItemId = dtoList.itemId
                _returningQuantityString.postValue(getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(true)
            } else if (serialItemsList.itemId == userSelectedItemId && (serialItemsList.serialItemsDto == null || serialItemsList.serialItemsDto.isEmpty())) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                _returningQuantityString.postValue(getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(false)
            }
        }
    }

    fun getItemDto(): PurchaseItemsDetailsDto? = selectedItemDto

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    private fun getAdjustmentQuantity() =
        userSelectedSerialItemsList.size

    fun getCheckBoxStateValue() = showCheckBoxes

    fun getSelectedItems(itemID: Long) {
        val newList =
            userCheckedItems.map { it.copy(ManufactureDate = it.getFormattedManufactureDate()) }
        userCheckedItems.clear()
        userCheckedItems.addAll(newList)
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

    fun checkAndEnableSaveButton() {
        _enableSaveButton.postValue(alreadySelected)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun getSavedItemDto() = stockItemsDetailsDto

    fun setAdjustmentQuantity(text: String) {
        if (text.isNotEmpty()) {
            enteredQuantity = text.toDouble()
            _enableSaveButton.postValue(enteredQuantity > 0)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    fun saveItemStock(quantity: String) {
        if (validateUserDetails(quantity)) {
            stockItemsDetailsDto = selectedItemDto?.copy(
                Quantity = quantity.toDouble(),
                userReturningQty = quantity.toDouble(),
                SerialItems = userSelectedSerialItemsList//if(isItemSerialize){ userSelectedSerialItemsList } else emptyList()
            )
            _saveItemStatus.postValue(true)
        }
    }

    fun setSelectedDeliveryNoteItemDto(
        prItemDto: PurchaseItemsDetailsDto?,
        serialItemsList: SerialItemsDtoList?
    ) {
        setCheckBoxState(true)
        selectedItemDtoInSerialNoScreen = prItemDto
        prItemDto?.let { dto ->
            val itemDto = getConvertedItemDto(dto)
            _convertedItemsDto.postValue(itemDto)
            checkIsListHavingAnySelectedObjects(dto, serialItemsList)
        }
    }

    private fun checkIsListHavingAnySelectedObjects(
        itemsDto: PurchaseItemsDetailsDto,
        serialItemsList: SerialItemsDtoList?
    ) {
        val list = mutableListOf<WarehouseSerialItemDetails>()
        itemsDto.SerialItems?.let { wSerialItemDetails ->
            wSerialItemDetails.map { it.getConvertedWarehouseSerialItemDetails() }
                .let { convertedList ->
                    serialItemsList?.takeIf { it.itemId == itemsDto.ItemID && it.serialItemsDto != null && it.serialItemsDto.isNotEmpty() }
                        ?.let { list ->
                            convertedList.forEach { warehouse ->
                                list.serialItemsDto?.find { it.SerialNumber == warehouse.SerialNumber }
                                    ?.let {
                                        warehouse.selected = true
                                        alreadySelected = true
                                        _enableSaveButton.postValue(true)
                                    }
                            }
                        }
                    list.addAll(convertedList)
                }
        }
        _warehouseSerialNosList.postValue(list.toList())
    }
}