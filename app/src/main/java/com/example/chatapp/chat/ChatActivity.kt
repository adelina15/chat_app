package com.example.chatapp.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.models.Chat
import com.example.chatapp.models.Message
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private var chat: Chat? = null
    private var user: User? = null

    private val messageList: MutableList<Message> = ArrayList<Message>()
    private val adapter by lazy { MessagesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        user = intent.getSerializableExtra("user") as User?
        chat = intent.getSerializableExtra("chat") as Chat?
        if (chat == null) {
            chat = Chat()
            val userIds = ArrayList<String>()
            userIds.add(FirebaseAuth.getInstance().uid.toString())
            userIds.add(user!!.id)
            chat!!.userIds = userIds
        } else {
            init()
            getMessages()
        }

        binding.sendButton.setOnClickListener {
            onClickSend()
        }
    }

    private fun getMessages() {
        FirebaseFirestore.getInstance().collection("chats").document(chat?.id.toString())
            .collection("messages")
            .addSnapshotListener { value, error ->
                for (change: DocumentChange in value!!.documentChanges){
                    when(change.type){
                        DocumentChange.Type.ADDED -> messageList.add(change.document.toObject(Message::class.java))
                    }
                }
            }
    }

    private fun init() {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ChatActivity)
            rcView.adapter = adapter
            adapter.setMessage(messageList)
        }
    }

    private fun onClickSend() {
        val messageText = binding.message.text.toString()
        if (chat?.id != null) {
            sendMessage(messageText)
        } else {
            createChat(messageText)
        }
    }

    private fun createChat(text: String) {
        FirebaseFirestore.getInstance().collection("chats").add(chat!!).addOnSuccessListener {
            chat?.id = it.id
            sendMessage(text)
            Toast.makeText(applicationContext, "send message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage(text: String) {
        val map = hashMapOf<String, Any>()
        map["text"] = text
        FirebaseFirestore.getInstance().collection("chats")
            .document(chat?.id!!)
            .collection("messages")
            .add(map)
    }

}