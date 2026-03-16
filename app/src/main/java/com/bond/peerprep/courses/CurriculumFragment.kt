package com.bond.peerprep.courses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bond.peerprep.R

data class CurriculumItem(
    val subject: String,
    val desc: String,
    val image: Int
)

class CurriculumFragment : Fragment() {

    companion object {
        fun newInstance(
            title: String,
            items: ArrayList<CurriculumItem>
        ): CurriculumFragment {
            val fragment = CurriculumFragment()
            val args = Bundle()
            args.putString("title", title)
            // Pass items as parallel arrays
            args.putStringArray("subjects", items.map { it.subject }.toTypedArray())
            args.putStringArray("descs", items.map { it.desc }.toTypedArray())
            args.putIntArray("images", items.map { it.image }.toIntArray())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_curriculum, container, false)

        // Set title
        view.findViewById<TextView>(R.id.curriculum_title).text =
            arguments?.getString("title")

        // Get data
        val subjects = arguments?.getStringArray("subjects") ?: return view
        val descs    = arguments?.getStringArray("descs") ?: return view
        val images   = arguments?.getIntArray("images") ?: return view

        // Get container
        val container = view.findViewById<LinearLayout>(R.id.curriculum_container)

        // Inflate each card
        for (i in subjects.indices) {
            val cardView = inflater.inflate(
                R.layout.item_curriculum, container, false
            )

            cardView.findViewById<TextView>(R.id.curriculum_subject).text = subjects[i]
            cardView.findViewById<TextView>(R.id.curriculum_desc).text = descs[i]
            cardView.findViewById<ImageView>(R.id.curriculum_image).setImageResource(images[i])

            container.addView(cardView)
        }

        return view
    }
}