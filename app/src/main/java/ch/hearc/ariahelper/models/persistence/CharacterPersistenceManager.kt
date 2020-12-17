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
    // --- attributes ---
    //const helpers
    private const val PREFIX : String = "Characters"
    private const val LASTCHARPREFIX : String = "lastSelected"
    private const val CHARAC_LIMIT = 1000
    private val charsetUTF8 = Charsets.UTF_8
    //lateinit files init from the smartphone internal storage
    private lateinit var characterDirectory : File
    private lateinit var lastSelectedDirectory : File
    //logistic var
    private var lastSelectedCharacterId : Int? = null
    private var characters : MutableList<Character> = ArrayList<Character>()

    /**
     * Init the persistence manager with a context - necessary to access the internal storage
     * @param context - context of the activity, to acess the smartphone
     */
    public fun init(context: Context){
        val path = context.filesDir
        characterDirectory = File(path, PREFIX)
        characterDirectory.mkdirs()
        lastSelectedDirectory = File(path, LASTCHARPREFIX)
        lastSelectedDirectory.mkdirs()
        loadAllCharacters()
    }

    /**
     * Load all characters from the files into the character list
     */
    private fun loadAllCharacters(){
        characters.clear()
        //iter over the directory, decode each character
        characterDirectory.listFiles()?.forEach {
            val character = Json.decodeFromString<Character>(it.readText(charsetUTF8))
            characters.add(character)
        }
        //search the last character's ID to stay on the same on at app close & start
        lastSelectedCharacterId = if(characters.isEmpty()){
            //there is no character : Create one as to never be empty
            val character = Character("Premier personnage")
            registerCharacter(character)
            character.id
        } else {
            val file = File(lastSelectedDirectory, LASTCHARPREFIX)
            if(file.exists())
                //try to read the serialized last character ID
                file.readText().toInt()
            else
                //nothing serialized - take the first one
                characters.first().id
        }
    }

    /**
     * Save all characters from character list to files in memory
     */
    public fun saveAllCharacter(){
        characters.forEach{
            saveCharacter(it)
        }
        val fileLastSelected = File(lastSelectedDirectory, LASTCHARPREFIX)
        fileLastSelected.writeText(lastSelectedCharacterId.toString())
    }

    /**
     * Save the current character to its file (characters/ID/char.txt as JSON)
     */
    private fun saveCharacter(char : Character){
        val file = File(characterDirectory, char.id.toString())
        val string = Json.encodeToString(char)
        file.writeText(string)
    }

    /**
     * Register the character into its internal character list
     * Assign a free ID if the character has no ID yet
     * @param character The character to register
     */
    public fun registerCharacter(character: Character){
        if(character.id == null){
            character.id = getFreeId()
            //save the character to reserve the file and ID
            saveCharacter(character)
        }
        lastSelectedCharacterId = character.id
        characters.add(character)
    }

    /**
     * Get the first free ID (Not taken by another registered character)
     */
    private fun getFreeId() : Int {
        var i = 0
        while(i < CHARAC_LIMIT){ //avoid infinite loop
            if(!File(characterDirectory, i.toString()).exists()){
                return i
            }
            i++
        }
        throw Exception("ID limit reached")
    }

    /**
     * Get the last selected character
     */
    public fun getLastCharacter() : Character {
        var character : Character?
        lastSelectedCharacterId = lastSelectedCharacterID()
        try{
            character = getCharacterByID(lastSelectedCharacterId!!)
        } catch(e : Exception){
            //maybe the character was deleted ?
            character = characters.first()
            lastSelectedCharacterId = character.id
        }
        return character!!
    }

    /**
     * Get all the names of the character
     * deprecated
     * Used for the spinners, problem with double names
     */
    fun getAllCharacterNames() : ArrayList<String> {
        val names = ArrayList<String>()
        characters.forEach{ names.add(it.name) }
        return names
    }

    /**
     * @return All the characters as containers (name, id)
     */
    fun getAllCharacterNamesId() : ArrayList<CharacterIdSpinnerContainer> {
        //CharacterIdSpinnerContainer : a simple pair (name, ID) that only shows the name in toString (ID is hidden)
        //ID is important to discriminate unique characters that have the same names
        val namedId = ArrayList<CharacterIdSpinnerContainer>()
        characters.forEach{ namedId.add(CharacterIdSpinnerContainer(it.name, it.id!!)) }
        return namedId
    }

    /**
     * Return the character in the registered characters list by its ID
     * @param id ID of the wanted character
     * @return character if exists, null otherwise
     */
    fun getCharacterByID(id: Int) : Character? {
        characters.forEach{
            if(it.id == id){
                lastSelectedCharacterId = it.id
                return it
            }
        }
        throw Exception("Character doesnt exist")
    }

    /**
     * Safely get the last selected char id
     */
    private fun lastSelectedCharacterID() : Int{ return lastSelectedCharacterId ?: characters.first().id!! }

    /**
     * Delete the character by the corresponding ID
     * Carefull : The character list can not be empty, deletion of the last character is forbidden
     * @param id ID of the character to delete
     */
    fun deleteCharacterById(id : Int){
        //safety - list should never be empty
        if(characters.size == 1)
            return
        val charToDelete = getCharacterByID(id)
        if(charToDelete?.id == lastSelectedCharacterId){
            lastSelectedCharacterId = null
        }
        //delete char in live memory
        characters.remove(charToDelete)
        //delete char in persistence (file)
        File(characterDirectory, charToDelete!!.id.toString()).delete()
    }

}