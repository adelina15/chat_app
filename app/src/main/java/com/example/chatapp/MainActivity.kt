package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.chatapp.contacts.ContactsActivity
import com.example.chatapp.contacts.ContactsAdapter
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.floatingActionButton.setOnClickListener{
            startActivity(Intent(this, ContactsActivity::class.java))
        }
    }
}