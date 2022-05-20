package com.example.udps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.*
import android.widget.*
import io.realm.Realm
import io.realm.kotlin.where
import org.bson.types.ObjectId


internal class MessageRecyclerAdapter(data: OrderedRealmCollection<messagesItem>): RealmRecyclerViewAdapter<messagesItem, MessageRecyclerAdapter.ViewHolder?>(data, true) {
    lateinit var _parent:ViewGroup


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val user = UDPSApp.currentUser()
        if(item?.sender==user?.id) return 1 //if you sent the message
        else if (item?.sender!=user?.id&&user?.id==item?.conversation) return 2 //if you didnt send the message AND you are the parent
        else if(item?.sender!=user?.id&&item?.sender==item?.conversation)return 2 //if you didnt send the message AND the parent sent the message
        else if(item?.sender!=user?.id&&item?.sender!=item?.conversation)return 3 //if you didnt send the message AND the parent didnt send the message
        else return 0

        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val user = UDPSApp.currentUser()
        var v:View = when(viewType){
            1-> LayoutInflater.from(parent.context).inflate(R.layout.message_sent_cardview, parent, false)
            2-> LayoutInflater.from(parent.context).inflate(R.layout.message_received_cardview, parent, false)
            3-> LayoutInflater.from(parent.context).inflate(R.layout.message_received2_cardview, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.message_sent_cardview, parent, false)
        }
        _parent = parent
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: MessageRecyclerAdapter.ViewHolder, position: Int) {
        val obj:messagesItem? = getItem(position)
        holder.data = obj
        holder.senderTxt.text = obj?.senderSname
        holder.timeTxt.text = obj?.time
        if(obj?.message!=null)holder.messageTxt.text = obj?.message
        holder.sender = obj?.sender
        if(obj?.image!=null) DownloadImageFromInternet(holder.messageImg).execute(obj?.image)


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
    //@SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            Log.e("dlpic", "started in background")
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                Log.e("dlpic", "done in background")
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
                Log.e("dlpic", "done in background")
            }
            return image
            Log.e("dlpic", "done in background")
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            Log.e("dlpic", "setting image")
        }
    }


}