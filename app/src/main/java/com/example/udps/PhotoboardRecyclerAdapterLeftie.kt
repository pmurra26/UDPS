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
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.util.concurrent.Executors


internal class PhotoboardRecyclerAdapterLeftie(data: OrderedRealmCollection<photoPostItem>): RealmRecyclerViewAdapter<photoPostItem, PhotoboardRecyclerAdapterLeftie.ViewHolder?>(data, true) {
    lateinit var _parent:ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val user = UDPSApp.currentUser()
        var v:View = LayoutInflater.from(parent.context).inflate(R.layout.photoboard_image_card, parent, false)
        _parent = parent
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: PhotoboardRecyclerAdapterLeftie.ViewHolder, position: Int) {
        Log.e("bindviewholder position", "position: $position")
        val obj:photoPostItem? = getItem(position)
        holder.data = obj
        val senderSname = obj?.senderSname
        val time = obj?.time
        val title = obj?.message
        val sender = obj?.sender
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
                        holder.postImg.setImageBitmap(image)
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //DownloadImageFromInternet(holder.messageImg).execute(obj?.image)
            holder.postImg.setOnClickListener(){
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(imageURL)
                holder.postImg.context.startActivity(openURL)


            }
        }


    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var data: photoPostItem? = null
        var postImg: ImageView = itemView.findViewById(R.id.card_image)

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