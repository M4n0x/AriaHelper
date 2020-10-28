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

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    companion object Default : Random()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        Thread(
            Runnable{
            var i = 1;
            while(i != 0) {
                Thread.sleep((500).toLong())
                i = (0..20).random()
                galleryViewModel._text.postValue("dice :" + i.toString())
            }

            galleryViewModel._text.postValue("LOOSER")
        }).start()
    //debile putain

        return root
    }
}