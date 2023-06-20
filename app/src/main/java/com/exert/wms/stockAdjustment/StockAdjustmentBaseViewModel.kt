package com.exert.wms.stockAdjustment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.*
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

    private val _errorAdjustmentType = MutableLiveData<Boolean>()
    val errorAdjustmentType: LiveData<Boolean> = _errorAdjustmentType

    private val _errorItemSerialNo = MutableLiveData<Boolean>()
    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

    private val _errorWarehouse = MutableLiveData<Boolean>()
    val errorWarehouse: LiveData<Boolean> = _errorWarehouse

    private val _warehouseList = MutableLiveData<List<WarehouseDto>>()
    val warehouseList: LiveData<List<WarehouseDto>> = _warehouseList

    private val _warehouseStringList = MutableLiveData<List<String>>()
    val warehouseStringList: LiveData<List<String>> = _warehouseStringList

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private val _warehouseSerialNosList = MutableLiveData<List<WarehouseSerialItemDetails>?>()
    val warehouseSerialNosList: LiveData<List<WarehouseSerialItemDetails>?> =
        _warehouseSerialNosList

    private val _isItemSerialized = MutableLiveData<Boolean>()
    val isItemSerialized: LiveData<Boolean> = _isItemSerialized

    var itemPartCode: String = ""
    var itemSerialNo: String = ""
    var selectedWarehouse: String = ""
    var warehouseDto: WarehouseDto? = null
    var stockItemsList = mutableListOf<StockItemAdjustmentDto>()

    var showCheckBoxes: Boolean = false
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
            _errorAdjustmentType.postValue(true)
            false
        } else if (itemPartCode.isEmpty() && itemSerialNo.isEmpty()) {
            _errorItemPartCode.postValue(true)
            false
        } else {
            _errorItemSelectionMessage.postValue(true)
            true
        }
    }

    fun setAdjustmentType(adjustment: String) {
        adjustmentTypeValue = adjustment
        _errorAdjustmentType.postValue(false)
    }

    fun getAdjustmentType() = adjustmentTypeValue

    fun checkItemDetailsEntered(itemPartCode: String, itemSerialNo: String) {
        if (validateUserDetails(itemPartCode, itemSerialNo, adjustmentTypeValue)) {
            _navigateToSerialNo.postValue(true)
        }
    }

    fun searchItemWithPartCode(partCode: String) {
        itemPartCode = partCode
        if (itemPartCode.isNotEmpty() && warehouseDto != null) {
            getWarehouseSerialNosList(itemPartCode, "", warehouseDto!!.WarehouseID)
            _errorItemPartCode.postValue(false)
        } else {
            _errorItemPartCode.postValue(true)
        }
    }

    fun searchItemWithSerialNumber(serialNo: String) {
        itemSerialNo = serialNo
        if (itemSerialNo.isNotEmpty() && warehouseDto != null) {
            getWarehouseSerialNosList("", itemSerialNo, warehouseDto!!.WarehouseID)
            _errorItemSerialNo.postValue(false)
        } else {
            _errorItemSerialNo.postValue(true)
        }
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

    fun checkWarehouse() {
        if (selectedWarehouse.isNotEmpty() && selectedWarehouse != stringProvider.getString(R.string.select)) {
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
                        val stringList: List<String> = dto.Warehouses.map { it.Warehouse }
                        _warehouseStringList.postValue(stringList)
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

    private fun setSelectedWarehouseName(warehouseName: String) {
        selectedWarehouse = warehouseName
    }

    fun setSelectedWarehouseDto(warehouse: WarehouseDto?) {
        warehouse?.let {
            warehouseDto = it
            selectedWarehouse = it.Warehouse
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

    fun selectedWarehouse(warehouseName: String) {
        if (warehouseName != stringProvider.getString(R.string.select)) {
            selectedWarehouse = warehouseName
        }
    }

    fun getSelectedWarehouseIndex(): Int {
        return _warehouseStringList.value?.indexOf(selectedWarehouse) ?: 0
    }

    fun getWarehouseStockDetails(): WarehouseStockDetails? {
        return itemsDto?.wStockDetails?.find { it.WarehouseID == getWarehouseObject()?.WarehouseID }
    }

    private fun setCheckBoxState(checkBoxState: Boolean) {
        showCheckBoxes = checkBoxState
    }

    fun getCheckBoxStateValue() = showCheckBoxes

    fun setWarehouseAndItemDetails(
        itemsDto: ItemsDto?,
        warehouseStockDetails: WarehouseStockDetails?,
        adjustmentType: String
    ) {
        if (adjustmentType.isNotEmpty()) {
            setAdjustmentType(adjustmentType)
            if (adjustmentType == stringProvider.getString(R.string.positive)) {// positive adjustment
                setCheckBoxState(false)
            } else {//negative type-get serial numbers list
                setCheckBoxState(true)
                getSerialNumbersList(itemsDto, warehouseStockDetails)
            }
        }
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
}