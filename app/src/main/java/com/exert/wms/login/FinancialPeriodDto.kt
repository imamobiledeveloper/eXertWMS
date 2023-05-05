package com.exert.wms.login

import androidx.annotation.Keep

@Keep
data class FinancialPeriodDto(
    val success:Boolean,
    val Period:List<PeriodDto>,
    val CurrentPeriod:Long
){
    companion object
}

@Keep
data class PeriodDto(
    val PeriodID:Long,
    val Period:String
){
    companion object
}

//{"success":true,"Period":[{"PeriodID":1,"Period":"  01 Jan 18 - 30 Jun 18"},{"PeriodID":2,"Period":" 01 Jul 18 - 30 Jun 19"},{"PeriodID":3,"Period":" 01 Jul 19 - 30 Jun 20"},{"PeriodID":4,"Period":"  01 Jul 20 - 30 Jun 21"},{"PeriodID":5,"Period":" 01 Jul 21 - 30 Jun 22"},{"PeriodID":6,"Period":"01 Jul 22 - 30 Jun 23"}],"CurrentPeriod":6}
