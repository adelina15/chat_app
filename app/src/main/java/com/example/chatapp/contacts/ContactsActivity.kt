package com.example.chatapp.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityContactsBinding
import com.example.chatapp.interfaces.Delegates
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.text.FieldPosition

class ContactsActivity : AppCompatActivity(), Delegates.RecyclerItemClicked {
    lateinit var binding: ActivityContactsBinding
    private val userList: MutableList<User> = ArrayList()
    private val adapter by lazy { ContactsAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)
        init()
        getContacts()
    }

    private fun init() {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ContactsActivity)
            rcView.adapter = adapter
            adapter.setUser(userList)
        }
    }

    private fun getContacts(){
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener {
                for (snapshot in it){
                    val user: User? = snapshot.toObject(User::class.java)
                    if(user != null) {
                        user.id = snapshot.id
                    }
                    userList.add(user!!)
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("user", userList[position])
        startActivity(intent)
    }
}