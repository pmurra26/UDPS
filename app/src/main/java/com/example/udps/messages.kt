package com.example.udps

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class messages : AppCompatActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    var messageHistory = mutableListOf<Array<out Any>>(arrayOf("kerry", "         10:50, 24/03", "test message from \"kerry\"", "text"),
        arrayOf("annie_mum", "       10:52, 24/03", "test message from \"annie_mum\"", "text"),
        arrayOf("kerry", "         10:50, 24/03", R.drawable.test_pic_01, "image"),
        arrayOf("annie_mum", "       10:52, 24/03", R.drawable.test_pic_02, "image"))


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



        for(i in messageHistory.indices){
            buildMessage(messageHistory[i][0] as String, messageHistory[i][1]  as String,messageHistory[i][3]  as String,messageHistory[i][2] )
        }
        val sendButton = findViewById<Button>(R.id.buttonSend)
        val inputTA = findViewById<EditText>(R.id.TAmessageInput)
        sendButton.setOnClickListener{
            buildMessage(content=inputTA.text)
            saveMessage(inputTA.text,"text")
            inputTA.text.clear()
        }
        val imgCapture = findViewById<Button>(R.id.buttonPicture)
        imgCapture.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)
        }


        //frag_home.addView(dynamicText)


    }

    private fun handleCameraImage(intent: Intent?) {
        Log.d("PT", "photo taken")
        val photo = intent?.extras?.get("data") as Bitmap
        saveMessage(type="BMP", input = photo)

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
        val messageH= LinearLayout(this)
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

        messageH.addView(senderL)
        messageH.addView(timeRCVD)
        messageCL.addView(messageH)

        if (type=="text"){
            message.text = content.toString()
            message.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            messageCL.addView(message)
        } else if(type == "image"){
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

        }else{
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





        if(intent.getStringExtra("account").toString()==sender){
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
    fun saveMessage(input:Any, type:String){
        val account = intent.getStringExtra("account").toString()
        val dateTime = "now"

        messageHistory.add(arrayOf(account, dateTime, input, type))
    }
}