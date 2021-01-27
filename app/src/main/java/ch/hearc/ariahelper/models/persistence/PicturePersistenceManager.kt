package ch.hearc.ariahelper.models.persistence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * This class is used to persist and retrieve Pictures
 * This class is working as a singleton
 */
object PicturePersistenceManager {
    private const val PREFIX : String = "pictures"

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
    fun getBitmapFromFilename(filename: String) : Bitmap? {
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
        return save(bitmap)
    }

    /**
     * This function is used to save any picture
     * and store it in the app intern data
     * @param bitmap picture we want to save in intern app data
     * @param resize resize image to 512 and reduce image quality
     * @return  return the file's id newly stored in app
     */
    fun save(bitmap: Bitmap, resize: Boolean = true) : String{
        val filename : String = UUID.randomUUID().toString() + ".png"
        val file = File(picturesDir, filename)

        var image = bitmap

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            // Use the compress method on the BitMap object to write image to the OutputStream
            if (resize) {
                val nh = (bitmap.height * (512.0 / bitmap.width)).toInt()
                image = bitmap.scale(512, nh, true)
            }
            image.compress(Bitmap.CompressFormat.PNG, 60, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close() // close file in any case
            } catch (e: IOException) {
                e.printStackTrace() // handle if file doesn't exist
            }
        }

        return filename
    }

}