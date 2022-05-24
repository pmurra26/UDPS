package com.example.udps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.log.RealmLog
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration


class photoboardActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private var user: User? = null
    private lateinit var accountT:String
    private lateinit var adapterL: PhotoboardRecyclerAdapterLeftie
    private lateinit var adapterR: PhotoboardRecyclerAdapterRightie
    private lateinit var recyclerViewL: RecyclerView
    private lateinit var recyclerViewR: RecyclerView
    private var mTouchedRvTag: Int =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoboard)
    }
    override fun onBackPressed() {
        // Disable going back to the MainActivity
        if (accountT == "parent"){moveTaskToBack(true)}
        else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        val txtHeader = findViewById<TextView>(R.id.PBtextHeader)
        val recipient:String = intent.getStringExtra("recipient").toString()
        val accountT = intent.getStringExtra("account")
        val account = intent.getStringExtra("username")
        val actionButton = findViewById<Button>(R.id.head_actionButton)
        val headImg = findViewById<TextView>(R.id.head_image)


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

                this@photoboardActivity.realm = realm
                //setUpRecyclerView(realm)
            }
        })
        txtHeader.text = "$recipient"

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
        /*if(true){
        val images = arrayOf(R.drawable.test_pic_01, R.drawable.test_pic_02, R.drawable.test_pic_03, R.drawable.test_pic_04,
            R.drawable.test_pic_05, R.drawable.test_pic_06, R.drawable.test_pic_07, R.drawable.test_pic_08,
            R.drawable.test_pic_09, R.drawable.test_pic_10, R.drawable.test_pic_11, R.drawable.test_pic_12,
            R.drawable.test_pic_13, R.drawable.test_pic_14, R.drawable.test_pic_15, R.drawable.test_pic_16,
            R.drawable.test_pic_17, R.drawable.test_pic_18, R.drawable.test_pic_19, R.drawable.test_pic_20)
        var flipper = 0
        val PBLeftie = findViewById<LinearLayout>(R.id.photoboardLLLeft)
        val PBRightie = findViewById<LinearLayout>(R.id.photoboardLLRight)
        for(i in images.indices) {
            val image = ImageView(this)
            image.setImageResource(images[i])
            image.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            image.adjustViewBounds = true
            image.isClickable = true
            image.setPadding(4)
            if (flipper == 0) {
                PBLeftie.addView(image)
                flipper = 1
            } else {
                PBRightie.addView(image)
                flipper = 0
            }
            image.setOnClickListener {
                val intent = Intent(this, photoboardCommentsActivity::class.java).apply {
                    putExtra("picture", images[i])
                    putExtra("recipient", recipient)
                    putExtra("account", account)
                }
                startActivity(intent)
            }
        }
        }*/
        Realm.getInstanceAsync(test, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@photoboardActivity.realm = realm
                recyclerViewL = findViewById(R.id.photoboardRVLeftie)
                recyclerViewL.tag=0
                recyclerViewL.addOnItemTouchListener(your_touch_listener)
                recyclerViewL.addOnScrollListener(your_scroll_listener)

                setUpRecyclerViewLeftie(realm, recipient)

            }
        })
        Realm.getInstanceAsync(test, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@photoboardActivity.realm = realm
                recyclerViewR = findViewById(R.id.photoboardRVRightie)
                recyclerViewR.tag=1
                recyclerViewR.addOnItemTouchListener(your_touch_listener)
                recyclerViewR.addOnScrollListener(your_scroll_listener)

                setUpRecyclerViewRightie(realm, recipient)
            }
        })

        if(accountT=="parent"){
            actionButton.text = String(Character.toChars(0x1F4E7))
            actionButton.setOnClickListener {
                val intent = Intent(this, messages::class.java).apply {
                    putExtra("recipient", "teachers")
                    putExtra("type", "direct_p")
                    putExtra("account", user?.id)
                }
                startActivity(intent)
            }

        }else{
            actionButton.setOnClickListener {
                val intent = Intent(this, photoboardPostActivity::class.java).apply {
                    putExtra("source", "teachers")
                    putExtra("room", recipient)
                    putExtra("account", user?.id)
                }
                startActivity(intent)
            }
        }
    }

    private val your_touch_listener: OnItemTouchListener = object : OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            mTouchedRvTag = rv.tag as Int
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    private val your_scroll_listener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.tag as Int == mTouchedRvTag) {
                    for (noOfRecyclerView in 0..1) {
                        if (noOfRecyclerView != recyclerView.tag as Int) {
                            val tempRecyclerView =
                                findViewById<ConstraintLayout>(R.id.photoboardParent).findViewWithTag(noOfRecyclerView) as RecyclerView
                            tempRecyclerView.scrollBy(dx, dy)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        }

    private fun setUpRecyclerViewLeftie(realm: Realm, account:String) {
        // a recyclerview requires an adapter, which feeds it items to display.
        // Realm provides RealmRecyclerViewAdapter, which you can extend to customize for your application
        // pass the adapter a collection of Tasks from the realm
        // we sort this collection so that the displayed order of Tasks remains stable across updates
        adapterL = PhotoboardRecyclerAdapterLeftie(realm.where<photoPostItem>().contains("room", account).sort("_id").findAll())
        recyclerViewL.layoutManager = LinearLayoutManager(this)
        recyclerViewL.adapter = adapterL
        recyclerViewL.setHasFixedSize(true)
        //recyclerView.scrollToPosition(adapter.itemCount-1)
        //recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
    private fun setUpRecyclerViewRightie(realm: Realm, account:String) {
        // a recyclerview requires an adapter, which feeds it items to display.
        // Realm provides RealmRecyclerViewAdapter, which you can extend to customize for your application
        // pass the adapter a collection of Tasks from the realm
        // we sort this collection so that the displayed order of Tasks remains stable across updates
        adapterR = PhotoboardRecyclerAdapterRightie(realm.where<photoPostItem>().contains("room", account).sort("_id").findAll())
        recyclerViewR.layoutManager = LinearLayoutManager(this)
        recyclerViewR.adapter = adapterR
        recyclerViewR.setHasFixedSize(true)
        //recyclerViewR.scrollToPosition(adapter.itemCount-1)
        //recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}