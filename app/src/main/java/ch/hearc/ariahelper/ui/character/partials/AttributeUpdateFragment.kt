package ch.hearc.ariahelper.ui.character.partials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_attribute_update.*


class AttributeUpdateFragment : Fragment() {
    private val args: AttributeUpdateFragmentArgs by navArgs()
    private val characterViewModel : CharacterViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private lateinit var attribute : Attribute

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val position = args.position
        if(position >= 0){
            attribute = characterViewModel.character.value!!.attributeList[position]
        } else {
            attribute = Attribute("Empty", 0)
            characterViewModel._character.value!!.attributeList.add(attribute)
        }
        return inflater.inflate(R.layout.fragment_attribute_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEdit.setText(attribute.name)
        valueEdit.setText(attribute.value.toString())

        buttonConfirm.setOnClickListener {
            attribute.name = nameEdit.text.toString()
            attribute.value = valueEdit.text.toString().toInt()
            findNavController().navigateUp()
        }

        buttonDelete.setOnClickListener {
            characterViewModel._character.value!!.attributeList.remove(attribute)
            findNavController().navigateUp()
        }
    }

}