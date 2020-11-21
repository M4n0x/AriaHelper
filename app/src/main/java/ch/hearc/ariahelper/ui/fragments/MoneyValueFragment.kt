package ch.hearc.ariahelper.ui.fragments

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.MoneyValue

/**
 * Fragment that displays a money value
 * Comes with helper functions
 */
class MoneyValueFragment : Fragment() {
    // attributes
    lateinit var bronzeEditText : EditText
    lateinit var silverEditText : EditText
    lateinit var goldEditText : EditText
    var bronzeWatcher : TextWatcher ? = null
    var silverWatcher : TextWatcher ? = null
    var goldWatcher : TextWatcher ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_money_value, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //get textedit views references once
        goldEditText = view?.findViewById<EditText>(R.id.goldTextEdit)!!
        silverEditText = view?.findViewById<EditText>(R.id.silverTextEdit)!!
        bronzeEditText = view?.findViewById<EditText>(R.id.bronzeTextEdit)!!
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        //get textedit views references (once)
        goldEditText   = view?.findViewById<EditText>(R.id.goldTextEdit)!!
        silverEditText = view?.findViewById<EditText>(R.id.silverTextEdit)!!
        bronzeEditText = view?.findViewById<EditText>(R.id.bronzeTextEdit)!!
        super.onResume()
    }

    /**
     * Helper function - display the given money value
     * @param monevalue MoneyValue to display
     */
    fun displayMoney(moneyValue: MoneyValue){
        //update views with values of money
        goldEditText.setText(moneyValue.gold.toString())
        silverEditText.setText(moneyValue.silver.toString())
        bronzeEditText.setText(moneyValue.bronze.toString())
    }

    /**
     * Helper function - unlink this fragment from its character (stop listener)
     */
    fun unLink(){
        if(goldWatcher != null){
            goldEditText.removeTextChangedListener(goldWatcher)
            silverEditText.removeTextChangedListener(silverWatcher)
            bronzeEditText.removeTextChangedListener(bronzeWatcher)
        }
    }

    /**
     * Link a player to this fragment's text edit - synchronizing their money with the charcter's money
     * @param character character to link
     */
    fun linkToPlayer(character : Character){
        //unlink if a link is already present to avoid potential errors (double links)
        unLink()
        displayMoney(character.money)

        // --- Listeners - **safely** links the textedits to the character's money ---
        goldWatcher = goldEditText.doAfterTextChanged {
            if(it != null && !it.isEmpty()){
                try {
                    //try to parse and change the value of the character money
                    character.money.gold = it.toString()?.toInt()
                } catch (e : NumberFormatException){
                    //not parsable : go back
                    goldEditText.setText(character.money.gold.toString())
                }
            }
        }

        silverWatcher = silverEditText.doAfterTextChanged {
            if(it != null && !it.isEmpty()){
                try {
                    //try to parse and change the value of the character money
                    character.money.silver = it.toString()?.toInt()
                } catch (e : NumberFormatException){
                    //not parsable : go back
                    silverEditText.setText(character.money.silver.toString())
                }
            }
        }

        bronzeWatcher = bronzeEditText.doAfterTextChanged {
            if(it != null && !it.isEmpty()){
                try {
                    //try to parse and change the value of the character money
                    character.money.bronze = it.toString()?.toInt()
                } catch (e : NumberFormatException){
                    //not parsable : go back
                    bronzeEditText.setText(character.money.bronze.toString())
                }
            }
        }
    }

}