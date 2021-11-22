package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.models.Chat
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chat: Chat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        val user: User = intent.getSerializableExtra("user") as User

        val userIds = ArrayList<String>()
        userIds.add(FirebaseAuth.getInstance().uid.toString())
        userIds.add(user.id)
        chat = Chat(userIds = userIds)
        binding.sendButton.setOnClickListener{
            onClickSend()
        }
    }

    private fun onClickSend(){
        val messageText = binding.message.text.toString()
        if (chat.id != null) {
            sendMessage(messageText)
        }
        else{
            createChat(messageText)
        }
    }

    private fun createChat(text: String) {
        FirebaseFirestore.getInstance().collection("chats").add(chat).addOnSuccessListener {
            chat.id = it.id
            sendMessage(text)
            Toast.makeText(applicationContext, "send message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage(text: String){
        val map = hashMapOf<String, Any>()
        map["text"] = text
        FirebaseFirestore.getInstance().collection("chats")
            .document(chat.id!!)
            .collection("messages")
            .add(map)
    }

}