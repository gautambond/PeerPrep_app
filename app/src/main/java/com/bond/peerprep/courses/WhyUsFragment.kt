package com.bond.peerprep.courses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bond.peerprep.R

class WhyUsFragment : Fragment() {

    companion object {
        fun newInstance(
            title: String,
            text1: String, icon1: Int,
            text2: String, icon2: Int,
            text3: String, icon3: Int,
            text4: String, icon4: Int,
            text5: String, icon5: Int,
            text6: String, icon6: Int
        ): WhyUsFragment {
            val fragment = WhyUsFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("text1", text1); args.putInt("icon1", icon1)
            args.putString("text2", text2); args.putInt("icon2", icon2)
            args.putString("text3", text3); args.putInt("icon3", icon3)
            args.putString("text4", text4); args.putInt("icon4", icon4)
            args.putString("text5", text5); args.putInt("icon5", icon5)
            args.putString("text6", text6); args.putInt("icon6", icon6)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_why_us, container, false)

        view.findViewById<TextView>(R.id.why_title).text = arguments?.getString("title")

        view.findViewById<TextView>(R.id.text1).text = arguments?.getString("text1")
        view.findViewById<TextView>(R.id.text2).text = arguments?.getString("text2")
        view.findViewById<TextView>(R.id.text3).text = arguments?.getString("text3")
        view.findViewById<TextView>(R.id.text4).text = arguments?.getString("text4")
        view.findViewById<TextView>(R.id.text5).text = arguments?.getString("text5")
        view.findViewById<TextView>(R.id.text6).text = arguments?.getString("text6")

        view.findViewById<ImageView>(R.id.icon1).setImageResource(arguments?.getInt("icon1") ?: R.drawable.ic_star)
        view.findViewById<ImageView>(R.id.icon2).setImageResource(arguments?.getInt("icon2") ?: R.drawable.ic_star)
        view.findViewById<ImageView>(R.id.icon3).setImageResource(arguments?.getInt("icon3") ?: R.drawable.ic_star)
        view.findViewById<ImageView>(R.id.icon4).setImageResource(arguments?.getInt("icon4") ?: R.drawable.ic_star)
        view.findViewById<ImageView>(R.id.icon5).setImageResource(arguments?.getInt("icon5") ?: R.drawable.ic_star)
        view.findViewById<ImageView>(R.id.icon6).setImageResource(arguments?.getInt("icon6") ?: R.drawable.ic_star)

        return view
    }
}