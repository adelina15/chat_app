package com.example.chatapp.models

import com.google.firebase.Timestamp
import java.io.Serializable

data class Message(var text: String = "", var time: Long = 0L, val isSeen: Boolean = false, var senderId: String= ""): Serializable {
}