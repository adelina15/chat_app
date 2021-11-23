package com.example.chatapp.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ListUserBinding
import com.example.chatapp.interfaces.Delegates
import com.example.chatapp.models.Message
import com.example.chatapp.models.User

class MessagesAdapter(): RecyclerView.Adapter<MessagesAdapter.ItemHolder>()  {

    private var list = listOf<Message>()
    fun setMessage(list: List<Message>){
        this.list = list
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListUserBinding.bind(itemView)

        fun bind(message: Message) = with(binding){
            txtView.text = message.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from((parent.context)).inflate(R.layout.list_user, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}