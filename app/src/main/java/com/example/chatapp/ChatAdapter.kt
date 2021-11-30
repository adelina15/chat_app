package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ListUserBinding
import com.example.chatapp.interfaces.Delegates
import com.example.chatapp.models.Chat
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatAdapter(private val itemClicker: Delegates.RecyclerItemClicked) :
    RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    private var list = listOf<Chat>()
    fun setUser(list: List<Chat>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListUserBinding.bind(itemView)

        fun bind(chat: Chat) {
            val ids = chat.userIds as ArrayList //save ids of users in this chat
            val userName = ids[0] //extract id of another user

            FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener {
                    for (snapshot in it) {
                        if (snapshot.id == userName) {
                            //find user by his id and get his name to be displayed
                            binding.txtView.text = snapshot["name"].toString()
                        }
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from((parent.context)).inflate(R.layout.list_user, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.layout.setOnClickListener {
            itemClicker.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}