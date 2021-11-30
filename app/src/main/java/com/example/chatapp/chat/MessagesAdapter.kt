package com.example.chatapp.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ListUserBinding
import com.example.chatapp.databinding.ReceiveBinding
import com.example.chatapp.databinding.SentBinding
import com.example.chatapp.interfaces.Delegates
import com.example.chatapp.models.Message
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2


//    private var list = listOf<Message>()
//    fun setMessage(list: List<Message>) {
//        this.list = list
//        notifyDataSetChanged()
//    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = SentBinding.bind(itemView)
        fun bind(message: Message) = with(binding){
            sentMessage.text = message.text
        }
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ReceiveBinding.bind(itemView)
        fun bind(message: Message) = with(binding){
            receivedMessage.text = message.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view = LayoutInflater.from((parent.context)).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        } else{
            val view = LayoutInflater.from((parent.context)).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            viewHolder.bind(messageList[position])
        } else {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.bind(messageList[position])
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    //    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val binding = ListUserBinding.bind(itemView)
//
//        fun bind(message: Message) = with(binding){
//            txtView.text = message.text
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
//        val view = LayoutInflater.from((parent.context)).inflate(R.layout.list_user, parent, false)
//        return ItemHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
//        holder.bind(list[position])
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }

}