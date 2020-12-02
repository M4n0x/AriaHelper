package ch.hearc.ariahelper.ui.common

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.ariahelper.R
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.QUALITY
import ch.hearc.ariahelper.models.persistence.PicturePersistenceManager
import ch.hearc.ariahelper.ui.loot.dm.LootViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_add_or_update_item.*
import kotlinx.android.synthetic.main.fragment_add_or_update_item.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [CreateOrUpdateItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateOrUpdateItemFragment : Fragment() {
    private val lootViewModel : LootViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }

    // var path used to store the store the file name of the newly stored image
    private var path : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_or_update_item, container, false)
        val viewHolder = ViewHolder(view)

        //Show a list of quality based on enum [QUALITY]
        view.inputQuality.adapter = ArrayAdapter<QUALITY>(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            QUALITY.values()
        )

        val position : Int? = arguments?.getInt("position")
        val item : Item = if (position != null) lootViewModel.itemList.value!![position] else Item("","", 0, "")

        path = item.picture

        //if item not empty we populate data in the viewHolder
        with(viewHolder) {
            titleView.setText(item.name)
            descriptionView.setText(item.description)
            if (item.picture != null && item.picture != "")
                imageView.setImageBitmap(PicturePersistenceManager.getBitmapFromFilename(item.picture))
            qualityView.setSelection(item.quality)
        }

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

            //we create our new item
            val nItem = Item(
                viewHolder.titleView.text.toString(),
                viewHolder.descriptionView.text.toString(),
                viewHolder.qualityView.selectedItemId.toInt(),
                path.toString()
            )

            if (position == null) { //if no position item is new, so we added it to the list
                lootViewModel.itemList.value!!.add(nItem)
            } else { //else the position is not null we are editing the item
                lootViewModel.itemList.value!![position] = nItem // and we just replace it in the list
            }

            // view.findNavController().navigate(R.id.action_fragmentAddItem_to_nav_lootdm) previous nav, not agnotic so replace by the line below
            findNavController().navigateUp()
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
                    PicturePersistenceManager.getBitmapFromFilename(it)
                })
            }
        }
    }

    companion object {
        const val RESULT_GALLERY = 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextInputEditText = view.inputNameEdit
        val qualityView: Spinner = view.inputQuality
        val descriptionView: TextInputEditText = view.inputDescriptionEdit
        val imageView: ImageView = view.itemImg

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }

}