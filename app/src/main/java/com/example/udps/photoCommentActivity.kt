package com.example.udps

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors

class photoCommentActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private var user: User? = null
    private lateinit var adapter: PhotoCommentRecyclerAdapter
    private lateinit var commentRecyclerView: RecyclerView

    lateinit var post:String

    lateinit var storage: FirebaseStorage


    val loadImg = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if (it!=null) {
                storage = Firebase.storage
                val name = user?.customData?.get("shortName").toString()+ SimpleDateFormat(
                    cameraActivity.FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/UDPS-image")
                    }
                }
                var storageRef = storage.reference
                var file = it
                val photoRef = storageRef.child("images/${file.lastPathSegment}")
                val uploadTask = photoRef.putFile(file)
                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    photoRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.e("message from gallery", "Photo upload succeded, url: ${downloadUri}")

                        val timeRaw = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")
                        val formatted = timeRaw.format(formatter)
                        var toInsert = photoCommentItem(
                            user!!.id,
                            user!!.customData!!.get("shortName")!!.toString(),
                            formatted,
                            "",
                            downloadUri.toString(),
                            post
                        )
                        realm.executeTransactionAsync { realm ->
                            realm.insert(toInsert)
                        }
                    }
                }
            }
        })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_comment)
    }

    override fun onResume() {
        super.onResume()
        val name = findViewById<TextView>(R.id.photoCommentSname)
        name.text = intent.getStringExtra("shortName").toString()
        val date = findViewById<TextView>(R.id.photoCommentDate)
        date.text = intent.getStringExtra("time").toString()
        val title = findViewById<TextView>(R.id.photoCommentTitle)
        title.text = intent.getStringExtra("title").toString()
        if (intent.getStringExtra("title").toString() == " ") title.visibility = View.GONE

        post = intent.getStringExtra("shortName").toString()+intent.getStringExtra("time").toString()


        val imageView = findViewById<ImageView>(R.id.photoCommentImg)
        //val date:String = intent.getStringExtra("recipient").toString()
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            //val imageURL = obj?.image
            try {
                val `in` = java.net.URL(intent.getStringExtra("image")).openStream()
                var image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imageView.setOnClickListener() {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(intent.getStringExtra("image"))
                imageView.context.startActivity(openURL)


            }
        }

        user = UDPSApp.currentUser()
        val test = SyncConfiguration.Builder(user!!, "test")
            .waitForInitialRemoteData()
            .build()
        Realm.setDefaultConfiguration(test)
        Realm.getInstanceAsync(test, object : Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@photoCommentActivity.realm = realm
                commentRecyclerView = findViewById(R.id.photoCommentRV)
                setUpCommentRecyclerView(realm, post)
            }
        })

        val sendButton = findViewById<Button>(R.id.buttonSend)
        val inputTA = findViewById<EditText>(R.id.TAcommentInput)
        sendButton.setOnClickListener{
            val timeRaw = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")
            val formatted = timeRaw.format(formatter)
            var toInsert = photoCommentItem(user!!.id,
                user!!.customData!!.get("shortName")!!.toString(), formatted, inputTA.text.toString(), "", post)
            realm.executeTransactionAsync { realm ->
                realm.insert(toInsert)

            }
            inputTA.text.clear()
        }

        /**
         * Changed buttonCamera to imagebuttonCamera to display camera icon
         */
        val imgCapture = findViewById<ImageButton>(R.id.imagebuttonCamera)
        //imgCapture.text = String(Character.toChars(0x1F4F7))
        imgCapture.setOnClickListener {
            val Intent = Intent(this, cameraActivity::class.java).apply {
                putExtra("source", "comment")
                putExtra("account", post)
                //putExtra("account", account)
            }
            startActivity(Intent)
        }

        /**
         * Changed buttonGallery to imagebuttonGaller to display gallery icon
         */
        val pickPicture = findViewById<ImageButton>(R.id.imagebuttonGallery)
        //pickPicture.text = String(Character.toChars(0x1F5BC))
        pickPicture.setOnClickListener {
            loadImg.launch("image/*")
            Log.e("pick button", "pickbuton pressed")
        }

    }
    private fun setUpCommentRecyclerView(realm: Realm, account:String) {
        // a recyclerview requires an adapter, which feeds it items to display.
        // Realm provides RealmRecyclerViewAdapter, which you can extend to customize for your application
        // pass the adapter a collection of Tasks from the realm
        // we sort this collection so that the displayed order of Tasks remains stable across updates
        adapter = PhotoCommentRecyclerAdapter(realm.where<photoCommentItem>().contains("post", account).sort("_id").findAll())
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = adapter
        commentRecyclerView.setHasFixedSize(true)
        //recyclerView.scrollToPosition(adapter.itemCount-1)
        //recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
