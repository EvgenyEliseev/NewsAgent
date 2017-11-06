package com.eliseev.newsagent.util

import java.text.SimpleDateFormat
import java.util.Date

object DateFormatter {

	private val dateFormat = SimpleDateFormat.getInstance()

    @JvmStatic
	fun format(date: Date?) : String {
		if (date != null) {
			return dateFormat.format(date)
		}
		return ""
	}
}