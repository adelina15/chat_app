package com.example.chatapp.models

import com.google.firebase.Timestamp
import java.io.Serializable

data class Message(var text: String = "", var time: Timestamp = Timestamp.now(), var senderId: String= ""): Serializable {
}