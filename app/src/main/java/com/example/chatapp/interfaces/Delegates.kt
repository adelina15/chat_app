package com.example.chatapp.interfaces

import com.example.chatapp.models.User

interface Delegates {
    interface RecyclerItemClicked{
        fun onItemClick(position: Int)
    }
}