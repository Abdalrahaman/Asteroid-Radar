package com.udacity.asteroidradar.database

import com.udacity.asteroidradar.util.Constants
import java.text.SimpleDateFormat
import java.util.*

private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(date)
}

fun getDateToday(): String {
    val calendar = Calendar.getInstance()
    return formatDate(calendar.time)
}

fun getDateSeventhDay(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
    return formatDate(calendar.time)
}