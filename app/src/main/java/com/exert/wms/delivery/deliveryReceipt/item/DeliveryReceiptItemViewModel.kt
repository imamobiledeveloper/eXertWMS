package com.exert.wms.delivery.deliveryReceipt.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.delivery.api.DeliveryReceiptItemsDetailsDto
import com.exert.wms.itemStocks.api.*
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.StockItemsDetailsDto
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.math.ceil

class DeliveryReceiptItemViewModel(
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

    private val _errorQuantity = MutableLiveData<String>()
    val errorQuantity: LiveData<String> = _errorQuantity

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

    private val _convertedItemsDto = MutableLiveData<ItemsDto>()
    val convertedItemsDto: LiveData<ItemsDto> = _convertedItemsDto

    private val _dnSerialItems = MutableLiveData<List<WarehouseSerialItemDetails>>()
    val dnSerialItems: LiveData<List<WarehouseSerialItemDetails>> = _dnSerialItems

    private val _returnedQuantityString =
        MutableLiveData<String>().apply { value = stringProvider.getString(R.string.zero) }
    val returnedQuantityString: LiveData<String> = _returnedQuantityString

    private var selectedItemDto: DeliveryReceiptItemsDetailsDto? = null
    private var stockItemsDetailsDto: DeliveryReceiptItemsDetailsDto? = null

    private var userCheckedItems: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedSerialItemsList: ArrayList<SerialItemsDto> = ArrayList()
    private var userSelectedItemId: Long? = null
    private var alreadySelected: Boolean = true
    private var showCheckBoxes: Boolean = false

    var itemsDto: ItemsDto? = null
    private var selectedItemDtoInSerialNoScreen: DeliveryReceiptItemsDetailsDto? = null

    private var enteredQuantity: Double = 0.0
    private var VendorTolerancePercentage: Double = 0.0

    private fun validateUserDetails(
        quantity: String
    ): Boolean {
        return if (quantity.isEmpty()) {
            _errorQuantityType.postValue(true)
            false
        } else if (selectedItemDto == null) {
            _errorFieldMessage.postValue(stringProvider.getString(R.string.item_empty_message))
            false
        } else {
            true
        }
    }

    fun saveItemStock(quantity: String) {
        if (validateUserDetails(quantity)) {
            stockItemsDetailsDto = selectedItemDto?.copy(
                Quantity = quantity.toDouble(),
                SerialItems = userSelectedSerialItemsList//if(isItemSerialize){ userSelectedSerialItemsList } else emptyList()
            )
            _saveItemStatus.postValue(true)
        }
    }

    fun setSelectedSerialItemsList(dtoList: SerialItemsDtoList?) {
        dtoList?.takeIf { it.serialItemsDto != null }?.let { serialItemsList ->
            if (serialItemsList.serialItemsDto?.isNotEmpty() == true && serialItemsList.itemId != null) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                userSelectedItemId = dtoList.itemId
                _quantityString.postValue(getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(true)
            } else if (serialItemsList.itemId == userSelectedItemId && (serialItemsList.serialItemsDto == null || serialItemsList.serialItemsDto.isEmpty())) {
                userSelectedSerialItemsList =
                    serialItemsList.serialItemsDto as ArrayList<SerialItemsDto>
                _quantityString.postValue(getAdjustmentQuantity().toString())
                _enableSaveButton.postValue(false)
            }
        }
    }

    private fun getWarrantyNumber(warrantyPeriod: String?): String? {
        warrantyPeriod?.let { str ->
            if (str.contains("Year") || str.contains("Years")) {
                return str.filter { it.isDigit() }
            }
        }
        return warrantyPeriod
    }

    fun getItemDto(): DeliveryReceiptItemsDetailsDto? = selectedItemDto

    private fun getAdjustmentQuantity() =
        userSelectedSerialItemsList.size

    fun getUserSelectedSerialItemsList(): SerialItemsDtoList =
        SerialItemsDtoList(userSelectedSerialItemsList, userSelectedItemId)


    fun checkAndEnableSaveButton() {
        _enableSaveButton.postValue(alreadySelected)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun setSelectedItemDto(item: DeliveryReceiptItemsDetailsDto?, VendorTolerancePercent: Double) {
        VendorTolerancePercentage = VendorTolerancePercent
        item?.let { drDto ->
            selectedItemDto = drDto
            selectedItemDto?.let {
                drDto.OrderedQty = it.QTYOrdered
            }
            val dto = getConvertedItemDto(drDto)
            itemsDto = dto
            _itemDto.postValue(dto)
            enteredQuantity = drDto.Quantity
            _returnedQuantityString.postValue(drDto.QTYReceived.toString())
            val qty =
                if (drDto.IsSerialItem == 1) "0.0" else getMinimumQuantityBasedOnVendorTolerance(
                    drDto.QTYOrdered,
                    drDto.QTYReceived
                )
            _quantityString.postValue(qty)//drDto.Quantity.toString())
            _isItemSerialized.postValue(drDto.IsSerialItem == 1)
        }
    }

    private fun getMinimumQuantityBasedOnVendorTolerance(
        qtyOrdered: Double,
        qtyReceived: Double
    ): String? {
        val balanceQty = qtyOrdered - qtyReceived
        VendorTolerancePercentage
        if (VendorTolerancePercentage == 0.0) return (roundUp(balanceQty)).toString()

        // Condition 3: If percentage > 0.0, restrict range based on the percentage
        if (VendorTolerancePercentage > 0.0) {
            return (roundUp(balanceQty * (VendorTolerancePercentage / 100))).toString()
        }
        return ""
    }

    private fun roundUp(value: Double): Int {
        // Round up to the next integer if there's a decimal part
        return ceil(value).toInt()
    }

    private fun getConvertedItemDto(it: DeliveryReceiptItemsDetailsDto): ItemsDto =
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

    fun getSavedItemDto() = stockItemsDetailsDto

    fun setAdjustmentQuantity(text: String) {
        if (text.isNotEmpty()) {
            enteredQuantity = text.toDouble()
            _enableSaveButton.postValue(enteredQuantity > 0 && validateQuantity(enteredQuantity) == true)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    private fun validateQuantity(quantity: Double): Boolean? {
        val orderQuantity = selectedItemDto?.QTYOrdered ?: 0.0
        val receivedQuantity = selectedItemDto?.QTYReceived ?: 0.0
//        val balanceQty = orderQuantity - receivedQuantity

//        VendorTolerancePercentage = 35.0
        // Case 1: VendorTolerancePercentage = 0%
        if (VendorTolerancePercentage == 0.0) {
            // Only the exact order quantity is allowed
            if (quantity > orderQuantity){//orderQuantity) {
                _errorQuantity.postValue(stringProvider.getString(R.string.error_quantity_must_not_exceed_received_qty_message))
                return false
            } else if (quantity != orderQuantity){//orderQuantity) {
                _errorQuantity.postValue(stringProvider.getString(R.string.error_quantity_is_mismatch_message))
                return false
            }
        }
        // Case 2: VendorTolerancePercentage > 0% and <= 100%
        else if (VendorTolerancePercentage > 0.0 && VendorTolerancePercentage < 100.0) {
            // Calculate the minimum allowed quantity based on VendorTolerancePercentage
//            val minQuantity = Math.ceil(orderQuantity * (VendorTolerancePercentage / 100)).toInt()
            val minQuantity = Math.ceil(orderQuantity * (VendorTolerancePercentage / 100)).toInt()

            // The quantity should be between the minimum allowed and orderQuantity
            if (quantity > orderQuantity){//orderQuantity) {
                _errorQuantity.postValue(stringProvider.getString(R.string.error_quantity_must_not_exceed_received_qty_message))
                return false
            } else if (quantity < minQuantity || quantity > orderQuantity){//orderQuantity) {
                _errorQuantity.postValue(stringProvider.getString(R.string.error_quantity_is_mismatch_message))
                return false
            }
            //     if qty >. ordered qty show error msg  " Received Qty must not exceed Ordered Qty"
        }
        // Case 3: VendorTolerancePercentage = 100%
        else if (VendorTolerancePercentage == 100.0) {
            // The user can transact for any quantity between 1 and orderQuantity
            if (quantity > orderQuantity){//orderQuantity) {
                _errorQuantity.postValue(stringProvider.getString(R.string.error_quantity_must_not_exceed_received_qty_message))
                return false
            } else if (quantity < 1 || quantity > orderQuantity){//orderQuantity) {
                _errorQuantity.postValue(stringProvider.getString(R.string.error_quantity_is_mismatch_message))
                return false
            }
        }

        // All conditions passed
        return true
    }

    fun setSelectedDeliveryReceiptItemDto(
        rItemDto: DeliveryReceiptItemsDetailsDto?,
        serialItemsList: SerialItemsDtoList?
    ) {
        selectedItemDtoInSerialNoScreen = rItemDto
        rItemDto?.let { dto ->
            val itemDto = getConvertedItemDto(dto)
            _convertedItemsDto.postValue(itemDto)
            if (dto.SerialItems?.isNotEmpty() == true) {
                val list = mutableListOf<WarehouseSerialItemDetails>()
                dto.SerialItems.map { it.getConvertedWarehouseSerialItemDetails() }.let {
                    list.addAll(it)
                }
                _dnSerialItems.postValue(list.toList())
            }
        }
    }

    fun getCheckBoxStateValue() = showCheckBoxes

    fun setCheckedItems(checkedItems: ArrayList<SerialItemsDto>) {
        userCheckedItems = checkedItems
        if (userCheckedItems.isNotEmpty()) {
            setConvertedWarehouseSerialNoList()
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
                        WarentyDays = getWarrantyNumber(dto.WarrantyPeriod),
                        selected = true
                    )
                }
            }

        newList?.let {
            _warehouseSerialNosList.postValue(it)
        }
    }

    fun getSelectedItems(itemID: Long) {
        val dto = SerialItemsDtoList(userCheckedItems, itemId = itemID)
        _checkedSerialItemsList.postValue(dto)
    }
}