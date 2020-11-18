package ch.hearc.ariahelper.models.persistence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * This class is used to persists DM Items loot between app's launch
 * This class is working as a singleton
 */
object PicturePersistenceManager {
    private const val PREFIX : String = "pictures"

    private val charsetUTF8 = Charsets.UTF_8
    private lateinit var picturesDir : File

    private lateinit var context: Context

     fun init(context: Context){
        val path = context.filesDir
        picturesDir = File(path, PREFIX)
        picturesDir.mkdirs()
        this.context = context
    }

    /**
     * This function is used to retrieve an image from pictures
     * @param   filename  id filename used to retrieve image
     * @return      return a bitmap of the image if found
     */
    fun getBitmapFromFilename(filename: String) : Bitmap {
        val file = File(picturesDir, filename)
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    /**
     * This function is used to save the picture from the gallery
     * and store it in the app intern data
     * @param   uri uri of the file we want to save in intern app data
     * @return  return the file's id newly stored in app
     */
    fun save(uri: Uri) : String {
        val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val filename : String = UUID.randomUUID().toString() + ".png"
        val file = File(picturesDir, filename)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close() // close file in anycase
            } catch (e: IOException) {
                e.printStackTrace() // handle if file doesnt exist
            }
        }

        return filename
    }

    /**
     * TODO : create a function to delete/remove image
     */
}