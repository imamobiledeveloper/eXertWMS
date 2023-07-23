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

    private val _warehouseSerialIsList = MutableLiveData<List<WarehouseSerialItemDetails>?>()
    val warehouseSerialIsList: LiveData<List<WarehouseSerialItemDetails>?> = _warehouseSerialIsList

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

    private val _errorFieldMessage = MutableLiveData<String>()
    val errorFieldMessage: LiveData<String> = _errorFieldMessage

    private val _itemBarCodeData = MutableLiveData<ItemsBarCodeDto>()
    val itemBarCodeData: LiveData<ItemsBarCodeDto> = _itemBarCodeData

    var itemPartCode: String = ""
    var itemSerialNo: String = ""
    var itemsDto: ItemsDto? = null

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
                            _enableStatusButton.postValue(true)
                        } else {
                            _errorGetItemsStatusMessage.postValue(stringProvider.getString(R.string.empty_items_list))
                            _enableStatusButton.postValue(false)
                        }
                    } else {
                        _getItemsStatus.value = (false)
                        _enableStatusButton.postValue(false)
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

    fun setItemPartCodeValue(partCode: String) {
        itemPartCode = partCode
    }

    fun searchItemWithPartCode() {
        if (itemPartCode.isNotEmpty()) {
            getItemWarehouseList(itemPartCode, "")
            _errorItemPartCode.postValue(false)
        } else {
            _errorItemPartCode.value = (true)
        }
        checkAndEnableStatusButton()
    }

    fun setItemSerialNumberValue(serialNo: String) {
        itemSerialNo = serialNo
    }

    fun searchItemWithSerialNumber() {
        if (itemSerialNo.isNotEmpty()) {
            getItemWarehouseList("", itemSerialNo)
            _errorItemSerialNo.postValue(false)
        } else {
            _errorItemSerialNo.value = (true)
        }
        checkAndEnableStatusButton()
    }

    private fun checkAndEnableStatusButton() {
        if (validateUserDetails(itemPartCode, itemSerialNo)) {
            _errorItemPartCode.postValue(false)
            _errorItemSerialNo.postValue(false)
        } else {
            _enableStatusButton.postValue(false)
        }
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
            if (warehouseStockDetails.wSerialItemDetails != null && warehouseStockDetails.wSerialItemDetails.isNotEmpty()) {
                _warehouseSerialIsList.postValue(warehouseStockDetails.wSerialItemDetails)
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
                    Log.v("WMS EXERT", "getWarehouseSerialNosList response $dto")
                    hideProgressIndicator()

                    if (dto.success && dto.Items != null && dto.Items.isNotEmpty()) {
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
                                R.string.error_failed_warehouse_serials_nos_message
                            )
                        )
                    }
                }
        }

    }

    fun setItemPartCodeAndSerialNo(partCode: String?, serialNo: String?) {
        if (partCode != null) {
            itemPartCode = partCode
        }
        if (serialNo != null) {
            itemSerialNo = serialNo
        }
    }

    fun setBarCodeData(barCode: String) {
        if(isItPartCodeScanRequest()){
            setItemPartCodeValue(barCode)
            val itemBarCodeDto=ItemsBarCodeDto(isItItemPartCode = true, ItemPartCodeData=barCode, ItemSerialNoData="")
            _itemBarCodeData.postValue(itemBarCodeDto)
        }else{
            setItemSerialNumberValue(barCode)
            val itemBarCodeDto=ItemsBarCodeDto(isItItemPartCode = false, ItemPartCodeData="", ItemSerialNoData=barCode)
            _itemBarCodeData.postValue(itemBarCodeDto)
        }
    }
}