package com.example.udps

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
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
import org.bson.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class messages : AppCompatActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var user: User? = null
    private lateinit var adapter: MessageRecyclerAdapter
    private lateinit var realm: Realm
    private lateinit var recyclerView: RecyclerView


    var messageHistory = mutableListOf<Array<out Any>>(arrayOf("kerry", "10:50, 24/03", "test message from \"kerry\"", "text"),
        arrayOf("annie_mum", "10:52, 24/03", "test message from \"annie_mum\"", "text"),
        arrayOf("kerry", "10:50, 24/03", R.drawable.test_pic_01, "image"),
        arrayOf("annie_mum", "10:52, 24/03", R.drawable.test_pic_02, "image"))


    //override fun onStart() {
      //  super.onStart()


    //}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        println(savedInstanceState==null)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(result.data)
                }
            }
        val pickPicture = findViewById<Button>(R.id.buttonPictureSelect)
        pickPicture.setOnClickListener {
            imageChooser()
        }


    }


    override fun onResume(){
        super.onResume()
        setContentView(R.layout.activity_messages)

        val recipient:String = intent.getStringExtra("recipient").toString()//recipient shortname
        val type:String =intent.getStringExtra("type").toString()
        val account:String = intent.getStringExtra("account").toString()//recipient id
        val txtHeader = findViewById<TextView>(R.id.textHeader)
        when(type){
            "direct_t"->txtHeader.text = "$recipient's parents"
            "direct_p"->txtHeader.text = "$recipient's teachers"
            "class" ->txtHeader.text = "$recipient"
            else ->txtHeader.text = "something has gone terribly wrong. type = $type , recipient =  $recipient"
        }

        val test = RealmConfiguration.Builder().name("default3")
            .schemaVersion(2)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.getInstanceAsync(test, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@messages.realm = realm
                recyclerView = findViewById(R.id.messagesRV)
                setUpRecyclerView(realm, account)
            }
        })

        //for(i in messageHistory.indices){
            //buildMessage(messageHistory[i][0] as String, messageHistory[i][1]  as String,messageHistory[i][3]  as String,messageHistory[i][2] )
        //}
        user = UDPSApp.currentUser()



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
            var toInsert = messagesItem(user?.id,
                user?.customData?.get("shortName")?.toString(), formatted, inputTA.text.toString(), null, account)
            realm.executeTransactionAsync { realm ->
                realm.insert(toInsert)

            }
            /*mongoCollection?.insertOne(toInsert)?.getAsync { result ->
                    if (result.isSuccess) {
                        Log.v("EXAMPLE", "Inserted message document. _id of inserted document: ${result.get().insertedId}")
                    } else {
                        Log.e("EXAMPLE", "Unable to insert message. Error: ${result.error}")
                    }
                }*/
            //buildMessage(content=inputTA.text)
            //saveMessage(inputTA.text,"text")
            inputTA.text.clear()
        }
        val imgCapture = findViewById<Button>(R.id.buttonPicture)
        imgCapture.setOnClickListener{
            val Intent = Intent(this, cameraActivity::class.java).apply {
                putExtra("source", "messages")
                putExtra("account", account)
                //putExtra("account", account)
            }
            startActivity(Intent)
        }


        //frag_home.addView(dynamicText)


    }

    private fun handleCameraImage(intent: Intent?) {
        Log.d("PT", "photo taken")
        val photo = intent?.extras?.get("data") as Bitmap
        //saveMessage(type="BMP", input = photo)

    }

    private fun buildMessage(
        sender:String = intent.getStringExtra("account").toString(),
        dateTime:String = "now",
        type:String = "text",
        content:Any,
        ) {
        val messageSV = findViewById<LinearLayout>(R.id.ll_messages_scrolling)
        val messageCL = LinearLayout(this)
        val messageSpacer = LinearLayout(this)
        val messageHT = LinearLayout(this)
        val messageHL = LinearLayout(this)
        val messageHD = LinearLayout(this)
        val message4 = LinearLayout(this)
        val senderL = TextView(this)
        val timeRCVD = TextView(this)
        val message = TextView(this)
        val image = ImageView(this)

        senderL.text = sender
        timeRCVD.text = dateTime

        senderL.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        timeRCVD.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        messageHT.layoutParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val hlp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        hlp.weight = 50F
        messageHL.layoutParams = hlp
        messageHD.layoutParams = hlp
        messageHD.gravity = Gravity.RIGHT


        messageSpacer.layoutParams = LinearLayout.LayoutParams(
            0,
            0
        )

        messageCL.layoutParams = LinearLayout.LayoutParams(
            800,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        messageCL.orientation = LinearLayout.VERTICAL

        message4.layoutParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        messageHL.addView(senderL)
        messageHD.addView(timeRCVD)
        messageHT.addView(messageHL)
        messageHT.addView(messageHD)
        messageCL.addView(messageHT)

        if (type == "text") {
            message.text = content.toString()
            message.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            messageCL.addView(message)
        } else if (type == "image") {
            image.setImageResource(content as Int)
            /*if(messageHistory[i][2]=="test_picture1.jpeg"){
                val testarray = arrayOf(R.drawable.test_pic_01, "test")
                image.setImageResource(R.drawable.test_pic_01)
            }else {
                image.setImageResource(R.drawable.test_pic_02)
            }*/
            image.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            messageCL.addView(image)

        } else {
            image.setImageBitmap(content as Bitmap)
            /*if(messageHistory[i][2]=="test_picture1.jpeg"){
                val testarray = arrayOf(R.drawable.test_pic_01, "test")
                image.setImageResource(R.drawable.test_pic_01)
            }else {
                image.setImageResource(R.drawable.test_pic_02)
            }*/
            image.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            messageCL.addView(image)

        }

        if (intent.getStringExtra("account").toString() == sender) {
            messageCL.setBackgroundResource(R.drawable.shape_sent)
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 66f
            val lp2 = LinearLayout.LayoutParams(0, 0)
            lp2.weight = 33f
            messageCL.layoutParams = lp
            messageSpacer.layoutParams = lp2
            message4.addView(messageSpacer)
            message4.addView(messageCL)
            messageSV.addView(message4)
        } else {
            messageCL.setBackgroundResource(R.drawable.shape_recieved)
            //messageSV.addView(messageCL)
        }
    }


    fun imageChooser() {

        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 200)
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    /*fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 200) {
                // Get the url of the image from data
                val selectedImageUri: Uri? = data.data
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    //IVPreviewImage.setImageURI(selectedImageUri)
                }
            }
        }
    }
*/

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