package com.example.udps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import android.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import io.realm.Realm
import io.realm.kotlin.where
import org.bson.types.ObjectId


internal class MessageRecyclerAdapter(data: OrderedRealmCollection<messagesItem>): RealmRecyclerViewAdapter<messagesItem, MessageRecyclerAdapter.ViewHolder?>(data, true) {
    lateinit var _parent:ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val user = UDPSApp.currentUser()

        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_sent_cardview, parent, false)
        _parent = parent
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: MessageRecyclerAdapter.ViewHolder, position: Int) {
        val obj:messagesItem? = getItem(position)
        holder.data = obj
        holder.senderTxt.text = obj?.senderSname
        holder.timeTxt.text = obj?.time
        holder.messageTxt.text = obj?.message
        holder.sender = obj?.sender

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var data: messagesItem? = null
        var senderTxt: TextView
        var timeTxt: TextView
        var messageTxt: TextView
        var messageImg: ImageView
        var sender:String?
        init {
            senderTxt = itemView.findViewById(R.id.senderTxt)
            timeTxt = itemView.findViewById(R.id.timeTxt)
            messageTxt = itemView.findViewById(R.id.messageTxt)
            messageImg = itemView.findViewById(R.id.messageImg)
            sender = " "
        }
    }


}