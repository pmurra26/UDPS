package com.example.udps

//import com.example.udps.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.udps.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.realm.Realm
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import org.bson.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


typealias LumaListener = (luma: Double) -> Unit


class cameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    lateinit var storage: FirebaseStorage

    private var user : User? = null
    private lateinit var realm: Realm

    lateinit var account:String
    lateinit var source:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.saveBtn.setOnClickListener(){}
        viewBinding.delBtn.setOnClickListener(){}

        cameraExecutor = Executors.newSingleThreadExecutor()

        storage = Firebase.storage

        user = UDPSApp.currentUser()
        account = intent.getStringExtra("account").toString()
        source = intent.getStringExtra("source").toString()



        realm = Realm.getDefaultInstance()
        val mongoClient : MongoClient = user?.getMongoClient("mongodb-atlas")!! // service for MongoDB Atlas cluster containing custom user data
        val mongoDatabase : MongoDatabase = mongoClient.getDatabase("YarmGwanga")!!
        val mongoCollection : MongoCollection<Document> = mongoDatabase.getCollection("Messages")!!

    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = user?.customData?.get("shortName").toString()+SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/UDPS-image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                    viewBinding.saveBtn.visibility = View.VISIBLE
                    viewBinding.delBtn.visibility = View.VISIBLE
                    viewBinding.previewImg.visibility = View.VISIBLE
                    viewBinding.imageCaptureButton.visibility = View.GONE
                    viewBinding.viewFinder.visibility=View.GONE
                    viewBinding.previewImg.setImageURI(output.savedUri)
                    val photo = File(output.savedUri?.path)
                    val photo2 = File("storage/emulated/0/Pictures/UDPS-image/"+name+".jpg")


                    viewBinding.saveBtn.setOnClickListener(){
                        viewBinding.saveBtn.isEnabled=false
                        viewBinding.delBtn.isEnabled=false
                        var storageRef = storage.reference
                        var file = Uri.fromFile(photo2)
                        val photoRef = storageRef.child("images/${file.lastPathSegment}")
                        val uploadTask = photoRef.putFile(file)
                        val urlTask = uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            photoRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                Log.e(TAG, "Photo capture succeded, url: ${downloadUri}")
                                if(source=="messages") {
                                    val timeRaw = LocalDateTime.now()
                                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")
                                    val formatted = timeRaw.format(formatter)
                                    var toInsert = messagesItem(
                                        user!!.id,
                                        user!!.customData!!.get("shortName")!!.toString(),
                                        formatted,
                                        "",
                                        downloadUri.toString(),
                                        account
                                    )
                                    realm.executeTransactionAsync { realm ->
                                        realm.insert(toInsert)

                                    }
                                }else if(source=="post"){
                                    val intent = Intent()
                                    intent.putExtra("url", downloadUri.toString())
                                    Log.e(TAG, "Photo returned:${downloadUri.toString()}")
                                    setResult(RESULT_OK, intent)
                                }
                                finish()
                            } else {
                                Log.e(TAG, "Photo capture failed")
                                // Handle failures
                                // ...
                            }
                        }

                    }
                    viewBinding.delBtn.setOnClickListener(){
                        viewBinding.saveBtn.visibility = View.GONE
                        viewBinding.delBtn.visibility = View.GONE
                        viewBinding.previewImg.visibility = View.GONE
                        viewBinding.imageCaptureButton.visibility = View.VISIBLE
                        viewBinding.viewFinder.visibility=View.VISIBLE
                        photo2.delete()
                    }
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}