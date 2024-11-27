package com.exert.wms.delivery.deliveryNote.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider

class DeliveryNoteItemViewModel(private val stringProvider: StringProvider) : BaseViewModel() {

    private val _enableSaveButton = MutableLiveData<Boolean>().apply { false }
    val enableSaveButton: LiveData<Boolean> = _enableSaveButton

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _navigateToSerialNo = MutableLiveData<Boolean>()
    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo

    private val _isItemSerialized = MutableLiveData<Boolean>()
    val isItemSerialized: LiveData<Boolean> = _isItemSerialized

    private val _quantityString = MutableLiveData<String>().apply { postValue("") }
    val quantityString: LiveData<String> = _quantityString

    private val _convertedItemsDto = MutableLiveData<ItemsDto>()
    val convertedItemsDto: LiveData<ItemsDto> = _convertedItemsDto

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _checkedSerialItemsList = MutableLiveData<SerialItemsDtoList>()
    val checkedSerialItemsList: LiveData<SerialItemsDtoList> = _checkedSerialItemsList

    private val _returnedQuantityString = MutableLiveData<String>().apply { value = stringProvider.getString(R.string.zero)}
    val returnedQuantityString: LiveData<String> = _returnedQuantityString

    private val _returningQuantityString = MutableLiveData<String>()
    val returningQuantityString: LiveData<String> = _returningQuantityString

    private val _errorReturningQty = MutableLiveData<Boolean>()
    val errorReturningQty: LiveData<Boolean> = _errorReturningQty

    private val _warehouseSerialNosList = MutableLiveData<List<WarehouseSerialItemDetails>?>()
    val warehouseSerialNosList: LiveData<List<WarehouseSerialItemDetails>?> =
        _warehouseSerialNosList

    private var selectedItemDto: DeliveryNoteItemsDetailsDto? = null
    var stockItemsDetailsDto: DeliveryNoteItemsDetailsDto? = null
    private var selectedItemDtoInSerialNoScreen: DeliveryNoteItemsDetailsDto? = null

    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()
    private var previousUserCheckedItems: ArrayList<SerialItemsDto> = ArrayList()

    var itemsDto: ItemsDto? = null
    private var userSelectedItemId: Long? = null
    var alreadySelected: Boolean = true
    private var showCheckBoxes: Boolean = false
    private var enteredQuantity: Double = 0.0
    private var actualOrPreviousQuantity: Double = 0.0
    var originalSerialItemsList: List<SerialItemsDto>? = null

