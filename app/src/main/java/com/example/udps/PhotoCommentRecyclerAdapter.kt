package com.example.udps

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.util.concurrent.Executors


internal class PhotoCommentRecyclerAdapter(data: OrderedRealmCollection<photoCommentItem>): RealmRecyclerViewAdapter<photoCommentItem, PhotoCommentRecyclerAdapter.ViewHolder?>(data, true) {
    lateinit var _parent:ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val user = UDPSApp.currentUser()
        var v:View =  LayoutInflater.from(parent.context).inflate(R.layout.photo_comment_cardview, parent, false)

        _parent = parent
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: PhotoCommentRecyclerAdapter.ViewHolder, position: Int) {
        Log.e("bindviewholder position", "position: $position")
        val obj:photoCommentItem? = getItem(position)
        holder.data = obj
        holder.senderTxt.text = obj?.senderSname
        holder.timeTxt.text = obj?.time
        if(obj?.message!="")holder.messageTxt.text = obj?.message
        holder.sender = obj?.sender
        if(obj?.image!="") {
            val imageURL = obj?.image
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null
            executor.execute {
                //val imageURL = obj?.image
                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        holder.messageImg.setImageBitmap(image)
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //DownloadImageFromInternet(holder.messageImg).execute(obj?.image)
            holder.messageImg.visibility=View.VISIBLE
            holder.messageImg.isClickable=true
            holder.messageImg.setOnClickListener(){
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(imageURL)
                holder.messageImg.context.startActivity(openURL)


            }
        }


    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var data: photoCommentItem? = null
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