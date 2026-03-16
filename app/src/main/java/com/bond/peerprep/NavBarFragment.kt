package com.bond.peerprep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NavBarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_navbar, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        setHasOptionsMenu(true)
        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            val navHostFragment = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)

            val navController = navHostFragment?.findNavController()
                ?: return super.onOptionsItemSelected(item)

            when (item.itemId) {
                R.id.menu_home -> navController.navigate(R.id.homeFragment)
                R.id.menu_about -> navController.navigate(R.id.aboutFragment)
                R.id.menu_contact -> navController.navigate(R.id.contactFragment)
                R.id.menu_login -> navController.navigate(R.id.loginFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }
}