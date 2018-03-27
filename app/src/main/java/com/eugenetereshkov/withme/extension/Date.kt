package com.eugenetereshkov.withme.extension

import com.eugenetereshkov.withme.Constants
import java.text.SimpleDateFormat


fun String.timeDifferent(): String {
    val startDate = SimpleDateFormat("dd-MM-yyyy HH").parse(Constants.START_DATE)
    val startMillis = startDate.time
    val nowMillis = System.currentTimeMillis()
    val diff = nowMillis - startMillis

    val diffSeconds = diff / 1000 % 60
    val diffMinutes = diff / (60 * 1000) % 60
    val diffHours = diff / (60 * 60 * 1000) % 24
    val diffDays = diff / (24 * 60 * 60 * 1000)

    val time = "%1\$02d:%2\$02d:%3\$02d".format(diffHours, diffMinutes, diffSeconds)

    return "$diffDays ${diffDays.toInt().dayAddition()}\n$time"
}

fun Int.dayAddition(): String {
    val preLastDigit = this % 100 / 10

    if (preLastDigit == 1) {
        return "дней"
    }

    return when (this % 10) {
        1 -> "день"
        2, 3, 4 -> "дня"
        else -> "дней"
    }
}