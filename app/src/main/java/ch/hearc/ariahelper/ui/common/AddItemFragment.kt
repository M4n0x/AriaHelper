package ch.hearc.ariahelper.ui.common

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.QUALITY
import ch.hearc.ariahelper.models.persistence.PicturePersistenceManager
import ch.hearc.ariahelper.ui.loot.dm.LootViewModel
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [AddItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddItemFragment : Fragment() {
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    // var path used to store the store the file name of the newly stored image
    private var path : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)

        //Show a list of quality based on enum [QUALITY]
        view.inputQuality.adapter = ArrayAdapter<QUALITY>(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            QUALITY.values()
        )

        //on click on image we call intent gallery to choose an image
        view.itemImg.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, RESULT_GALLERY)
        }

        //on btn add click we go back to last
        view.btnSubmit.setOnClickListener{
            //before going back we add the new item to the itemList
            // we doesn't know whatever is behind itemList (it can be player's loot or dm)
            lootViewModel.itemList.value!!.add(
                Item(
                    view.inputName.text.toString(),
                    view.inputDescription.text.toString(),
                    view.inputQuality.selectedItemId.toInt(),
                    path.toString()
                )
            )
            // view.findNavController().navigate(R.id.action_fragmentAddItem_to_nav_lootdm) previous nav, not agnotic so replace by the line below
            parentFragmentManager.popBackStack();
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RESULT_GALLERY -> if (data != null ) {
                //On gallery result, we save the picture in our app intern data
                path = data.data?.let { PicturePersistenceManager.save(it) }

                //As the picture is saved we load the freshly saved image in the bitmap picture
                itemImg.setImageBitmap(path?.let {
                    PicturePersistenceManager.getBitmapFromFilename(
                        it
                    )
                })

            }
        }
    }

    companion object {
        const val RESULT_GALLERY = 0
    }

}