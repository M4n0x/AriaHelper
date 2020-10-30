package ch.hearc.ariahelper.models

import android.content.Context
import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path

//object : singleton
object CharacterPersistenceManager {
    private val PREFIX : String = "Characters"
    private val LASTCHARPREFIX : String = "$PREFIX/lastSelected"
    private val charsetUTF8 = Charsets.UTF_8
    private val CHARAC_LIMIT = 1000
    private lateinit var characterDirectory : File

    private var lastSelectedCharacter : Int? = null
    private var characters : MutableList<Character> = ArrayList<Character>()

    public fun init(context: Context){
        val path = context.filesDir
        characterDirectory = File(path, PREFIX)
        characterDirectory.mkdirs()
        loadAllCharacters()
    }

    private fun loadAllCharacters(){
        characterDirectory.list()?.forEach {
            val file = File(characterDirectory, it)
            val character = Json.decodeFromString<Character>(file.readText(charsetUTF8))
            characters.add(character)
        }
        if(characters.isEmpty()){
            val character = Character("Premier personnage") as Character
            registerCharacter(character)
            lastSelectedCharacter = character.id
        } else {
            val file = File(characterDirectory, LASTCHARPREFIX)
            if(file.exists())
                lastSelectedCharacter = file.readText().toInt()
            else
                lastSelectedCharacter = null
        }
    }

    public fun saveAllCharacter(){
        characters.forEach{
            saveCharacter(it)
        }
        val file = File(characterDirectory, LASTCHARPREFIX)
        file.writeText(lastSelectedCharacter.toString())
    }

    private fun saveCharacter(char : Character){
        val file = File(characterDirectory, char.id.toString())
        val string = Json.encodeToString(char)
        file.writeText(string)
    }

    public fun registerCharacter(character: Character){
        if(character.id == null){
            character.id = getFreeId()
            //save the character to reserve the file
            saveCharacter(character)
        }
        characters.add(character)
    }

    private fun getFreeId() : Int {
        var i = 0
        while(i < CHARAC_LIMIT){
            if(!File(characterDirectory, i.toString()).exists()){
                return i
            }
            i++
        }
        throw Exception("ID limit reached")
    }

    public fun getLastCharacter() : Character{
        val character : Character ?
        if(lastSelectedCharacter == null){
            character = characters.first()
        } else {
            character = characters[lastSelectedCharacter!!] ?: characters.first()
        }
        return character!!
    }

    public fun getAllCharacterNames() : ArrayList<String> {
        val names = ArrayList<String>()
        characters.forEach{ names.add(it.name) }
        return names
    }

    public fun getAllCharacterNamesOrderedById() : ArrayList<String> {
        val names = ArrayList<String>()
        characters.forEach{ names.add(it.id!!, it.name) }
        return names
    }


    public fun getCharacterByID(id: Int) : Character? {
        characters.forEach{
            if(it.id == id){
                lastSelectedCharacter = characters.indexOf(it)
                return it
            }
        }
        throw Exception("Character doesnt exist")
    }

    public fun getCharacterByPosition(position: Int) : Character {
        lastSelectedCharacter = position
        return characters[position]
    }

    public fun deleteCharacterById(id : Int){
        val charToDelete = getCharacterByID(id)
        characters.remove(charToDelete)
        File(characterDirectory, charToDelete!!.id.toString()).delete()
    }

}