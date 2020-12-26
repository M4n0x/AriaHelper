package ch.hearc.ariahelper.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ch.hearc.ariahelper.models.persistence.CharacterPersistenceManager
import ch.hearc.ariahelper.models.persistence.PicturePersistenceManager
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.io.ByteArrayOutputStream

class SerializableItem : Serializable {
    private var itemAsString : String
    private var pictureAsByte : ByteArray ? = null

    /**
     * Convert an Item to a serializable item
     */
    constructor(item : Item){
        this.itemAsString = Json.encodeToString(item)
        //only build the ByteArray if necessary
        if(!item.picture.isNullOrBlank()){
            val buffer = ByteArrayOutputStream()
            PicturePersistenceManager.getBitmapFromFilename(item.picture).compress(Bitmap.CompressFormat.PNG, 70, buffer)
            pictureAsByte = buffer.toByteArray()
        }
    }

    /**
     * rebuild the item with its eventual picture
     */
    fun getItem() : Item {
        val item = Json.decodeFromString<Item>(itemAsString)
        pictureAsByte?.let {
            val bitmapPicture = BitmapFactory.decodeByteArray(it, 0, it!!.size);
            item.picture = PicturePersistenceManager.save(bitmapPicture)
        }
        return item
    }

}