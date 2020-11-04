package ch.hearc.ariahelper.ui.fragments

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.MoneyValue

class MoneyValueFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
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
        super.onViewCreated(view, savedInstanceState)
        goldEditText = view?.findViewById<EditText>(R.id.goldTextEdit)!!
        silverEditText = view?.findViewById<EditText>(R.id.silverTextEdit)!!
        bronzeEditText = view?.findViewById<EditText>(R.id.bronzeTextEdit)!!
    }

    override fun onResume() {
        //get textedit views
        goldEditText= view?.findViewById<EditText>(R.id.goldTextEdit)!!
        silverEditText =view?.findViewById<EditText>(R.id.silverTextEdit)!!
        bronzeEditText =view?.findViewById<EditText>(R.id.bronzeTextEdit)!!
        super.onResume()
    }

    public fun displayMoney(moneyValue: MoneyValue){
        goldEditText.setText(moneyValue.gold.toString())
        silverEditText.setText(moneyValue.silver.toString())
        bronzeEditText.setText(moneyValue.bronze.toString())
    }

    public fun unLink(){
        if(goldWatcher != null){
            goldEditText.removeTextChangedListener(goldWatcher)
            silverEditText.removeTextChangedListener(silverWatcher)
            bronzeEditText.removeTextChangedListener(bronzeWatcher)
        }
    }

    public fun linkToPlayer(character : Character){
        unLink()
        displayMoney(character.money)
        goldWatcher = goldEditText.doAfterTextChanged {
            if(it != null && !it.isEmpty())
                character.money.gold = it.toString()?.toInt()
        }
        silverWatcher = silverEditText.doAfterTextChanged {
            if(it !=null && !it.isEmpty())
                character.money.silver = it.toString()?.toInt()
        }
        bronzeWatcher = bronzeEditText.doAfterTextChanged {
            if(it !=null && !it.isEmpty())
                character.money.bronze = it.toString()?.toInt()
        }
    }
}