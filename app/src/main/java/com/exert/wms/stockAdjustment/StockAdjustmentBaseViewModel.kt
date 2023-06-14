package com.exert.wms.stockAdjustment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.itemStocks.api.ItemStocksRequestDto
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.StockAdjustmentRepository
import com.exert.wms.stockAdjustment.api.StockItemAdjustmentDto
import com.exert.wms.stockAdjustment.api.WarehouseDto
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StockAdjustmentBaseViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val stockAdjustmentRepo: StockAdjustmentRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _enableSaveButton = MutableLiveData<Boolean>().apply { false }
    val enableSaveButton: LiveData<Boolean> = _enableSaveButton

    private val _itemsList = MutableLiveData<List<String>>()
    val itemsList: LiveData<List<String>> = _itemsList

    private val _enableUpdateButton = MutableLiveData<Boolean>().apply { false }
    val enableUpdateButton: LiveData<Boolean> = _enableUpdateButton

    private val _getItemsStatus = MutableLiveData<Boolean>()
    val getItemsStatus: LiveData<Boolean> = _getItemsStatus

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _saveItemStatus = MutableLiveData<Boolean>()
    val saveItemStatus: LiveData<Boolean> = _saveItemStatus

    private val _navigateToSerialNo = MutableLiveData<Boolean>()
    val navigateToSerialNo: LiveData<Boolean> = _navigateToSerialNo

    private val _errorItemPartCode = MutableLiveData<Boolean>()
    val errorItemPartCode: LiveData<Boolean> = _errorItemPartCode

    private val _errorItemSerialNo = MutableLiveData<Boolean>()
    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private val _warehouseList = MutableLiveData<List<WarehouseDto>>()
    val warehouseList: LiveData<List<WarehouseDto>> = _warehouseList

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    var itemPartCode: String = ""
    var itemSerialNo: String = ""
    var selectedWarehouse: String = ""
    var stockItemsList = mutableListOf<StockItemAdjustmentDto>()

    var adjustmentTypeValue: String = ""
    var itemsDto: ItemsDto? = null
    var warehousesList: List<WarehouseDto>? = null

    init {
        getWarehouseList()
    }

    fun saveItems() {

    }

    fun saveItemStock(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            val item = StockItemAdjustmentDto(
                selectedWarehouse,
                itemPartCode,
                itemSerialNo,
                "",
                "",
                "",
                0,
                adjustmentTypeValue,
                0,
                0.0,
                0.0
            )
            addItemToList(item)
            _saveItemStatus.postValue(true)
            checkAndEnableUpdateButton()
        }
    }

    private fun checkAndEnableUpdateButton() {
        if (stockItemsList.size > 0) {
            _enableUpdateButton.postValue(true)
        } else {
            _enableUpdateButton.postValue(false)
        }
    }

    private fun addItemToList(item: StockItemAdjustmentDto) {
        stockItemsList.add(item)
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
            _errorFieldMessage.postValue(stringProvider.getString(R.string.adjustment_type_empty_message))
            false
        } else if (itemPartCode.isNotEmpty()) {
            _errorItemPartCode.postValue(true)
            false
        } else if (itemSerialNo.isNotEmpty()) {
            _errorItemSerialNo.postValue(true)
            true
        } else {
            _errorItemSelectionMessage.postValue(true)
            false
        }
    }

    fun setAdjustmentType(adjustment: String) {
        adjustmentTypeValue = adjustment
    }

    fun checkItemDetailsEntered(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            _navigateToSerialNo.postValue(true)
        } else {
            _navigateToSerialNo.postValue(false)
        }
    }

    fun searchItemWithPartCode(partCode: String) {
        itemPartCode = partCode
        if (itemPartCode.isNotEmpty()) {
            getItemWarehouseList(itemPartCode, "")
            _errorItemPartCode.postValue(false)
        } else {
            _errorItemPartCode.postValue(true)
        }
        checkAndEnableStatusButton()
    }

    private fun validateItemPartCodeAndSerialNoDetails(
        itemPartCode: String,
        itemSerialNo: String
    ): Boolean {
        return itemPartCode.isNotEmpty() || itemSerialNo.isNotEmpty()
    }

    private fun checkAndEnableStatusButton() {
        if (validateUserDetails(itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            _enableSaveButton.postValue(true)
            _errorItemPartCode.postValue(false)
            _errorItemSerialNo.postValue(false)
        } else {
            _enableSaveButton.postValue(false)
        }
    }

    fun searchItemWithSerialNumber(serialNo: String) {
        itemSerialNo = serialNo
        if (itemSerialNo.isNotEmpty()) {
            getItemWarehouseList("", itemSerialNo)
            _errorItemSerialNo.postValue(false)
        } else {
            _errorItemSerialNo.postValue(true)
        }
        checkAndEnableStatusButton()

    }

    fun checkWarehouse(warehouseName: String) {
        if (warehouseName.isNotEmpty() && warehouseName != stringProvider.getString(R.string.select)) {
            setSelectedWarehouseName(warehouseName)
            _errorWarehouse.postValue(true)
        } else {
            _errorWarehouse.postValue(false)
        }
    }

    private fun getWarehouseList() {
        showProgressIndicator()
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            stockAdjustmentRepo.getWarehouseList()
                .collect { dto ->
                    Log.v("WMS EXERT", "getWarehouseList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Warehouses.isNotEmpty()) {
                        warehousesList = dto.Warehouses
                        _warehouseList.postValue(dto.Warehouses)
                    } else {
                        _errorFieldMessage.postValue(stringProvider.getString(R.string.warehouse_list_empty_message))
                    }
                }
        }
    }

    fun getWarehouseObject(): WarehouseDto? {
        return warehousesList?.filter { it.Warehouse == selectedWarehouse }.run {
            this?.get(0)
        }
    }

    fun setSelectedWarehouseName(warehouseName: String) {
        selectedWarehouse = warehouseName
    }

    fun setSelectedWarehouseDto(warehouse: WarehouseDto?) {
        warehouse?.let {
            selectedWarehouse = it.Warehouse
        }
    }


    private fun getItemWarehouseList(itemPartCode: String, itemSerialNo: String) {
//        itemPartCode="18x085NiCd-Hop"
        showProgressIndicator()
        val request =
            ItemStocksRequestDto(ItemPartCode = itemPartCode, ItemSerialNumber = itemSerialNo)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getItemWarehouseList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getItemWarehouseList response $dto")
                    hideProgressIndicator()
                    if (dto.success) {
                        if (dto.Items != null && dto.Items.isNotEmpty()) {
                            itemsDto = dto.Items[0]
                            _itemDto.postValue(dto.Items[0])
                        } else {
                            _errorGetItemsStatusMessage.postValue(stringProvider.getString(R.string.empty_items_list))
                        }
                    } else {
                        _getItemsStatus.value = (false)
                    }
                }

        }
    }

    fun getItemDto(): ItemsDto? = itemsDto
}