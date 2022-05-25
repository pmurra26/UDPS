package com.example.udps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import io.realm.log.RealmLog
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.Realm
import org.bson.Document

/**
 * log in screen code
 * currently uses a VERY simple and hardcoded array with no encryption to allow logging in under 3 accounts:
 * kerry, a test teacher account; annie_mum, a test parent account for a made up kid annie;
 * and twin_mum, a test account to work out a parent with multiple children.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.editTextUser)
        password = findViewById(R.id.editTextPass)
        loginButton = findViewById<Button>(R.id.loginBtn)
        //attach an action to the log in screen button

        loginButton.setOnClickListener{
            login()
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            user = UDPSApp.currentUser()
        } catch (e: IllegalStateException) {
            RealmLog.warn(e)
        }
        if (user != null) {
            val customUserData : Document? = user?.customData
            val accountType = customUserData?.get("accountType")
            val shortName = customUserData?.get("shortName")
            if(accountType=="teacher") {
                val intent = Intent(this, MainActivity2::class.java).apply {
                    putExtra("username", shortName.toString())
                    putExtra("account", "teacher")
                }

                Toast.makeText(this@MainActivity, "Welcome $username!", Toast.LENGTH_SHORT)
                    .show()
                startActivity(intent)
            }else {
                val accessCode:Int = customUserData?.get("access").toString().toInt()
                var room = ""
                if(accessCode<=8){
                    when(accessCode){
                        1->room="Bush Babies"
                        2->room="Gumnut Toddlers"
                        4->room="Kindy Koalas"
                        8->room="Preschool Possums"
                        else->{

                        }
                    }

                }

                val Intent = Intent(this, photoboardActivity::class.java).apply {
                    putExtra("username", shortName.toString())
                    putExtra("account", "parent")
                    putExtra("recipient", room)
                }

                Toast.makeText(this@MainActivity, "Welcome $username!", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent)

            }
        }

    }

    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }

    private fun onLoginSuccess(partition: String) {
        // successful login ends this activity, bringing the user back to the task activity
        val sharedPreference =  getSharedPreferences("prefs name", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("partition",partition)
        editor.commit()
        user = UDPSApp.currentUser()
        val customUserData : Document? = user?.customData
        Log.v("EXAMPLE", "found custom user data document. _id of inserted document: ${customUserData}")
        val accountType = customUserData?.get("accountType")
        val shortName = customUserData?.get("shortName")
        if(accountType=="teacher") {
            val intent = Intent(this, MainActivity2::class.java).apply {
                putExtra("username", shortName.toString())
                putExtra("account", accountType.toString())
            }

            Toast.makeText(this@MainActivity, "Welcome $shortName!", Toast.LENGTH_SHORT)
                .show()
            startActivity(intent)
        }else if(accountType=="parent") {
            val accessCode:Int = customUserData?.get("access").toString().toInt()
            var room = ""
            if(accessCode<=8){
                when(accessCode){
                    1->room="Bush Babies"
                    2->room="Gumnut Toddlers"
                    4->room="Kindy Koalas"
                    8->room="Preschool Possums"
                    else->{

                    }
                }

            }
            val Intent = Intent(this, photoboardActivity::class.java).apply {
                putExtra("username", shortName.toString())
                putExtra("account", accountType.toString())
                putExtra("recipient", room)
            }

            Toast.makeText(this@MainActivity, "Welcome $shortName!", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent)

        } else{
            val queryFilter = Document("flag", username.text.toString())
            val mongoClient : MongoClient = user?.getMongoClient("mongodb-atlas")!! // service for MongoDB Atlas cluster containing custom user data
            val mongoDatabase : MongoDatabase = mongoClient.getDatabase("YarmGwanga")!!
            val mongoCollection : MongoCollection<Document> = mongoDatabase.getCollection("YarmGwangaCustomData")!!

            mongoCollection.findOne(queryFilter)
                .getAsync { task ->
                    if (task.isSuccess) {
                        val result = task.get()
                        mongoCollection.insertOne(Document("ownerId", user!!.id).append("_partition", "test")
                            .append("shortName", result["shortName"]).append("accountType", result["accountType"])
                            .append("children", result["children"]).append("access", result["access"]).append("active", "1")).getAsync { result ->
                            if (result.isSuccess) {
                                Log.v("EXAMPLE", "Inserted custom user data document. _id of inserted document: ${result.get().insertedId}")
                            } else {
                                Log.e("EXAMPLE", "Unable to insert custom user data. Error: ${result.error}")
                            }
                        }

                        result.clear()
                        Log.v("EXAMPLE", "successfully found a document: $result")
                        Log.v("EXAMPLE", "successfully found a document: ${username.text.toString()}")
                        var realm = Realm.getDefaultInstance()
                        user?.logOutAsync {
                            if (it.isSuccess) {
                                // always close the realm when finished interacting to free up resources
                                realm.close()
                                user = null
                                Log.v(TAG(), "user logged out")
                                //startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                RealmLog.error(it.error.toString())
                                Log.e(TAG(), "log out failed! Error: ${it.error}")
                            }
                        }
                        login()
                    } else {
                        Log.e("EXAMPLE", "failed to find document with: ${task.error}")
                    }
                }
        }
        /*val Intent = Intent(this, photoboardActivity::class.java).apply {
            putExtra("username", username.text)
            putExtra("account", "teacher")
            putExtra("recipient", "teacher" )
        }

        Toast.makeText(this@MainActivity, "Welcome $username!", Toast.LENGTH_SHORT)
            .show()
        startActivity(Intent)*/
        //finish()
    }
    private fun onLoginFailed(errorMsg: String) {
        Log.v(TAG(), errorMsg)
        Toast.makeText(baseContext, errorMsg, Toast.LENGTH_LONG).show()
    }
    private fun validateCredentials(): Boolean = when {
        // zero-length usernames and passwords are not valid (or secure), so prevent users from creating accounts with those client-side.
        username.text.toString().isEmpty() -> false
        password.text.toString().isEmpty() -> false
        else -> true
    }

    private fun login() {
        if (!validateCredentials()) {
            onLoginFailed("Invalid username or password")
            return
        }

        // while this operation completes, disable the buttons to login or create a new account
        loginButton.isEnabled = false

        val username = this.username.text.toString()
        val password = this.password.text.toString()



        val creds = Credentials.emailPassword(username, password)
        UDPSApp.loginAsync(creds) {
            // re-enable the buttons after
            //loginButton.isEnabled = true
            if (!it.isSuccess) {
                RealmLog.error(it.error.toString())
                onLoginFailed(it.error.message ?: "An error occurred.")
                loginButton.isEnabled = true
            } else {

                onLoginSuccess("test")
            }
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