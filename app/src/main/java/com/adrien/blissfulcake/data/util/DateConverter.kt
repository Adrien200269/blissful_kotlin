package com.adrien.blissfulcake.data.util

import java.util.Date

object DateConverter {
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    fun dateToTimestamp(date: Date?): Long? = date?.time
} 