package com.example.udps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding

class photoboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoboard)
    }

    override fun onResume() {
        super.onResume()
        val txtHeader = findViewById<TextView>(R.id.PBtextHeader)
        val recipient:String = intent.getStringExtra("recipient").toString()
        val accountT = intent.getStringExtra("account")
        val account = intent.getStringExtra("username")
        val actionButton = findViewById<Button>(R.id.head_actionButton)
        txtHeader.text = "$recipient"
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
            image.isClickable=true
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
        if(accountT=="parent"){
            actionButton.text = String(Character.toChars(0x1F4E7))
            actionButton.setOnClickListener {
                val intent = Intent(this, messages::class.java).apply {
                    putExtra("recipient", "child")
                    putExtra("type", "direct_p")
                       putExtra("account", account)
                }
                startActivity(intent)
            }

        }
    }
}