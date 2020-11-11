package ch.hearc.ariahelper.ui.common

import android.R.attr
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
        //defaultViewModelProviderFactory or the ViewModelProvider.Factory you are using.
        defaultViewModelProviderFactory
    }

    private var path : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)

        view.inputQuality.adapter = ArrayAdapter<QUALITY>(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            QUALITY.values()
        )

        view.itemImg.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, RESULT_GALLERY)
        }

        view.btnSubmit.setOnClickListener{
            lootViewModel.itemList.value!!.add(
                Item(
                    view.inputName.text.toString(),
                    view.inputDescription.text.toString(),
                    view.inputQuality.selectedItemId.toInt(),
                    path.toString()
                )
            )
            view.findNavController().navigate(R.id.action_fragmentAddItem_to_nav_lootdm)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RESULT_GALLERY -> if (null != attr.data) {
                if (data != null ) {
                    path = data.data?.let { PicturePersistenceManager.save(it) }

                    itemImg.setImageBitmap(path?.let {
                        PicturePersistenceManager.getBitmapFromFilename(
                            it
                        )
                    })
                }
            }
        }
    }

    companion object {
        const val RESULT_GALLERY = 0
    }

}