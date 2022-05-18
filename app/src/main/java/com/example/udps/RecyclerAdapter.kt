package com.example.udps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesRecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.messageSent_cardview, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var senderTxt: TextView
        var timeTxt: TextView
        var messageTxt: TextView
        var messageImg: ImageView
        init {
            senderTxt = itemView.findViewById(R.id.senderTxt)
            timeTxt = itemView.findViewById(R.id.timeTxt)
            messageTxt = itemView.findViewById(R.id.messageTxt)
            messageImg = itemView.findViewById(R.id.messageImg)
        }
    }


}