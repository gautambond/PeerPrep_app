package com.bond.peerprep.courses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bond.peerprep.R

class SubjectCardFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_SUBTITLE = "subtitle"
        private const val ARG_POINT1 = "point1"
        private const val ARG_POINT2 = "point2"
        private const val ARG_POINT3 = "point3"
        private const val ARG_IMAGE = "image"
        private const val ARG_BUTTON = "button"

        // ✅ Call this to create the fragment with data
        fun newInstance(
            title: String,
            subtitle: String,
            point1: String,
            point2: String,
            point3: String,
            image: Int,
            buttonText: String = "Book a FREE trial class →"
        ): SubjectCardFragment {
            val fragment = SubjectCardFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_SUBTITLE, subtitle)
            args.putString(ARG_POINT1, point1)
            args.putString(ARG_POINT2, point2)
            args.putString(ARG_POINT3, point3)
            args.putInt(ARG_IMAGE, image)
            args.putString(ARG_BUTTON, buttonText)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_subject_card, container, false)

        // Get data from arguments
        val title    = arguments?.getString(ARG_TITLE) ?: ""
        val subtitle = arguments?.getString(ARG_SUBTITLE) ?: ""
        val point1   = arguments?.getString(ARG_POINT1) ?: ""
        val point2   = arguments?.getString(ARG_POINT2) ?: ""
        val point3   = arguments?.getString(ARG_POINT3) ?: ""
        val image    = arguments?.getInt(ARG_IMAGE) ?: R.drawable.logo2
        val button   = arguments?.getString(ARG_BUTTON) ?: ""

        // Set data to views
        view.findViewById<TextView>(R.id.card_title).text = title
        view.findViewById<TextView>(R.id.card_subtitle).text = subtitle
        view.findViewById<TextView>(R.id.card_point1).text = point1
        view.findViewById<TextView>(R.id.card_point2).text = point2
        view.findViewById<TextView>(R.id.card_point3).text = point3
        view.findViewById<ImageView>(R.id.card_image).setImageResource(image)
        view.findViewById<Button>(R.id.card_button).text = button

        // Button click
        view.findViewById<Button>(R.id.card_button).setOnClickListener {
            val navHostFragment = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)
            navHostFragment?.findNavController()?.navigate(R.id.contactFragment)
        }

        return view
    }
}