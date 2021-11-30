package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.chat.ChatActivity
import com.example.chatapp.contacts.ContactsActivity
import com.example.chatapp.contacts.ContactsAdapter
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.interfaces.Delegates
import com.example.chatapp.models.Chat
import com.example.chatapp.models.Message
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), Delegates.RecyclerItemClicked {
    lateinit var binding: ActivityMainBinding
    private val chatList: MutableList<Chat> = ArrayList()
    private val adapter by lazy { ChatAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        binding.floatingActionButton.setOnClickListener{
            startActivity(Intent(this, ContactsActivity::class.java))
        }
        initList()
        getChats()
    }

    private fun getChats() {
        val uid = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection("chats")
            .whereArrayContains("userIds", uid.toString())
            .addSnapshotListener { value, error ->
                for (change: DocumentChange in value!!.documentChanges){
                    when(change.type){
                        DocumentChange.Type.ADDED -> run {
                            val chat: Chat = change.document.toObject(Chat::class.java)
                            chat.id = change.document.id
                            chatList.add(chat)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
//            .addOnSuccessListener {
//                for (snapshot in it){
//                    val chat: Chat? = snapshot.toObject(Chat::class.java)
//                    if(chat != null) {
//                        chat.id = snapshot.id
//                    }
//                    chatList.add(chat!!)
//                }
//                adapter.notifyDataSetChanged()
//
//            }
    }

    private fun initList() {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.adapter = adapter
            adapter.setUser(chatList)
        }

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chat", chatList[position])
        startActivity(intent)
    }
}