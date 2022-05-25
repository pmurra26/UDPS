package com.example.udps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView


import io.realm.log.RealmLog
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import org.bson.Document


/**
 * code controlling the room selection screen, dynamically creates rooms based on who logs in,
 * students and classes are hard coded in arrays, which are looped through to find which rooms to make
 * teacher and parent code is handled differently due to different needs
 *
 */


class MainActivity2 : AppCompatActivity() {
    private lateinit var realm: Realm
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }
    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }
    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_main2)

        val account:String = intent.getStringExtra("username").toString()
        val accountType=intent.getStringExtra("account").toString()

        val headImg = findViewById<TextView>(R.id.head_image)
        user = UDPSApp.currentUser()
        val test = RealmConfiguration.Builder().name("default3")
            .schemaVersion(2)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(test)
        realm = Realm.getDefaultInstance()
        headImg.setOnClickListener(){
            user?.logOutAsync {
                if (it.isSuccess) {
                    // always close the realm when finished interacting to free up resources
                    realm.close()
                    user = null
                    Log.v(TAG(), "user logged out")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    RealmLog.error(it.error.toString())
                    Log.e(TAG(), "log out failed! Error: ${it.error}")
                }
            }
        }

        //class arrays
        val classes = arrayOf(arrayOf(arrayOf("annie","annie_mum"), arrayOf("betty","bettys_mum"),
            arrayOf("chris", "chris_mum"), arrayOf("david", "david_par"), arrayOf("evangeline", "eva_par"),
            arrayOf("fred", "fred_par)"), arrayOf("grahame", "grahame_par")

        ),arrayOf(arrayOf("henry","henry_mum"), arrayOf("ian","ians_mum"),
            arrayOf("joel", "joel_mum"), arrayOf("kim", "kim_par"), arrayOf("lydia", "lydia_par"),
            arrayOf("monica", "monica_par)"), arrayOf("nora", "nora_par")

        ),arrayOf(arrayOf("optimus prime","optimus prime_par"), arrayOf("patrick","patrick_mum"),
            arrayOf("quincy", "quincy_mum"), arrayOf("ryan", "ryan_par"), arrayOf("sam", "sam_par"),
            arrayOf("tina", "tina_par)"), arrayOf("umbrella", "umbrella_par")

        ),arrayOf(arrayOf("vincent","vincent_par"), arrayOf("winston","winston_mum"),
            arrayOf("xanthe", "xanthe_mum"), arrayOf("yvette", "yvette_par"), arrayOf("zed", "zed_par")
        ))

        val llHome = findViewById<LinearLayout>(R.id.ll_home)
        val messageTitle = findViewById<CardView>(R.id.messageTitle)

        val classNames = arrayOf("Bush Babies", "Gumnut Toddlers", "Kindy Koalas", "Preschool Possums")
        var activeUserId = mutableListOf<String>()
        var activeUserSname = mutableListOf<String>()

        val queryFilter = Document("active", "1")
        val mongoClient : MongoClient = user?.getMongoClient("mongodb-atlas")!! // service for MongoDB Atlas cluster containing custom user data
        val mongoDatabase : MongoDatabase = mongoClient.getDatabase("YarmGwanga")!!
        val mongoCollection : MongoCollection<Document> = mongoDatabase.getCollection("YarmGwangaCustomData")!!

        var findTask = mongoCollection.find(queryFilter).iterator()
        findTask.getAsync { task ->
            if (task.isSuccess) {
                val results = task.get()
                Log.v("EXAMPLE", "successfully found all active users")
                while (results.hasNext()) {
                    var current = results.next()
                    Log.v("EXAMPLE", current.toString())
                    if(current["accountType"] == "parent") {
                        activeUserId.add(current["ownerId"].toString())
                        activeUserSname.add(current["shortName"].toString())
                    }
                }
                if( accountType == "teacher"){
                    Log.v("EXAMPLE", "list of names: ${activeUserSname.toString()}")

                    for (i in classes.indices) {
                        val button_dynamic = Button(this)
                        // setting layout_width and layout_height using layout parameters
                        button_dynamic.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        /**
                         *  Each class photoboard button aesthetics
                         */
                        val param = button_dynamic.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(10,10,10,10)
                        button_dynamic.layoutParams = param
                        button_dynamic.setBackgroundResource(R.drawable.round_button)
                        button_dynamic.setTextColor(getResources().getColor(R.color.primary_text_color))

                        button_dynamic.text = classNames[i]
                        button_dynamic.setOnClickListener{
                            // On click button shape
                            button_dynamic.setBackgroundResource(R.drawable.round_button_clicked)

                            val Intent = Intent(this, photoboardActivity::class.java).apply {
                                putExtra("recipient", classNames[i])
                                putExtra("type", "class")
                                putExtra("account", account)
                            }
                            startActivity(Intent)
                        }
                        llHome.addView(button_dynamic)
                    }

                    // Had to remove and add view to dynamically add class photoboard buttons inbetween titles
                    llHome.removeView(messageTitle)
                    llHome.addView(messageTitle)

                    for (item in activeUserSname) {
                        Log.v("EXAMPLE", activeUserSname.toString())

                        val button_dynamic = Button(this)
                        // setting layout_width and layout_height using layout parameters
                        button_dynamic.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )

                        /**
                         *  Each student button aesthetics
                         */
                        button_dynamic.setTextColor(getResources().getColor(R.color.primary_text_color))
                        button_dynamic.setBackgroundResource(R.drawable.round_button)
                        val param = button_dynamic.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(5,20,5,0)

                        var index = activeUserSname.indexOf(item)
                        button_dynamic.text = item
                        button_dynamic.setOnClickListener {
                            // On click aesthetic
                            button_dynamic.setBackgroundResource(R.drawable.round_button_clicked)

                            val Intent = Intent(this, messages::class.java).apply {
                                putExtra("recipient", item)
                                putExtra("type", "direct_t")
                                putExtra("account", activeUserId.elementAt(index))
                            }
                            startActivity(Intent)
                        }
                        llHome.addView(button_dynamic)

                    }
                }
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ${task.error}")

            }
        }

        Log.v("EXAMPLE", "list of names: ${activeUserSname.toString()}")
        Log.v("EXAMPLE", "accountType: $accountType")

        /**creates a dynamic text view that states who you are logged in as,
         * then uses that information to create buttons for classes
         *
         */
        val nametag = findViewById<TextView>(R.id.hp_textHeader)
        Log.v("EXAMPLE", "list of names: ${activeUserSname.toString()}")
        Log.v("EXAMPLE", "accountType: $accountType")

        val accountButton = findViewById<Button>(R.id.homeHeaderButton)
        nametag.text = account
        accountButton.setOnClickListener{
            val Intent = Intent(this, accountManagementActivity::class.java).apply {
                putExtra("type", "class")
                putExtra("account", account)
            }
            startActivity(Intent)
        }

        if (accountType == "parent"){
            for (i in classes.indices){
                for(j in classes[i].indices){
                    if (classes[i][j][1]==account){
                        val button_dynamic = Button(this)
                        // setting layout_width and layout_height using layout parameters
                        button_dynamic.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        button_dynamic.text = classNames[i]
                        button_dynamic.setOnClickListener{
                            val Intent = Intent(this, messages::class.java).apply {
                                putExtra("recipient", classNames[i])
                                putExtra("type", "class")
                                putExtra("account", account)
                            }
                            startActivity(Intent)
                        }
                        llHome.addView(button_dynamic)
                        val button_dynamic2 = Button(this)
                        // setting layout_width and layout_height using layout parameters
                        button_dynamic2.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )

                        button_dynamic2.text = "direct message teacher"
                        button_dynamic2.setOnClickListener {
                            val Intent = Intent(this, messages::class.java).apply {
                                putExtra("recipient", classes[i][j][0])
                                putExtra("type", "direct_p")
                                putExtra("account", account)
                            }
                            startActivity(Intent)
                        }
                        llHome.addView(button_dynamic2)
                    }
                }
            }
        }

    }
}