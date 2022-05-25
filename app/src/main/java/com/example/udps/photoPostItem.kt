package com.example.udps

import org.bson.types.ObjectId
import io.realm.MutableRealmInteger
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.codecs.pojo.annotations.BsonProperty


open class photoPostItem(sender: String = "unknown",
                        senderSname: String = "unknown",
                        time: String = "0",
                        message: String = " ",
                        image:String = " ",
                        room: String = "unknown",
                        partition: String= "test"):RealmObject() {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition:String = partition
    var sender:String =sender
    var senderSname:String = senderSname
    var time:String = time
    var room:String = room
    @Required var message:String = message
    @Required var image:String = image

    override fun toString(): String {
        return "photopost: [id=$_id, Sender=$sender, SenderShortname=$senderSname, date=$time, message=$message, conversation =$room partition=$_partition]"
    }
}