    private fun validateUserDetails(
        returningQty: String
    ): Boolean {
        val balanceQuantity = selectedItemDto?.QTYReceived?.let { selectedItemDto?.Quantity?.minus(it) }
        return if (returningQty.isEmpty()) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.return_qty_empty_message))
            false
        } else if (returningQty.isNotEmpty() && (returningQty.toDouble() > (balanceQuantity
                ?: 0.0))
        ) {
            _errorReturningQty.postValue(true)
            false
        }
        else if (selectedItemDto == null) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.item_empty_message))
            false
        } else {
            true
        }
    }

    fun setSelectedItemDto(item: DeliveryNoteItemsDetailsDto?) {
        item?.let { dnDto ->
            selectedItemDto = dnDto
            val dto = getConvertedItemDto(dnDto)
            itemsDto = dto
            _itemDto.postValue(dto)
            enteredQuantity = dnDto.QTYReceived
            actualOrPreviousQuantity = enteredQuantity
            userSelectedItemId = dnDto.ItemID
            userSelectedSerialItemsList = getAlreadySelectedItemsList(dnDto.SerialItems)
            _returnedQuantityString.postValue(dnDto.QTYReceived.toString())
            _returningQuantityString.postValue(if (dnDto.userReturningQty > 0) dnDto.getUserReturningQtyString() else "")
            _isItemSerialized.postValue(dnDto.IsSerialItem == 1)
        }
    }

    private fun getAlreadySelectedItemsList(serialItems: List<SerialItemsDto>?): java.util.ArrayList<SerialItemsDto> =
        (serialItems?.filter { it.selected } ?: emptyList()) as java.util.ArrayList<SerialItemsDto>

    private fun getConvertedItemDto(it: DeliveryNoteItemsDetailsDto): ItemsDto =
        ItemsDto(
            ItemName = it.ItemName,
            ItemNameAlias = it.ItemNameArabic,
            Manufacturer = it.Manfacturer,
            ItemPartCode = it.ItemCode,
            Stock = it.Quantity,
            Warehouse=it.Warehouse,
            convertedStockDetails = emptyList(),//it.SerialItems,
            wStockDetails = emptyList()
        )

    fun checkSerialItems() {
        selectedItemDto?.let {
            _navigateToSerialNo.postValue(true)
        } ?: _navigateToSerialNo.postValue(false)
    }

    fun setSelectedSerialItemsList(dtoList: SerialItemsDtoList?) {
        dtoList?.serialItemsDto?.let { serialItemsList ->
            if (dtoList.serialItemsDto?.isNotEmpty() == true && dtoList.itemId != null) {
                userSelectedSerialItemsList =
                    dtoList.serialItemsDto as ArrayList<SerialItemsDto>
                userSelectedItemId = dtoList.itemId
                _returningQuantityString.postValue (getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(true)
            } else if (dtoList.itemId == userSelectedItemId && (dtoList.serialItemsDto == null || dtoList.serialItemsDto.isEmpty())) {
                _returningQuantityString.postValue("0")
                val isItDifferent= checkUserPreviousSelectedAndCurrentSelectedDifferenceItems(dtoList.itemId, dtoList.serialItemsDto)
                userSelectedItemId = dtoList?.itemId
                userSelectedSerialItemsList =
                    dtoList.serialItemsDto as ArrayList<SerialItemsDto>
                _enableSaveButton.postValue(isItDifferent)
                checkAndEnableSaveButton()
            }
        } ?: updateList(dtoList)
    }

    private fun checkUserPreviousSelectedAndCurrentSelectedDifferenceItems(
        itemId: Long?,
        serialItemsDto: List<SerialItemsDto>?
    ): Boolean {
        return itemId?.let { id ->
            id.takeIf { it == userSelectedItemId}?.let {
                userSelectedSerialItemsList != serialItemsDto
            } ?: false
        } ?: false
    }

    private fun updateList(dtoList: SerialItemsDtoList?) {
        dtoList?.let {
            if (it.itemId == userSelectedItemId){
                userSelectedSerialItemsList = it.serialItemsDto as ArrayList<SerialItemsDto>
                _returningQuantityString.value = (getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(true)
            }
        }
    }

    fun getItemDto(): DeliveryNoteItemsDetailsDto? = selectedItemDto

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    private fun getAdjustmentQuantity() = userSelectedSerialItemsList.size

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
    }

    fun getSavedItemDto() = stockItemsDetailsDto

    fun setAdjustmentQuantity(text: String) {
        if (text.isNotEmpty()) {
            enteredQuantity = text.toDouble()
            checkAndEnableSaveButton()
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    fun saveItemStock(quantity: String) {
        if (validateUserDetails(quantity)) {
            stockItemsDetailsDto = selectedItemDto?.copy(
                userReturningQty = quantity.toDouble(),
                SerialItems = getSerialItemsWithUserSelection() ?: emptyList()
            )
            _saveItemStatus.postValue(true)
        }
    }


    private fun getSerialItemsWithUserSelection(): List<SerialItemsDto>? {
        val mainList = selectedItemDto?.SerialItems
        if(userSelectedSerialItemsList.isNullOrEmpty()){
            mainList?.forEach { it.selected = false }
        }else {
            userSelectedSerialItemsList.forEach { userItem ->
                if (mainList != null) {
                    mainList.find { it.SerialNumber == userItem.SerialNumber }?.let { mainItem ->
                        mainItem.selected = userItem.selected
                    }
                }
            }
        }
        return mainList
    }

    fun setSelectedDeliveryNoteItemDto(nItemDto: DeliveryNoteItemsDetailsDto?,
                                       serialItemsList: SerialItemsDtoList?) {
        setCheckBoxState(true)
        selectedItemDtoInSerialNoScreen = nItemDto
        nItemDto?.let { dto ->
            showProgressIndicator()
            val itemDto = getConvertedItemDto(dto)
            _convertedItemsDto.postValue(itemDto)
            originalSerialItemsList = serialItemsList?.serialItemsDto
            checkIsListHavingAnySelectedObjects(dto, serialItemsList)
            hideProgressIndicator()
        }
    }

    private fun checkIsListHavingAnySelectedObjects(
        itemsDto: DeliveryNoteItemsDetailsDto,
        serialItemsList: SerialItemsDtoList?
    ) {
        val list = mutableListOf<WarehouseSerialItemDetails>()
        itemsDto.SerialItems?.let { wSerialItemDetails ->
            wSerialItemDetails.map { it.getConvertedWarehouseSerialItemDetails() }
                .let { convertedList ->
                    serialItemsList?.takeIf { it.itemId == itemsDto.ItemID}?.let { sList ->
                        if(sList.serialItemsDto != null && sList.serialItemsDto.isNotEmpty()){
                            convertedList.forEach { warehouse ->
                                warehouse.selected = false
                                sList.serialItemsDto?.find { it.SerialNumber == warehouse.SerialNumber }
                                    ?.let {
                                        warehouse.selected = true
                                        alreadySelected = true
                                        _enableSaveButton.postValue(true)
                                    }
                            }
                        }else{ // if serialItemsList is null or empty
                            convertedList?.forEach { it.selected = false }
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

    private fun checkAndEnableSaveButton(){
        if(actualOrPreviousQuantity!= enteredQuantity){
            _enableSaveButton.postValue(true)
        }else if(actualOrPreviousQuantity != getAdjustmentQuantity().toDouble()){
            _enableSaveButton.postValue(true)
        }else{
            _enableSaveButton.postValue(false)
        }
    }
}