package com.example.udps

import android.R
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.udps.databinding.FragmentCreateAccountBinding

import io.realm.log.RealmLog
import io.realm.mongodb.Credentials
import io.realm.mongodb.App
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.Realm
import io.realm.mongodb.auth.EmailPasswordAuth
import org.bson.Document

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [createAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class createAccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() =_binding!!
    var accountType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun onRadioBtnClicked(){
        if(binding.radioTeacher.isChecked){
            binding.childTop.visibility=View.GONE
            accountType=0
        }else{
            binding.childTop.visibility=View.VISIBLE
            accountType = 1
        }
    }

    private fun validateCredentials(): Boolean = when {
        // zero-length usernames and passwords are not valid (or secure), so prevent users from creating accounts with those client-side.
        binding.parentEmail.text.toString().isEmpty() -> false
        binding.parentPassword.text.toString().isEmpty() -> false
        binding.parentShortName.text.toString().isEmpty() -> false
        else -> true
    }
    private fun onCreateFailed(errorMsg: String) {
        Log.v(TAG(), errorMsg)
        //Toast.makeText(baseContext, errorMsg, Toast.LENGTH_LONG).show()
    }

    fun createAccount(){
        if (!validateCredentials()) {
            onCreateFailed("Invalid username or password")
            return
        }
        binding.saveBtn.isEnabled = false
        binding.clearBtn.isEnabled = false
        val username = binding.parentEmail.text.toString()
        val password = binding.parentPassword.text.toString()
        val shortName = binding.parentShortName.text.toString()
        UDPSApp.emailPassword.registerUserAsync(username, password) {
            // re-enable the buttons after user registration completes
            binding.saveBtn.isEnabled = true
            binding.clearBtn.isEnabled = true
            if (!it.isSuccess) {
                onCreateFailed("Could not register user.")
                Log.v(TAG(), "Error: ${it.error}")
            } else {
                Log.v(TAG(), "Successfully registered user.")
                // when the account has been created successfully, log in to the account
                //login(false)
            }
        }
        var access = 0
        if(binding.radioBabies.isChecked)access = 1
        if(binding.radioToddlers.isChecked)access = 2
        if(binding.radioKindy.isChecked)access = 4
        if(binding.radioPreschool.isChecked)access = 8
        var user = UDPSApp.currentUser()
        val mongoClient : MongoClient = user?.getMongoClient("mongodb-atlas")!! // service for MongoDB Atlas cluster containing custom user data
        val mongoDatabase : MongoDatabase = mongoClient.getDatabase("YarmGwanga")!!
        val mongoCollection : MongoCollection<Document> = mongoDatabase.getCollection("YarmGwangaCustomData")!!
        var newUserDoc = Document("ownerId", "tbd").append("flag", username).append("shortName", shortName).append("_partition", "test")
        if (accountType==0){
            newUserDoc.append("accountType", "teacher")
        }else{
            newUserDoc.append("accountType", "parent").append("children", binding.childname.text.toString()).append("access", access)
        }

        mongoCollection.insertOne(newUserDoc)
            .getAsync { result ->
                if (result.isSuccess) {
                    Log.v("EXAMPLE", "Inserted custom user data document. _id of inserted document: ${result.get().insertedId}")
                } else {
                    Log.e("EXAMPLE", "Unable to insert custom user data. Error: ${result.error}")
                }
            }
        binding.parentShortName.text.clear()
        binding.parentEmail.text.clear()
        binding.parentPassword.text.clear()
        binding.radioTeacher.isChecked = true
        onRadioBtnClicked()
        binding.childname.text.clear()
    }

    fun clearFields(){
        binding.parentShortName.text.clear()
        binding.parentEmail.text.clear()
        binding.parentPassword.text.clear()
        binding.radioTeacher.isChecked = true
        onRadioBtnClicked()
        binding.childname.text.clear()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.radioParent.setOnClickListener(){onRadioBtnClicked()}
        binding.radioTeacher.setOnClickListener(){onRadioBtnClicked()}
        binding.saveBtn.setOnClickListener(){createAccount()}
        binding.clearBtn.setOnClickListener(){clearFields()}
        /*val res:Resources = resources
        val classList = arrayListOf<String>("red wombats", "yellow porcupines","gold girraffes", "blue beetles")
        if (binding.spinClass ==null){
            val adapter = ArrayAdapter(activity,
                R.layout.simple_spinner_item, classList)
            binding.spinClass.adapter = adapter
        }*/
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment createAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                createAccountFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}