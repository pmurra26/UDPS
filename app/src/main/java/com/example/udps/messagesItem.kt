package com.example.udps

import org.bson.types.ObjectId
import io.realm.MutableRealmInteger
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required



public class messagesItem(sender: String = "unknown", date: String = "0", message: String = "blank", conversation: String = "unknown"):RealmObject() {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var sender = sender
    var date = date
    var message = message
    var conversation = conversation
}
