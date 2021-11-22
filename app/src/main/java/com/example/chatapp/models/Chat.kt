package com.example.chatapp.models

import java.io.Serializable

data class Chat(var id: String? = null, var userIds: List<String> = listOf()) : Serializable