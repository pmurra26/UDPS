package com.example.udps

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class messages : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        println(savedInstanceState==null)
    }
    override fun onResume(){
        super.onResume()
        setContentView(R.layout.activity_messages)

        val recipient:String = intent.getStringExtra("recipient").toString()
        val type:String =intent.getStringExtra("type").toString()
        val account:String = intent.getStringExtra("account").toString()
        println("parts recieved")
        val txtHeader = findViewById<TextView>(R.id.textHeader)
        when(type){
            "direct_t"->txtHeader.text = "$recipient's parents"
            "direct_p"->txtHeader.text = "$recipient's teachers"
            "class" ->txtHeader.text = "$recipient"
            else ->txtHeader.text = "something has gone terribly wrong. type = $type , recipient =  $recipient"
        }

        val messageHistory = arrayOf(arrayOf("kerry", "         10:50, 24/03", "test message from \"kerry\"", "text"),
            arrayOf("annie_mum", "       10:52, 24/03", "test message from \"annie_mum\"", "text"),
            arrayOf("kerry", "         10:50, 24/03", R.drawable.test_pic_01, "image"),
            arrayOf("annie_mum", "       10:52, 24/03", R.drawable.test_pic_02, "image"))
        val messageSV = findViewById<LinearLayout>(R.id.ll_messages_scrolling)
        for(i in messageHistory.indices){
            val messageCL = LinearLayout(this)
            val messageSpacer = LinearLayout(this)
            val messageH= LinearLayout(this)
            val message4 = LinearLayout(this)
            val sender = TextView(this)
            val timeRCVD = TextView(this)
            val message = TextView(this)
            val image = ImageView(this)

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
                800,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            messageCL.orientation = LinearLayout.VERTICAL

            message4.layoutParams = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            messageH.addView(sender)
            messageH.addView(timeRCVD)
            messageCL.addView(messageH)

            if (messageHistory[i][3]=="text"){
                message.text = messageHistory[i][2] as String
                message.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                messageCL.addView(message)
            } else{
                image.setImageResource(messageHistory[i][2] as Int)
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





            if(account==messageHistory[i][0]){
                messageCL.setBackgroundResource(R.drawable.shape_sent)
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 66f
                val lp2 = LinearLayout.LayoutParams(0, 0)
                lp2.weight = 33f
                messageCL.layoutParams = lp
                messageSpacer.layoutParams=lp2
                message4.addView(messageSpacer)
                message4.addView(messageCL)
                messageSV.addView(message4)
            } else {
                messageCL.setBackgroundResource(R.drawable.shape_recieved)
                messageSV.addView(messageCL)
            }



        }
        println("attempted to join room")

        //frag_home.addView(dynamicText)

    }
}