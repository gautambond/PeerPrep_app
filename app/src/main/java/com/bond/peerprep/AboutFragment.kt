package com.bond.peerprep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view?.findViewById<MaterialButton>(R.id.fab_book)?.setOnClickListener {
            findNavController().navigate(R.id.bookDemoFragment)
        }
        return inflater.inflate(R.layout.fragment_about, container, false)

    }
}