package com.example.udps


import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.log.LogLevel
import io.realm.log.RealmLog
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration


lateinit var UDPSApp: App

// global Kotlin extension that resolves to the short version
// of the name of the current class. Used for labelling logs.
inline fun <reified T> T.TAG(): String = T::class.java.simpleName

/*
 * TaskTracker: Sets up the taskApp Realm App and enables Realm-specific logging in debug mode.
 */
class UDPS : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val test = RealmConfiguration.Builder().name("default2")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        UDPSApp = App(AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID)
            .baseUrl(BuildConfig.MONGODB_REALM_URL)
            .appName(BuildConfig.VERSION_NAME)
            .appVersion(BuildConfig.VERSION_CODE.toString())
            .build())

        // Enable more logging in debug mode
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(LogLevel.ALL)
        }
    }
}
