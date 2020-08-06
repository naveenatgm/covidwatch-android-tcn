package org.covidwatch.android.presentation.util

import android.app.DatePickerDialog
import android.telephony.PhoneNumberUtils
import androidx.fragment.app.Fragment
import java.util.*

fun Fragment.showDatePicker(callback: (date: Date) -> Unit) {
    val calendar = Calendar.getInstance()
    val dialog = DatePickerDialog(
        requireContext(),
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            callback(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    dialog.datePicker.maxDate = Date().time

    dialog.show()
}

fun Fragment.validateContactNumber(phoneNum: String): Boolean {

    var reg = "^\\(?\\d{3}\\)? ?\\d{3}-?\\d{4}$".toRegex()  //"\\(?\\d{3}\\)?\\-?\\d{3}\\-\\d{4}".toRegex()
    val formattedPhoneNum = formatContactNumber(phoneNum)
    return formattedPhoneNum.matches(reg)
}

fun Fragment.formatContactNumber(phoneNum: String): String {
    // TODO: IS It a US Only APP ??? Sukhdev validate the phone number based on country????
    return if (PhoneNumberUtils.formatNumber(phoneNum, Locale.getDefault().country) != null) {
        PhoneNumberUtils.formatNumber(phoneNum,Locale.getDefault().country)
    }else {
        "No contact number saved"
    }

}