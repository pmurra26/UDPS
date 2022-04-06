package com.example.udps

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom


class photoboardCommentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoboard_comments)
    }

    override fun onResume() {
        super.onResume()

        val txtHeader = findViewById<TextView>(R.id.PBC_textHeader)
        val recipient = intent.getStringExtra("recipient")
        val account = intent.getStringExtra("account")
        val picture = findViewById<ImageView>(R.id.PBCPhoto)
        txtHeader.setText(recipient)
        picture.setImageResource(intent.getIntExtra("picture", R.drawable.test_pic_20))

        val messageHistory = arrayOf(arrayOf("kerry", "         10:50, 24/03", "test message from \"kerry\""),
            arrayOf("annie_mum", "       10:52, 24/03", "test message from \"annie_mum\""),
            arrayOf("kerry", "         10:50, 24/03", "heres the kids doing something", "image"),
            arrayOf("annie_mum", "       10:52, 24/03", "that looks great!", "image"))
        val messageSV = findViewById<LinearLayout>(R.id.PBC_ll)
        for(i in messageHistory.indices) {
            val messageCL = LinearLayout(this)
            val messageSpacer = LinearLayout(this)
            val messageH = LinearLayout(this)
            val message4 = LinearLayout(this)
            val sender = TextView(this)
            val timeRCVD = TextView(this)
            val message = TextView(this)


            sender.text = messageHistory[i][0] as String
            timeRCVD.text = messageHistory[i][1] as String

            sender.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            timeRCVD.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            messageH.layoutParams = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            messageSpacer.layoutParams = LinearLayout.LayoutParams(
                0,
                0
            )

            messageCL.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            messageCL.orientation = LinearLayout.VERTICAL
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(4, 4, 4, 4)

            message4.layoutParams = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            messageH.addView(sender)
            messageH.addView(timeRCVD)
            messageCL.addView(messageH)


            message.text = messageHistory[i][2] as String
            message.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            messageCL.addView(message)

            messageCL.setBackgroundColor(Color.parseColor("#D3D3D3"))


            messageSV.addView(messageCL, layoutParams)

        }
    }
}