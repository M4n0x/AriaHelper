package ch.hearc.ariahelper.models.persistence

import android.content.Context
import ch.hearc.ariahelper.models.Item
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * This class is used to persists DM Items loot between app's launch
 * This class is working as a singleton
 * This is used by the DM loot only,
 */
object LootPersistenceManager {
    private const val PREFIX : String = "loot"
    private const val FILENAME : String = "loot"

    private val charsetUTF8 = Charsets.UTF_8
    private lateinit var lootDirectory : File

    private lateinit var loot : MutableList<Item>

    /**
     * init class look for folder loot
     */
    fun init(context: Context){
        val path = context.filesDir
        lootDirectory = File(path, PREFIX)
        lootDirectory.mkdirs()
        loadLoot()
    }

    /**
     * this load the loots that are stored in app intern app
     */
    private fun loadLoot(){
        val file = File(lootDirectory, FILENAME)
        loot = if(file.exists()){
            Json.decodeFromString(file.readText(charsetUTF8)) // unserialize list of item into loot
        } else {
            mutableListOf() // if file doesn't exist we just create an empty list
        }
    }

    /**
     * This function is a public to get the list of items available
     * @return MutableList<Item>
     */
    fun getLoot() :  MutableList<Item>{
        return loot
    }


    /**
     * This function is used to save the loot in the directory
     * It should be call on whenever we want to save data, especially when app goes on pause
     */
    fun save(){
        val file = File(lootDirectory, FILENAME)
        val string = Json.encodeToString(loot)
        file.writeText(string)
    }
}