package com.example.udps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

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

        val classNames = arrayOf("red wombats", "yellow porcupines", "gold giraffes", "blue beetles")

        /**creates a dynamic text view that states who you are logged in as,
         * then uses that information to create buttons for classes
         *
         */
        val llHome = findViewById(R.id.ll_home) as LinearLayout
        val nametag = TextView(this)
        nametag.text = "signed in as: "+account+" \naccount type: "+accountType
        llHome.addView(nametag)
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
            }
            for (i in classes.indices){
                for(j in classes[i].indices){
                    val button_dynamic = Button(this)
                    // setting layout_width and layout_height using layout parameters
                    button_dynamic.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    button_dynamic.text = classes[i][j][0]+"'s parents"
                    button_dynamic.setOnClickListener {
                        val Intent = Intent(this, messages::class.java).apply {
                            putExtra("recipient", classes[i][j][0])
                            putExtra("type", "direct_t")
                            putExtra("account", account)
                        }
                        startActivity(Intent)
                    }
                    llHome.addView(button_dynamic)
                }
            }
        }

    }
}