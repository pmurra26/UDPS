package com.example.udps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/**
 * log in screen code
 * currently uses a VERY simple and hardcoded array with no encryption to allow logging in under 3 accounts:
 * kerry, a test teacher account; annie_mum, a test parent account for a made up kid annie;
 * and twin_mum, a test account to work out a parent with multiple children.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //attach an action to the log in screen button
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener{
            validate(findViewById<EditText>(R.id.editTextUser).text.toString(), findViewById<EditText>(R.id.editTextPass).text.toString())
        }
    }

    //log in validation function. will have to be changed completely at some point
    fun validate(username:String, password:String){
        var accounts = arrayOf(arrayOf("kerry", "abc123", "teacher", "teacher"),
            arrayOf("annie_mum", "zxasqw12", "parent", "red wombats"), arrayOf("twin_mum", "twins!", "parent", "red wombats"))
        for(i in accounts.indices)
            if (username == accounts[i][0]){
                if (password==accounts[i][1]){
                    if(accounts[i][2]=="teacher") {
                        val Intent = Intent(this, MainActivity2::class.java).apply {
                            putExtra("username", username)
                            putExtra("account", accounts[i][2])
                        }

                        Toast.makeText(this@MainActivity, "Welcome $username!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent)
                        break
                    }else {
                        val Intent = Intent(this, photoboardActivity::class.java).apply {
                            putExtra("username", username)
                            putExtra("account", accounts[i][2])
                            putExtra("recipient", accounts[i][3] )
                        }

                        Toast.makeText(this@MainActivity, "Welcome $username!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent)
                        break
                    }

                }
                else Toast.makeText(this@MainActivity, "incorrect username or password", Toast.LENGTH_SHORT).show()

            }
            else Toast.makeText(this@MainActivity, "incorrect username or password", Toast.LENGTH_SHORT).show()

    }
}