package com.example.chatapp.models

import java.io.Serializable

data class Message(var text: String = "", var senderId: String= ""): Serializable {
}