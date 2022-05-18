package com.example.udps

import org.bson.types.ObjectId
import io.realm.MutableRealmInteger
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.codecs.pojo.annotations.BsonProperty




open class messagesItem(@PrimaryKey var _id: ObjectId = ObjectId(),
                        var sender: String? = "unknown",
                        var senderSname: String? = "unknown",
                        var time: String? = "0",
                        var message: String? = "blank",
                        var conversation: String? = "unknown",
                        @field:BsonProperty("_partition") // specify that this is a field-level annotation
                        var partition : String? = "test"):RealmObject() {
    override fun toString(): String {
        return "Message: [id=$_id, Sender=$sender, SenderShortname=$senderSname, date=$time, message=$message, conversation =$conversation partition=$partition]"
    }
}
