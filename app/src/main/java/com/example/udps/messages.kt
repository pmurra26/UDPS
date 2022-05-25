package com.example.udps

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmConfiguration

import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.sync.SyncConfiguration

import org.bson.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import java.text.SimpleDateFormat
import java.util.*


class messages : AppCompatActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var user: User? = null
    private lateinit var adapter: MessageRecyclerAdapter
    private lateinit var realm: Realm
    private lateinit var recyclerView: RecyclerView
    lateinit var account:String

    lateinit var storage: FirebaseStorage

    lateinit var test:SyncConfiguration


    var messageHistory = mutableListOf<Array<out Any>>(arrayOf("kerry", "10:50, 24/03", "test message from \"kerry\"", "text"),
        arrayOf("annie_mum", "10:52, 24/03", "test message from \"annie_mum\"", "text"),
        arrayOf("kerry", "10:50, 24/03", R.drawable.test_pic_01, "image"),
        arrayOf("annie_mum", "10:52, 24/03", R.drawable.test_pic_02, "image"))

    val loadImg = registerForActivityResult(ActivityResultContracts.GetContent(),
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
                        var toInsert = messagesItem(
                            user!!.id,
                            user!!.customData!!.get("shortName")!!.toString(),
                            formatted,
                            "",
                            downloadUri.toString(),
                            account
                        )
                        realm.executeTransactionAsync { realm ->
                            realm.insert(toInsert)
                        }
                    }
                }
            }
        })

    //override fun onStart() {
      //  super.onStart()


    //}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        println(savedInstanceState==null)
        val pickPicture = findViewById<Button>(R.id.buttonPictureSelect)
        pickPicture.setOnClickListener {
            loadImg.launch("image/*")
            Log.e("pick button", "pickbuton pressed")
        }

        user = UDPSApp.currentUser()
        test = SyncConfiguration.Builder(user!!, "test")
            .waitForInitialRemoteData()
            .build()

        Realm.setDefaultConfiguration(test)
        Realm.getInstanceAsync(test, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@messages.realm = realm
                //setUpRecyclerView(realm)
            }
        })


    }


    override fun onResume(){
        super.onResume()
        setContentView(R.layout.activity_messages)

        val recipient:String = intent.getStringExtra("recipient").toString()//recipient shortname
        val type:String =intent.getStringExtra("type").toString()
        account = intent.getStringExtra("account").toString()//recipient id
        val txtHeader = findViewById<TextView>(R.id.textHeader)
        when(type){
            "direct_t"->txtHeader.text = "$recipient's parents"
            "direct_p"->txtHeader.text = "$recipient's teachers"
            "class" ->txtHeader.text = "$recipient"
            else ->txtHeader.text = "something has gone terribly wrong. type = $type , recipient =  $recipient"
        }

        user = UDPSApp.currentUser()
        test = SyncConfiguration.Builder(user!!, "test")
            .waitForInitialRemoteData()
            .build()

        Realm.getInstanceAsync(test, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@messages.realm = realm
                recyclerView = findViewById(R.id.messagesRV)
                setUpRecyclerView(realm, account)
            }
        })





        //realm = Realm.getDefaultInstance()
        val mongoClient : MongoClient = user?.getMongoClient("mongodb-atlas")!! // service for MongoDB Atlas cluster containing custom user data
        val mongoDatabase : MongoDatabase = mongoClient.getDatabase("YarmGwanga")!!
        val mongoCollection : MongoCollection<Document> = mongoDatabase.getCollection("Messages")!!

        val sendButton = findViewById<Button>(R.id.buttonSend)
        val inputTA = findViewById<EditText>(R.id.TAmessageInput)
        sendButton.setOnClickListener{
            val timeRaw = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")
            val formatted = timeRaw.format(formatter)
            var toInsert = messagesItem(user!!.id,
                user!!.customData!!.get("shortName")!!.toString(), formatted, inputTA.text.toString(), "", account)
            realm.executeTransactionAsync { realm ->
                realm.insert(toInsert)

            }
            inputTA.text.clear()
        }
        val imgCapture = findViewById<Button>(R.id.buttonPicture)
        imgCapture.text = String(Character.toChars(0x1F4F7))
        imgCapture.setOnClickListener{
            val Intent = Intent(this, cameraActivity::class.java).apply {
                putExtra("source", "messages")
                putExtra("account", account)
                //putExtra("account", account)
            }
            startActivity(Intent)
        }
        val pickPicture = findViewById<Button>(R.id.buttonPictureSelect)
        pickPicture.text = String(Character.toChars(0x1F5BC))
        pickPicture.setOnClickListener {
            loadImg.launch("image/*")
            Log.e("pick button", "pickbuton pressed")
        }


        //frag_home.addView(dynamicText)


    }


    private fun setUpRecyclerView(realm: Realm, account:String) {
        // a recyclerview requires an adapter, which feeds it items to display.
        // Realm provides RealmRecyclerViewAdapter, which you can extend to customize for your application
        // pass the adapter a collection of Tasks from the realm
        // we sort this collection so that the displayed order of Tasks remains stable across updates
        adapter = MessageRecyclerAdapter(realm.where<messagesItem>().contains("conversation", account).sort("_id").findAll())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.scrollToPosition(adapter.itemCount-1)
        //recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}