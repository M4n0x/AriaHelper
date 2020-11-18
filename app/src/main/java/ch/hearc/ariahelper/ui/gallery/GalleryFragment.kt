package ch.hearc.ariahelper.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ch.hearc.ariahelper.R
import java.util.*


/**
 * This is a place holder for the loot of the user
 * It will be used as a skeleton to create the user's bag
 * So don't remove this class !!!
 * TODO Adapt this class to create user's bag loot
 */
class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    companion object Default : Random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel::class.java)

        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // This code is just used as a POC for thread creation and UI sync via ViewModel
        Thread(
            Runnable {
                var i = 1;
                while (i != 0) {
                    Thread.sleep((500).toLong())
                    i = (0..20).random()
                    galleryViewModel._text.postValue("dice :$i")
                }

                galleryViewModel._text.postValue("LOOSER")
            }).start()

        return root
    }
}