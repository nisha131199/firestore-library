package com.dream.myfirestorecharlibrary.models

import com.dream.myfirestorecharlibrary.Constants.DATE_FORMAT
import com.dream.myfirestorecharlibrary.Constants.TIME_FORMAT
import com.dream.myfirestorecharlibrary.MessageType
import java.text.SimpleDateFormat

data class Message(
    val message: String = "",
    val messageBy: String? = null,
    var messageType: String = MessageType.TEXT.name,
    var poem: String? = null,
    val time: String? = SimpleDateFormat(TIME_FORMAT).format(System.currentTimeMillis()).toString(),
    val date: String? = SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis()).toString(),
    var imagePath: String? = null,
    var isPoemReading: Boolean = false,
)