package com.exert.wms.transfer.transferIn.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.transfer.api.ExternalTransferItemsDto
import kotlinx.coroutines.Job

class TransferInItemViewModel : BaseViewModel() {
    private var coroutineJob: Job? = null

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _navigateToSerialNo = MutableLiveData<Boolean>()
    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo

    private val _isItemSerialized = MutableLiveData<Boolean>()
    val isItemSerialized: LiveData<Boolean> = _isItemSerialized

    private val _convertedItemsDto = MutableLiveData<ItemsDto>()
    val convertedItemsDto: LiveData<ItemsDto> = _convertedItemsDto

    private val _transferInSerialItems = MutableLiveData<List<WarehouseSerialItemDetails>>()
    val transferInSerialItems: LiveData<List<WarehouseSerialItemDetails>> = _transferInSerialItems

    private val _quantityString = MutableLiveData<String>().apply { postValue("") }
    val quantityString: LiveData<String> = _quantityString

    var itemsDto: ItemsDto? = null
    private var selectedItemDto: ExternalTransferItemsDto? = null
    private var selectedItemDtoInSerialNoScreen: ExternalTransferItemsDto? = null

    fun setSelectedItemDto(
        externalTransferItemsDto: ExternalTransferItemsDto?
    ) {
        externalTransferItemsDto?.let {
            selectedItemDto = it
            val dto = getConvertedItemDto(it)
            _itemDto.postValue(dto)
            _quantityString.postValue(it.Quantity.toString())
            _isItemSerialized.postValue(it.IsSerialItem == 1)
        }
    }

    private fun getConvertedItemDto(it: ExternalTransferItemsDto): ItemsDto =
        ItemsDto(
            ItemName = it.ItemName,
            ItemNameAlias = it.ItemNameArabic,
            Manufacturer = it.Manfacturer,
            ItemPartCode = it.ItemCode,
            Stock = it.Quantity,
            convertedStockDetails = it.SerialItems,
            wStockDetails = emptyList()
        )

    private fun getWarrantyNumber(warrantyPeriod: String?): String? {
        warrantyPeriod?.let { str ->
            if (str.contains("Year") || str.contains("Years")) {
                return str.filter { it.isDigit() }
            }
        }
        return warrantyPeriod
    }

    fun getItemDto(): ExternalTransferItemsDto? = selectedItemDto

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun checkSerialItems() {
        selectedItemDto?.let {
            if (it.SerialItems?.isNotEmpty() == true) {
                _navigateToSerialNo.postValue(true)
            } else {
                _navigateToSerialNo.postValue(false)
            }
        } ?: _navigateToSerialNo.postValue(false)
    }

    fun setSelectedExternalTransferItemsDto(itemDto: ExternalTransferItemsDto?) {
        selectedItemDtoInSerialNoScreen = itemDto
        itemDto?.let { dto ->
            val itemDto = getConvertedItemDto(dto)
            _convertedItemsDto.postValue(itemDto)
            if (dto.SerialItems?.isNotEmpty() == true) {
                val list = mutableListOf<WarehouseSerialItemDetails>()
                dto.SerialItems.map { it.getConvertedWarehouseSerialItemDetails() }.let {
                    list.addAll(it)
                }

                _transferInSerialItems.postValue(list.toList())
            }
        }
    }
}