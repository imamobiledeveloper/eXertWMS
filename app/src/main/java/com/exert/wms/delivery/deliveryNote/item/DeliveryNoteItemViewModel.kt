package com.exert.wms.delivery.deliveryNote.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.transfer.api.ExternalTransferItemsDto

class DeliveryNoteItemViewModel : BaseViewModel() {

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

    private val _dnSerialItems = MutableLiveData<List<WarehouseSerialItemDetails>>()
    val dnSerialItems: LiveData<List<WarehouseSerialItemDetails>> = _dnSerialItems

    private var selectedItemDto: DeliveryNoteItemsDetailsDto? = null

    private var selectedItemDtoInSerialNoScreen: DeliveryNoteItemsDetailsDto? = null

    fun checkSerialItems() {
        selectedItemDto?.let {
            if (it.SerialItems?.isNotEmpty() == true) {
                _navigateToSerialNo.postValue(true)
            } else {
                _navigateToSerialNo.postValue(false)
            }
        } ?: _navigateToSerialNo.postValue(false)
    }

    fun setSelectedItemDto(item: DeliveryNoteItemsDetailsDto?) {
        item?.let {
            selectedItemDto = it
            val dto = getConvertedItemDto(it)
            _itemDto.postValue(dto)
            _quantityString.postValue(it.Quantity.toString())
            _isItemSerialized.postValue(it.IsSerialItem == 1)
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

    fun getItemDto(): DeliveryNoteItemsDetailsDto? = selectedItemDto

    fun setSelectedDeliveryNoteItemDto(itemDto: DeliveryNoteItemsDetailsDto?) {
        selectedItemDtoInSerialNoScreen = itemDto
        itemDto?.let { dto ->
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


    private fun getConvertedItemDto(it: DeliveryNoteItemsDetailsDto): ItemsDto =
        ItemsDto(
            ItemName = it.ItemName,
            ItemNameAlias = it.ItemNameArabic,
            Manufacturer = it.Manfacturer,
            ItemPartCode = it.ItemCode,
            Stock = it.Quantity,
            convertedStockDetails = it.SerialItems,
            wStockDetails = emptyList()
        )

}