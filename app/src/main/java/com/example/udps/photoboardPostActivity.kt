package com.example.udps

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import org.w3c.dom.Text
import java.util.concurrent.Executors

class photoboardPostActivity : AppCompatActivity() {
    lateinit var galleryBtn:Button
    lateinit var cameraBtn:Button
    lateinit var postBtn:Button
    lateinit var previewImg:ImageView
    lateinit var titleTA:EditText

    lateinit var url:String
    lateinit var placeholderTxt:TextView
    var mode=0


    val resultContract=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result?.resultCode == Activity.RESULT_OK) {
            placeholderTxt.text = "got image: $result"
            val data = result.data
            url = data?.getStringExtra("url").toString()
            Log.e("photoboardpost", "Photo recieved:${url.toString()}")
            if (url != null) {
                val imageURL = url
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                var image: Bitmap? = null
                executor.execute {
                    //val imageURL = obj?.image
                    try {
                        val `in` = java.net.URL(imageURL).openStream()
                        image = BitmapFactory.decodeStream(`in`)
                        handler.post {
                            previewImg.setImageBitmap(image)
                            placeholderTxt.visibility= View.GONE
                            postBtn.isEnabled=true
                            mode=2
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                placeholderTxt.text = "didnt get image"

            }

        }
    }
    val loadImg = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if (it!=null) {
                previewImg.setImageURI(it)
                placeholderTxt.visibility=View.GONE
                postBtn.isEnabled=true
                mode=1
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoboard_post)
        galleryBtn = findViewById<Button>(R.id.postGallery)
        cameraBtn = findViewById<Button>(R.id.postCamera)
        postBtn = findViewById<Button>(R.id.postBtn)
        previewImg = findViewById<ImageView>(R.id.postImg)
        titleTA = findViewById<EditText>(R.id.postTitle)
        placeholderTxt = findViewById<TextView>(R.id.postTxtPlaceholder)

        galleryBtn.setOnClickListener(){
            selectFromGallery()
        }
        cameraBtn.setOnClickListener(){
            selectFromCamera()
        }
        postBtn.setOnClickListener(){
            makePost()
        }
        postBtn.isEnabled=false

    }

    private fun makePost() {
        TODO("Not yet implemented")
    }

    private fun selectFromCamera() {
        val intent = Intent(this, cameraActivity::class.java).apply {
            putExtra("source", "post")
            putExtra("account", "null")
            //putExtra("account", account)
        }
        resultContract.launch(intent)

    }

    private fun selectFromGallery() {
        loadImg.launch("image/*")

    }
}