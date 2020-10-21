package ch.hearc.ariahelper.ui.character

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Character
import ch.hearc.ariahelper.models.commonpool.AttributeBasicPool
import kotlinx.android.synthetic.main.fragment_character_view.*


/**
 * A simple [Fragment] subclass.
 * Use the [CharacterViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterViewFragment : Fragment() {
    private lateinit var attributeAdapter : AttributeRecViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // var dummyCharacter = Character("Jeanne")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attributeAdapter = AttributeRecViewAdapter(AttributeBasicPool.ATTRIBUTES)
        attributesRecyclerView!!.adapter = attributeAdapter
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
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CharacterViewFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CharacterViewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}