package com.example.chatapp.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.models.Chat
import com.example.chatapp.models.Message
import com.example.chatapp.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private var chat: Chat? = null
    private var user: User? = null
    private val myId = FirebaseAuth.getInstance().uid
    private val messageList = ArrayList<Message>()
    private val adapter by lazy { MessagesAdapter(this) }
    private var chatExist: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        user = intent.getSerializableExtra("user") as User?
        chat = intent.getSerializableExtra("chat") as Chat?

        supportActionBar?.title = user?.name

        if (chat == null) {
            Toast.makeText(this, "chat == null", Toast.LENGTH_SHORT).show()
            val userIds: ArrayList<String> = ArrayList()
            userIds.add(user?.id.toString())
            userIds.add(myId.toString())
            Log.i("TAG", "User ids $userIds")
            if (!exist(userIds)) {
                Toast.makeText(this, "userIds !exist", Toast.LENGTH_SHORT).show()
                chat = Chat()
                chat!!.userIds = userIds
                supportActionBar?.title = user?.name
            }
        } else if (user == null) {
            Toast.makeText(this, "user == null", Toast.LENGTH_SHORT).show()
            isSeen()
            getMessages()
            init()
            FirebaseFirestore.getInstance().collection("users")
                .document(chat?.userIds?.get(0).toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    user = documentSnapshot.toObject(User::class.java)
                    assert(user != null)
                    supportActionBar?.title = user?.name
                }
        }

        binding.sendButton.setOnClickListener {
            onClickSend()
        }

    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun exist(userIds: ArrayList<String>): Boolean {
        chatExist = false
        FirebaseFirestore.getInstance().collection("chats")
            .whereEqualTo("userIds", userIds)
            .get()
            .addOnSuccessListener { snapshots ->
                Log.i("TAG", "success: $chatExist ")
                if (snapshots != null) {
                    chatExist = true
                    Log.i("TAG", "exist true $chatExist")
                    for (snapshot in snapshots) {
                        chat = snapshot.toObject(Chat::class.java)
                        chat?.id = snapshot.id
                    }
                }
            }
            .addOnFailureListener {
                chatExist = false
                Log.i("TAG", "failure: $chatExist ")
            }
        Log.i("TAG", "end :$chatExist ")
        return chatExist
    }

    private fun isSeen() {
        val ids = chat?.userIds as MutableList
        ids.remove(myId)
        val userName = ids[0]

        FirebaseFirestore.getInstance().collection("chats")
            .document(chat?.id.toString())
            .collection("messages")
            .whereEqualTo("senderId", userName)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (doc in it.result!!) {
                        doc.reference.update("isSeen", true)
                    }
                }
            }
    }

    private fun getMessages() {
        FirebaseFirestore.getInstance().collection("chats").document(chat?.id.toString())
            .collection("messages")
            .orderBy("time")
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
            adapter.setMessage(messageList)
        }
    }

    private fun onClickSend() {
        val messageText = binding.message.text.toString()
        if (chat?.id == null) {
            createChat(messageText)
        } else {
            sendMessage(messageText)
        }
    }


    private fun createChat(text: String) {
        Toast.makeText(this, "created chat", Toast.LENGTH_SHORT).show()
        val map: MutableMap<String, Any> = HashMap()
        map["userIds"] = chat?.userIds!!
        FirebaseFirestore.getInstance().collection("chats")
            .add(map)
            .addOnSuccessListener { documentReference ->
                chat?.id = (documentReference.id)
                sendMessage(text)
                getMessages()
                init()
                startActivity(Intent(this, MainActivity::class.java))
            }
    }

    private fun sendMessage(text: String) {
        Toast.makeText(this, "send message", Toast.LENGTH_SHORT).show()
        val map: MutableMap<String, Any> = HashMap()
        map["text"] = text
        map["senderId"] = myId.toString()
        map["isSeen"] = false
        map["time"] = Calendar.getInstance().timeInMillis

        FirebaseFirestore.getInstance().collection("chats").document(chat?.id!!)
            .collection("messages")
            .add(map)
        binding.message.setText("")
        binding.rcView.scrollToPosition(adapter.itemCount - 1)
    }
}