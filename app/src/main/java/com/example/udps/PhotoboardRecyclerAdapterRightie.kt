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
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.util.concurrent.Executors


internal class PhotoboardRecyclerAdapterRightie(data: OrderedRealmCollection<photoPostItem>): RealmRecyclerViewAdapter<photoPostItem, PhotoboardRecyclerAdapterRightie.ViewHolder?>(data, true) {
    lateinit var _parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val user = UDPSApp.currentUser()
        var v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.photoboard_image_card, parent, false)
        _parent = parent
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: PhotoboardRecyclerAdapterRightie.ViewHolder,
        position: Int
    ) {
        if (position % 2 == 1) {
            Log.e("bindviewholder position", "position: $position")
            val obj: photoPostItem? = getItem(position)
            holder.data = obj
            val senderSname = obj?.senderSname
            val time = obj?.time
            val title = obj?.message
            val sender = obj?.sender
            if (obj?.image != "") {
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
                            holder.postImg.setImageBitmap(image)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //DownloadImageFromInternet(holder.messageImg).execute(obj?.image)
                holder.postImg.setOnClickListener() {
                    val intent =
                        Intent(holder.postImg.context, photoCommentActivity::class.java).apply {
                            putExtra("shortName", senderSname)
                            putExtra("time", time)
                            putExtra("title", title)
                            putExtra("image", imageURL)
                        }
                    holder.postImg.context.startActivity(intent)
                }
            }
        } else holder.container.visibility = View.GONE


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: photoPostItem? = null
        var postImg: ImageView = itemView.findViewById(R.id.card_image)
        var container: CardView = itemView.findViewById(R.id.icard_view)

    }

}