package com.exert.wms.returns.salesReturn.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.returns.api.SalesItemsDetailsDto
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SalesReturnItemViewModel(
    private val stringProvider: StringProvider,
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

    private val _warehouseSerialNosList = MutableLiveData<List<WarehouseSerialItemDetails>?>()
    val warehouseSerialNosList: LiveData<List<WarehouseSerialItemDetails>?> =
        _warehouseSerialNosList

    private val _isItemSerialized = MutableLiveData<Boolean>()
    val isItemSerialized: LiveData<Boolean> = _isItemSerialized

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _checkedSerialItemsList = MutableLiveData<SerialItemsDtoList>()
    val checkedSerialItemsList: LiveData<SerialItemsDtoList> = _checkedSerialItemsList

    private val _returningQuantityString = MutableLiveData<String>()
    val returningQuantityString: LiveData<String> = _returningQuantityString

    private val _errorReturningQty = MutableLiveData<Boolean>()
    val errorReturningQty: LiveData<Boolean> = _errorReturningQty

    private val _convertedItemsDto = MutableLiveData<ItemsDto>()
    val convertedItemsDto: LiveData<ItemsDto> = _convertedItemsDto

    private var selectedItemDtoInSerialNoScreen: SalesItemsDetailsDto? = null
    private var showCheckBoxes: Boolean = false

    var itemsDto: ItemsDto? = null
    var stockItemsDetailsDto: SalesItemsDetailsDto? = null

    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var previousUserCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedItemId: Long? = null
    var alreadySelected: Boolean = true
    var originalSerialItemsList: List<SerialItemsDto>? = null

    private var selectedItemDto: SalesItemsDetailsDto? = null
    private var enteredQuantity: Double = 0.0

    private fun validateUserDetails(
        returningQty: String
    ): Boolean {
        return if (returningQty.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.return_qty_empty_message))
            false
        } else if (returningQty.isNotEmpty() && (returningQty.toDouble() > (selectedItemDto?.InvoicedQty
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

    fun setSelectedItemDto(item: SalesItemsDetailsDto?) {
        item?.let { pDto ->
            selectedItemDto = pDto
            val dto = getConvertedItemDto(pDto)
            itemsDto = dto
            _itemDto.postValue(dto)
            enteredQuantity = pDto.userReturningQty
            userSelectedSerialItemsList = getAlreadySelectedItemsList(pDto.SerialItems)
            _returningQuantityString.postValue(if (pDto.userReturningQty > 0) pDto.getUserReturningQtyString() else "")
            _isItemSerialized.postValue(pDto.IsSerialItem == 1)
        }
    }

    private fun getAlreadySelectedItemsList(serialItems: List<SerialItemsDto>?): java.util.ArrayList<SerialItemsDto> =
        (serialItems?.filter { it.selected } ?: emptyList()) as java.util.ArrayList<SerialItemsDto>

    private fun getConvertedItemDto(it: SalesItemsDetailsDto): ItemsDto =
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

    fun getItemDto(): SalesItemsDetailsDto? = selectedItemDto

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    private fun getAdjustmentQuantity() =
        userSelectedSerialItemsList.size

    fun getCheckBoxStateValue() = showCheckBoxes

    fun getSelectedItems(itemID: Long) {
        val dto = SerialItemsDtoList(userCheckedItems, itemId = itemID)
        _checkedSerialItemsList.postValue(dto)
    }

    fun setCheckedItems(checkedItems: ArrayList<SerialItemsDto>) {
        userCheckedItems = checkedItems
        val areEqual = compareListsById(previousUserCheckedItems, userCheckedItems)
        _enableSaveButton.postValue(!areEqual)
    }

    private fun compareListsById(
        list1: List<SerialItemsDto>,
        list2: List<SerialItemsDto>
    ): Boolean {
        if (list1.size != list2.size) {
            return false
        }

        val set1 = list1.map { it.SerialNumber }.toSet()
        val set2 = list2.map { it.SerialNumber }.toSet()

        return set1 == set2
    }

    fun getUserSelectedSerialItemsList(): SerialItemsDtoList =
        SerialItemsDtoList(userSelectedSerialItemsList, userSelectedItemId)

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
                SerialItems = getSerialItemsWithUserSelection() ?: emptyList()
            )
            _saveItemStatus.postValue(true)
        }
    }

    private fun getSerialItemsWithUserSelection(): List<SerialItemsDto>? {
        val mainList = selectedItemDto?.SerialItems
        userSelectedSerialItemsList.forEach { userItem ->
            if (mainList != null) {
                mainList.find { it.SerialNumber == userItem.SerialNumber }?.let { mainItem ->
                    mainItem.selected = true
                }
            }
        }
        return mainList
    }

    fun setSelectedSalesReturnItemDto(
        prItemDto: SalesItemsDetailsDto?,
        serialItemsList: SerialItemsDtoList?
    ) {
        setCheckBoxState(true)
        selectedItemDtoInSerialNoScreen = prItemDto
        prItemDto?.let { dto ->
            showProgressIndicator()
//            coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
                val itemDto = getConvertedItemDto(dto)
                _convertedItemsDto.postValue(itemDto)
                originalSerialItemsList = serialItemsList?.serialItemsDto
                checkIsListHavingAnySelectedObjects(dto, serialItemsList)
                hideProgressIndicator()
//            }
        }
    }

    private fun checkIsListHavingAnySelectedObjects(
        itemsDto: SalesItemsDetailsDto,
        serialItemsList: SerialItemsDtoList?
    ) {
        val list = mutableListOf<WarehouseSerialItemDetails>()
        itemsDto.SerialItems?.let { wSerialItemDetails ->
            wSerialItemDetails.map { it.getConvertedWarehouseSerialItemDetails() }
                .let { convertedList ->
                    serialItemsList?.takeIf { it.itemId == itemsDto.ItemID && it.serialItemsDto != null && it.serialItemsDto.isNotEmpty() }
                        ?.let { list ->
                            convertedList.forEach { warehouse ->
                                warehouse.selected = false
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

    fun setUserSelectedItems(checkedItems: java.util.ArrayList<SerialItemsDto>) {
        userCheckedItems = checkedItems
        previousUserCheckedItems.addAll(checkedItems)
        setCheckedItems(checkedItems)
    }
}