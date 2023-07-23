package com.exert.wms.addItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.Constants
import com.exert.wms.utils.StringProvider
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddStockItemViewModel(private val stringProvider: StringProvider) : BaseViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _serialItem = MutableLiveData<SerialItemsDto>()
    val serialItem: LiveData<SerialItemsDto> = _serialItem

    private var warrantyPeriod: String = ""
    private var warrantyPeriodList: List<String>? = null
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedMonthString: String = ""
    private var selectedDay: Int = 0

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
                ManufactureDate = getFormattedDateFromDate(manufacture),
                WarrantyPeriod = getWarrantyNumber(warranty),
                0
            )
            _serialItem.postValue(item)
        }
    }

    private fun getFormattedDateFromDate(manufacture: String): String? {
        val input = SimpleDateFormat(Constants.MANUFACTURE_DATE_FORMAT)
        val output = SimpleDateFormat(Constants.MANUFACTURE_DATE_API_FORMAT)
        try {
            val getAbbreviate = input.parse(manufacture)    // parse input
            return output.format(getAbbreviate)    // format output
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getWarrantyNumber(warrantyPeriod: String?): String? {
        warrantyPeriod?.let { str ->
            if (str.contains("Year") || str.contains("Years")) {
                return str.filter { it.isDigit() }
            }
        }
        return warrantyPeriod
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
        selectedYear = year
        selectedMonth = monthOfYear
        selectedDay = dayOfMonth
        selectedMonthString = getSelectedMonthInString(year, monthOfYear + 1, dayOfMonth)
    }

    private fun getSelectedMonthInString(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val monthFormat =
            SimpleDateFormat(Constants.MONTH) // Use "MMM" for abbreviated month (e.g., Jan)
        return monthFormat.format(calendar.time)
    }

    fun getSelectedYear() = selectedYear
    fun getSelectedMonth() = selectedMonth
    fun getSelectedDayOfMonth() = selectedDay
    fun getMonthIn2Digits(monthOfYear: Int): String {
        val month = monthOfYear + 1
        if (month < 10) {
            return String.format("%02d", month)
        }
        return month.toString()
    }
}