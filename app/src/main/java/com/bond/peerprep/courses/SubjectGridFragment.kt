package com.bond.peerprep.courses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bond.peerprep.R
import com.google.android.material.imageview.ShapeableImageView

class SubjectGridFragment : Fragment() {

    companion object {
        fun newInstance(
            title: String,
            subtitle: String,
            card1Title: String, card1Desc: String, card1Image: Int,
            card2Title: String, card2Desc: String, card2Image: Int,
            card3Title: String, card3Desc: String, card3Image: Int
        ): SubjectGridFragment {
            val fragment = SubjectGridFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("subtitle", subtitle)
            args.putString("card1Title", card1Title)
            args.putString("card1Desc", card1Desc)
            args.putInt("card1Image", card1Image)
            args.putString("card2Title", card2Title)
            args.putString("card2Desc", card2Desc)
            args.putInt("card2Image", card2Image)
            args.putString("card3Title", card3Title)
            args.putString("card3Desc", card3Desc)
            args.putInt("card3Image", card3Image)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_subject_grid, container, false)

        // Set title and subtitle
        view.findViewById<TextView>(R.id.grid_title).text = arguments?.getString("title")
        view.findViewById<TextView>(R.id.grid_subtitle).text = arguments?.getString("subtitle")

        // Card 1
        view.findViewById<TextView>(R.id.card1_title).text = arguments?.getString("card1Title")
        view.findViewById<TextView>(R.id.card1_desc).text = arguments?.getString("card1Desc")
        view.findViewById<ShapeableImageView>(R.id.card1_image).setImageResource(arguments?.getInt("card1Image") ?: R.drawable.logo2)

        // Card 2
        view.findViewById<TextView>(R.id.card2_title).text = arguments?.getString("card2Title")
        view.findViewById<TextView>(R.id.card2_desc).text = arguments?.getString("card2Desc")
        view.findViewById<ShapeableImageView>(R.id.card2_image).setImageResource(arguments?.getInt("card2Image") ?: R.drawable.logo2)

        // Card 3
        view.findViewById<TextView>(R.id.card3_title).text = arguments?.getString("card3Title")
        view.findViewById<TextView>(R.id.card3_desc).text = arguments?.getString("card3Desc")
        view.findViewById<ShapeableImageView>(R.id.card3_image).setImageResource(arguments?.getInt("card3Image") ?: R.drawable.logo2)

        return view
    }
}