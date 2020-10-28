package ch.hearc.ariahelper.ui.character

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.commonpool.AttributeBasicPool
import kotlinx.android.synthetic.main.fragment_character_view.*
import ch.hearc.ariahelper.models.Character


/**
 * A simple [Fragment] subclass.
 * Use the [CharacterViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterViewFragment : Fragment() {
    private lateinit var attributeAdapter : AttributeRecViewAdapter
    private lateinit var skillAdapter : SkillRecViewAdapter
    private lateinit var dummyCharacter : Character

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //dummy for test : Retrieve from bundle later
        dummyCharacter = Character("Jeanne")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //set adapter to attribute RV
        attributeAdapter = AttributeRecViewAdapter(dummyCharacter.attributeList)
        attributesRecyclerView!!.adapter = attributeAdapter

        //set adapter to skill RV
        skillAdapter = SkillRecViewAdapter(dummyCharacter.skillList)
        skillsRecyclerView!!.adapter = skillAdapter

        //TODO change later - put progress as WIP
        diceProgressBar.setProgress(10, true)

        //put characters in spinner
        //TODO link data of all characters here
        val characterNames = arrayListOf("Jeanne D'arc", "Grand-Jean", "Atlan")

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, characterNames)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //TODO change character here
                Log.i("CHARACTER CHANGED", "onItemSelected: " + characterNames[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_view, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CharacterViewFragment.
         */
        @JvmStatic
        fun newInstance() =
            CharacterViewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}