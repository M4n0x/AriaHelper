package ch.hearc.ariahelper.models.persistence

import android.content.Context
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.CharacterIdSpinnerContainer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

//object : singleton
object CharacterPersistenceManager {
    private const val PREFIX : String = "Characters"
    private const val LASTCHARPREFIX : String = "lastSelected"
    private const val CHARAC_LIMIT = 1000
    private val charsetUTF8 = Charsets.UTF_8
    private lateinit var characterDirectory : File
    private lateinit var lastSelectedDirectory : File

    private var lastSelectedCharacterId : Int? = null
    private var characters : MutableList<Character> = ArrayList<Character>()

    public fun init(context: Context){
        val path = context.filesDir
        characterDirectory = File(path, PREFIX)
        characterDirectory.mkdirs()
        lastSelectedDirectory = File(path, LASTCHARPREFIX)
        lastSelectedDirectory.mkdirs()
        loadAllCharacters()
    }

    private fun loadAllCharacters(){
        characters.clear()
        characterDirectory.listFiles()?.forEach {
            val character = Json.decodeFromString<Character>(it.readText(charsetUTF8))
            characters.add(character)
        }
        if(characters.isEmpty()){
            val character = Character("Premier personnage") as Character
            registerCharacter(character)
            lastSelectedCharacterId = character.id
        } else {
            val file = File(lastSelectedDirectory, LASTCHARPREFIX)
            if(file.exists())
                lastSelectedCharacterId = file.readText().toInt()
            else
                lastSelectedCharacterId = characters.first().id
        }
    }

    public fun saveAllCharacter(){
        characters.forEach{
            saveCharacter(it)
        }
        val fileLastSelected = File(lastSelectedDirectory, LASTCHARPREFIX)
        fileLastSelected.writeText(lastSelectedCharacterId.toString())
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
        lastSelectedCharacterId = character.id
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

    public fun getLastCharacter() : Character {
        var character : Character? = null
        lastSelectedCharacterId = lastSelectedCharacterID()
        try{
            character = getCharacterByID(lastSelectedCharacterId!!)
        } catch(e : Exception){
            character = characters.first()
            lastSelectedCharacterId = character.id
        }
        return character!!
    }

    public fun getAllCharacterNames() : ArrayList<String> {
        val names = ArrayList<String>()
        characters.forEach{ names.add(it.name) }
        return names
    }

    public fun getAllCharacterNamesId() : ArrayList<CharacterIdSpinnerContainer> {
        val namedId = ArrayList<CharacterIdSpinnerContainer>()
        characters.forEach{ namedId.add(CharacterIdSpinnerContainer(it.name, it.id!!)) }
        return namedId
    }

    public fun getCharacterByID(id: Int) : Character? {
        characters.forEach{
            if(it.id == id){
                lastSelectedCharacterId = it.id
                return it
            }
        }
        throw Exception("Character doesnt exist")
    }

    public fun lastSelectedCharacterID() : Int{ return lastSelectedCharacterId ?: characters.first().id!! }

    public fun deleteCharacterById(id : Int){
        //safety
        if(characters.size == 1)
            return
        val charToDelete = getCharacterByID(id)
        if(charToDelete?.id == lastSelectedCharacterId){
            lastSelectedCharacterId = null
        }
        characters.remove(charToDelete)
        File(characterDirectory, charToDelete!!.id.toString()).delete()
    }

}