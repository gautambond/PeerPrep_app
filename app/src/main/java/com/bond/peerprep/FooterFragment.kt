package com.bond.peerprep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class FooterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)

        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment?.findNavController()

        // Quick Links
        view.findViewById<TextView>(R.id.footer_home).setOnClickListener {
            navController?.navigate(R.id.homeFragment)
        }
        view.findViewById<TextView>(R.id.footer_offers).setOnClickListener {
            navController?.navigate(R.id.homeFragment)
        }
        view.findViewById<TextView>(R.id.footer_consultation).setOnClickListener {
            navController?.navigate(R.id.contactFragment)
        }

        // Explore
        view.findViewById<TextView>(R.id.footer_about).setOnClickListener {
            navController?.navigate(R.id.aboutFragment)
        }
        view.findViewById<TextView>(R.id.footer_terms).setOnClickListener {
            navController?.navigate(R.id.aboutFragment)
        }

        return view
    }
}
