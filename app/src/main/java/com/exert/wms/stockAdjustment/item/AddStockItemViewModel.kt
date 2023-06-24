package com.exert.wms.stockAdjustment.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.R
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.SerialItemsDto
import com.exert.wms.utils.StringProvider

class AddStockItemViewModel(private val stringProvider: StringProvider) : BaseViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _serialItem = MutableLiveData<SerialItemsDto>()
    val serialItem: LiveData<SerialItemsDto> = _serialItem

    private var warrantyPeriod: String = ""
    private var warrantyPeriodList: List<String>? = null
    private var selectedYear: Int=0
    private var selectedMonth: Int=0
    private var selectedDay: Int=0

    private fun validateNewItemDetails(
        manufacture: String,
        warranty: String?,
        serialNum: String
    ): Boolean {
        return if (manufacture.isEmpty()) {
            _errorMessage.postValue(stringProvider.getString(R.string.error_manufaturer_empty_message))
            false
        } else if (warranty.isNullOrEmpty() || warranty == stringProvider.getString(R.string.select_warranty_period)) {
            _errorMessage.postValue(stringProvider.getString(R.string.error_warranty_empty_message))
            false
        } else if (serialNum.isEmpty()) {
            _errorMessage.postValue(stringProvider.getString(R.string.error_serial_number_empty_message))
            false
        } else {
            true
        }
    }

    fun addItemDetails(
        manufacture: String,
        warranty: String?,
        serialNum: String
    ) {
        if (validateNewItemDetails(manufacture, warranty, serialNum)) {
            val item = SerialItemsDto(
                SerialNumber = serialNum,
                ManufactureDate = manufacture,
                WarrantyPeriod = warranty,
                0
            )
            _serialItem.postValue(item)
        }
    }

    fun setWarrantyPeriod(warranty: String) {
        if (warranty != stringProvider.getString(R.string.select_warranty_period)) {
            warrantyPeriod = warranty
        }
    }

    fun getSelectedWarrantyPeriodIndex(): Int {
        return warrantyPeriodList?.let {
            it.indexOf(warrantyPeriod)
        } ?: 0
    }

    fun setWarrantyPeriodList(yearsList: List<String>) {
        warrantyPeriodList = yearsList
    }

    fun setSelectedDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedYear=year
        selectedMonth=monthOfYear
        selectedDay=dayOfMonth
    }

    fun getSelectedYear()=selectedYear
    fun getSelectedMonth()=selectedMonth
    fun getSelectedDayOfMonth()=selectedDay
}