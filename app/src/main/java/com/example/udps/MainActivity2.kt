package com.example.udps

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import org.xml.sax.Parser

/**
 * code controlling the room selection screen, dynamically creates rooms based on who logs in,
 * students and classes are hard coded in arrays, which are looped through to find which rooms to make
 * teacher and parent code is handled differently due to different needs
 *
 */


class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_main2)

        val account:String = intent.getStringExtra("username").toString()
        val accountType=intent.getStringExtra("account").toString()


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

        val classNames = arrayOf("Red Wombats", "Yellow Porcupines", "Gold Giraffes", "Blue Beetles")



        /**creates a dynamic text view that states who you are logged in as,
         * then uses that information to create buttons for classes
         *
         */
        val homeHeader = findViewById<LinearLayout>(R.id.homeHeader)
        val llHome = findViewById<LinearLayout>(R.id.ll_home)
        val messageTitle = findViewById<CardView>(R.id.messageTitle)

        val nametag = TextView(this)
        val accountButton = findViewById<Button>(R.id.homeHeaderButton)

        nametag.text = "signed in as: "+account+" \naccount type: "+accountType
        nametag.setTextColor(Color.parseColor("#FFFFFF"))
        homeHeader.addView(nametag)

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
        if( accountType == "teacher"){

            for (i in classes.indices) {
                val button_dynamic = Button(this)
                // setting layout_width and layout_height using layout parameters
                button_dynamic.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                /**
                 *  Each class photoboard button aesthetics
                 */
                val param = button_dynamic.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(10,10,10,10)
                button_dynamic.layoutParams = param
                button_dynamic.text = classNames[i]
                button_dynamic.setBackgroundResource(R.drawable.round_button)
                button_dynamic.setTextColor(Color.argb(255,255,255,255))

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

            for (i in classes.indices){

                /**
                 *  Added horizontal scroller with linear layout. Dynamic student buttons are added to linear layout
                 *  Added class titles inbetween horizontal scrollers for each class
                 */
                val classScroll = HorizontalScrollView(this)
                classScroll.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                val llClass = LinearLayout(this)
                llClass.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    //LinearLayout.LayoutParams.WRAP_CONTENT
                    200
                )
                llClass.gravity = Gravity.CENTER
                llClass.orientation = LinearLayout.HORIZONTAL

                // Class titles
                val classN = TextView(this)
                classN.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                classN.text = classNames[i]
                classN.updatePadding(left = 25)
                classN.setTypeface(null, Typeface.BOLD)
                classN.setTextColor(Color.argb(255,255,255,255))
                llHome.addView(classN)

                for(j in classes[i].indices){
                    val button_dynamic = Button(this)
                    // setting layout_width and layout_height using layout parameters
                    button_dynamic.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    button_dynamic.text = classes[i][j][0] + "'s" + "\n" + "parents"

                    // DYNAMIC BUTTON AESTHETICS
                    button_dynamic.setTextColor(Color.argb(255,255,255,255))
                    button_dynamic.setBackgroundResource(R.drawable.round_button)
                    val param = button_dynamic.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(5,0,5,0)

                    button_dynamic.setOnClickListener {
                        // On click aesthetic
                        button_dynamic.setBackgroundResource(R.drawable.round_button_clicked)
                        val Intent = Intent(this, messages::class.java).apply {
                            putExtra("recipient", classes[i][j][0])
                            putExtra("type", "direct_t")
                            putExtra("account", account)
                        }
                        startActivity(Intent)
                    }
                    // Button added to linear layout
                    llClass.addView(button_dynamic)
                }
                // Linear layout added to horizontal scroll
                classScroll.addView(llClass)
                // Horizontal scroll added to llhome
                llHome.addView(classScroll)

            }
        }

    }
}