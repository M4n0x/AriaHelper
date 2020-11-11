package ch.hearc.ariahelper.models.persistence

import android.content.Context
import android.util.Log
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.Item
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


/**
 * This class is used to persists DM Items loot between app's launch
 * This class is working as a singleton
 */
object LootPersistenceManager {
    private const val PREFIX : String = "loot"
    private const val FILENAME : String = "loot"

    private val charsetUTF8 = Charsets.UTF_8
    private lateinit var lootDirectory : File

    private lateinit var loot : MutableList<Item>

    public fun init(context: Context){
        val path = context.filesDir
        lootDirectory = File(path, PREFIX)
        lootDirectory.mkdirs()
        LootPersistenceManager.loadLoot()
    }

    private fun loadLoot(){
        val file = File(lootDirectory, FILENAME)
        loot = if(file.exists()){
            Json.decodeFromString<MutableList<Item>>(file.readText(LootPersistenceManager.charsetUTF8))
        } else {
            mutableListOf<Item>()
        }
    }

    public fun getLoot() :  MutableList<Item>{
        return loot
    }

    public fun save(){
        val file = File(lootDirectory, FILENAME)
        val string = Json.encodeToString(loot)
        file.writeText(string)
    }
}