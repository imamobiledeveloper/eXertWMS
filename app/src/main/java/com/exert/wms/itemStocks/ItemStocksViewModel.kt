package com.exert.wms.itemStocks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.itemStocks.api.*
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ItemStocksViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var coroutineJob: Job? = null

    private val _itemsList = MutableLiveData<List<ItemsDto>>()
    val itemsList: LiveData<List<ItemsDto>> = _itemsList

    private val _itemDto = MutableLiveData<ItemsDto>()
    val itemDto: LiveData<ItemsDto> = _itemDto

    private val _warehouseSerialIsList = MutableLiveData<List<WarehouseSerialItemDetails>>()
    val warehouseSerialIsList: LiveData<List<WarehouseSerialItemDetails>> = _warehouseSerialIsList

    private val _getItemsStatus = MutableLiveData<Boolean>()
    val getItemsStatus: LiveData<Boolean> = _getItemsStatus

    private val _errorGetItemsStatusMessage = MutableLiveData<String>()
    val errorGetItemsStatusMessage: LiveData<String> = _errorGetItemsStatusMessage

    private val _errorItemSelectionMessage = MutableLiveData<Boolean>()
    val errorItemSelectionMessage: LiveData<Boolean> = _errorItemSelectionMessage

    private val _itemStockStatus = MutableLiveData<Boolean>()
    val itemStockStatus: LiveData<Boolean> = _itemStockStatus

    private val _getItemsSerialNosStatus = MutableLiveData<Boolean>()
    val getItemsSerialNosStatus: LiveData<Boolean> = _getItemsSerialNosStatus

    private val _checkBoxState = MutableLiveData<Boolean>()
    val checkBoxState: LiveData<Boolean> = _checkBoxState

    private val _enableStatusButton = MutableLiveData<Boolean>().apply { false }
    val enableStatusButton: LiveData<Boolean> = _enableStatusButton

    private val _errorItemPartCode = MutableLiveData<Boolean>()
    val errorItemPartCode: LiveData<Boolean> = _errorItemPartCode

    private val _errorItemSerialNo = MutableLiveData<Boolean>()
    val errorItemSerialNo: LiveData<Boolean> = _errorItemSerialNo

    var itemPartCode: String = ""
    var itemSerialNo: String = ""
    var itemsDto: ItemsDto? = null

    private fun getOnlineSalesItems(itemPartCode: String, itemSerialNo: String) {
        showProgressIndicator()
        //{"ItemPartCode":"18x085NiCd-Hop"}
        val request = ItemStocksRequestDto(ItemPartCode = "18x085NiCd-Hop")
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getOnlineSalesItems(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getOnlineSalesItems response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Items.isNotEmpty()) {
                        itemsDto = dto.Items[0]
                        _itemDto.postValue(dto.Items[0])
                    } else {
                        _getItemsStatus.postValue(false)
                    }
                }

        }
    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        Log.v("WMS EXERT", "Error ${throwable.message}")
        _errorGetItemsStatusMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_get_items_message
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun checkItemStock() {
        if (_itemDto.value?.ItemName?.isNotEmpty() == true) {
            _itemStockStatus.postValue(true)
        } else {
            _itemStockStatus.postValue(false)
        }
    }

    private fun validateUserDetails(itemPartCode: String, itemSerialNo: String): Boolean {
        return itemPartCode.isNotEmpty() || itemSerialNo.isNotEmpty()
    }

    fun searchItemWithPartCode(partCode: String) {
        itemPartCode = partCode
        if (itemPartCode.isNotEmpty()) {
            // api call
            getOnlineSalesItems(itemPartCode, "")
            _errorItemPartCode.postValue(false)
        } else {
            _errorItemPartCode.postValue(true)
        }
        checkAndEnableStatusButton()
    }

    private fun checkAndEnableStatusButton() {
        if (validateUserDetails(itemPartCode, itemSerialNo)) {
            _enableStatusButton.postValue(true)
            _errorItemPartCode.postValue(false)
            _errorItemSerialNo.postValue(false)
        } else {
            _enableStatusButton.postValue(false)
        }
    }

    fun searchItemWithSerialNumber(serialNo: String) {
        itemSerialNo = serialNo
        if (itemSerialNo.isNotEmpty()) {
            // api call
            getOnlineSalesItems("", itemSerialNo)
            _errorItemSerialNo.postValue(false)
        } else {
            _errorItemSerialNo.postValue(true)
        }
        checkAndEnableStatusButton()

    }

    fun setCheckBoxState(checkBoxState: Boolean) {
        _checkBoxState.postValue(checkBoxState)
    }

    fun getItemDto(): ItemsDto? = itemsDto
    fun setItemDto(itemsDetails: ItemsDto) {
        this.itemsDto = itemsDetails
    }

    fun getSerialNumbersList(itemsDto: ItemsDto?, warehouseStockDetails: WarehouseStockDetails?) {
        if (itemsDto != null && warehouseStockDetails != null) {
            if (itemsDto.ItemPartCode != null && warehouseStockDetails.WarehouseID > 0) {
                getWarehouseSerialNosList(itemsDto.ItemPartCode, warehouseStockDetails.WarehouseID)
            }

        }
    }

    private fun getWarehouseSerialNosList(itemPartCode: String, warehouseId: Long) {
        showProgressIndicator()
        //{"ItemPartCode":"18x085NiCd-Hop"}
        val request = WarehouseSerialItemsRequestDto(
            ItemPartCode = "18x085NiCd-Hop",
//            ItemSerialNumber = "",
            WarehouseID = warehouseId
        )
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            itemStocksRepo.getWarehouseSerialNosList(request)
                .collect { dto ->
                    Log.v("WMS EXERT", "getWarehouseSerialNosList response $dto")
                    hideProgressIndicator()
                    if (dto.success && dto.Items.isNotEmpty()) {
                        val warehouseList = dto.Items[0].wStockDetails
                        if (warehouseList != null && warehouseList.isNotEmpty() && warehouseList[0].wSerialItemDetails != null) {
                            warehouseList[0].wSerialItemDetails?.let {
                                _warehouseSerialIsList.postValue(it)
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
                                R.string.error_warehouse_serials_nos_message
                            )
                        )
                    }
                }
        }

    }
}