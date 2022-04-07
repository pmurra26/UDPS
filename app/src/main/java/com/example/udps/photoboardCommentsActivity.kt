package com.example.udps

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
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
            arrayOf("kerry", "         10:50, 24/03", "heres the kids doing something"),
            arrayOf("annie_mum", "       10:52, 24/03", "that looks great!"))

        for(i in messageHistory.indices) {
            buildMessage(messageHistory[i][0],messageHistory[i][1],messageHistory[i][2])
        }
        val sendButton = findViewById<Button>(R.id.buttonSend)
        val inputTA = findViewById<EditText>(R.id.TAmessageInput)
        sendButton.setOnClickListener{
            buildMessage(content = inputTA.text)
            inputTA.text.clear()
        }
    }
    private fun buildMessage(sender:String = intent.getStringExtra("account").toString(),
                             dateTime:String = "now",
                             content:Any,){
        val messageSV = findViewById<LinearLayout>(R.id.PBC_ll)
        val messageCL = LinearLayout(this)
        val messageSpacer = LinearLayout(this)
        val messageHT= LinearLayout(this)
        val messageHL= LinearLayout(this)
        val messageHD= LinearLayout(this)
        val message4 = LinearLayout(this)
        val senderL = TextView(this)
        val timeRCVD = TextView(this)
        val message = TextView(this)


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

        messageHL.addView(senderL)
        messageHD.addView(timeRCVD)
        messageHT.addView(messageHL)
        messageHT.addView(messageHD)
        messageCL.addView(messageHT)


        message.text = content.toString()
        message.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        messageCL.addView(message)

        messageCL.setBackgroundColor(Color.parseColor("#D3D3D3"))


        messageSV.addView(messageCL, layoutParams)
    }
}