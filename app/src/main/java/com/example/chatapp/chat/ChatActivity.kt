package com.example.chatapp.chat


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.models.Chat
import com.example.chatapp.models.Message
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Ref
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private var chat: Chat? = null
    private var user: User? = null
    private val myId: String = FirebaseAuth.getInstance().uid.toString()
    private val messageList = ArrayList<Message>()
    private val adapter by lazy { MessagesAdapter(this, messageList) }

//    val userIds = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        user = intent.getSerializableExtra("user") as User?
        chat = intent.getSerializableExtra("chat") as Chat?

        supportActionBar?.title = user?.name

//        userIds.add(FirebaseAuth.getInstance().uid.toString())
//        userIds.add(user!!.id)
//
//        FirebaseFirestore.getInstance().collection("chats")
//            .whereArrayContains("userIds", userIds)

        if (chat == null) {
            chat = Chat()
            val userIds = ArrayList<String>()
            userIds.add(FirebaseAuth.getInstance().uid.toString())
            userIds.add(user!!.id)
            chat?.userIds = userIds
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
                for (change in value!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> messageList.add(
                            change.document.toObject(Message::class.java)
                        )
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun init() {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ChatActivity)
            rcView.adapter = adapter
//            adapter.setMessage(messageList)
        }
    }

    private fun onClickSend() {
        val messageText = binding.message.text.toString()
        if (chat?.id == null) {
            createChat(messageText)
        } else {
            sendMessage2(messageText)
        }
    }

    private fun createChat(text: String) {
        FirebaseFirestore.getInstance().collection("chats").add(chat!!).addOnSuccessListener {
            chat?.id = it.id
            Toast.makeText(applicationContext, "send message in a new chat", Toast.LENGTH_SHORT)
                .show()
            sendMessage2(text)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun sendMessage(text: String) {
        val map = hashMapOf<String, Any>()
        map["text"] = text
        map["senderId"] = myId
        FirebaseFirestore.getInstance().collection("chats")
            .document(chat?.id!!)
            .collection("messages")
            .add(map)
        Toast.makeText(applicationContext, "send message in existing chat", Toast.LENGTH_SHORT)
            .show()
    }

    private fun sendMessage2(text: String) {
        Log.i("MyTag", "SendMessage")
        var map: MutableMap<String, Any> = HashMap()
        map["text"] = text
        map["senderId"] = myId
//        map["isRead"] = false
//        map["time"] = Calendar.getInstance().timeInMillis

        FirebaseFirestore.getInstance().collection("chats").document(chat?.id!!)
            .collection("messages")
            .add(map)
        binding.message.setText("")
    }
}