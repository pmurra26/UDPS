package com.example.udps

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import io.realm.Realm
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class photoboardPostActivity : AppCompatActivity() {
    lateinit var galleryBtn:Button
    lateinit var cameraBtn:Button
    lateinit var postBtn:Button
    lateinit var previewImg:ImageView
    lateinit var titleTA:EditText
    lateinit var room:String

    private var user: User?=null
    private lateinit var realm: Realm

    lateinit var url:String
    lateinit var placeholderTxt:TextView
    var mode=0
    lateinit var imageholder:Uri

    lateinit var storage: FirebaseStorage


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
                            postBtn.setOnClickListener(){
                                cameraPost()
                            }
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
                imageholder = it
                placeholderTxt.visibility=View.GONE
                postBtn.isEnabled=true
                postBtn.setOnClickListener(){
                    galleryPost()
                }
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

        room = intent.getStringExtra("room").toString()

        galleryBtn.setOnClickListener(){
            selectFromGallery()
        }
        cameraBtn.setOnClickListener(){
            selectFromCamera()
        }
        postBtn.isEnabled=false

        storage = Firebase.storage
        user = UDPSApp.currentUser()
        val test = SyncConfiguration.Builder(user!!, "test")
            .waitForInitialRemoteData()
            .build()
        Realm.setDefaultConfiguration(test)
        Log.e("photoboardpost", "attempt to create realm")

        Realm.getInstanceAsync(test, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                Log.e("photoboardpost", "realm created successfully")

                this@photoboardPostActivity.realm = realm
                //setUpRecyclerView(realm)
            }
        })

    }

    private fun cameraPost(){
        val timeRaw = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")
        val formatted = timeRaw.format(formatter)
        var toInsert = photoPostItem(
            user!!.id,
            user!!.customData!!.get("shortName")!!.toString(),
            formatted,
            titleTA.text.toString(),
            url,
            room
        )


        realm.executeTransactionAsync { realm ->
            realm.insert(toInsert)
        }

        finish()
    }
    private fun galleryPost() {
        postBtn.isEnabled=false
        val file = imageholder
        var storageRef = storage.reference
        val photoRef = storageRef.child("images/${file.lastPathSegment}")
        val uploadTask = photoRef.putFile(file)
        val urlTask = uploadTask.continueWithTask{ task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            photoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                url = task.result.toString()
                val timeRaw = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")
                val formatted = timeRaw.format(formatter)
                var toInsert = photoPostItem(
                    user!!.id,
                    user!!.customData!!.get("shortName")!!.toString(),
                    formatted,
                    titleTA.text.toString(),
                    url,
                    room
                )
                realm.executeTransactionAsync { realm ->
                    realm.insert(toInsert)
                }
                finish()
            }
        }
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