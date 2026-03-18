package com.bond.peerprep

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class NavBarFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserSection(view)
    }

    override fun onResume() {
        super.onResume()
        // ✅ Refresh user name every time navbar is visible
        view?.let { setupUserSection(it) }
    }

    // ✅ Setup user name and avatar in navbar
    private fun setupUserSection(view: View) {
        val prefs    = requireActivity().getSharedPreferences("PeerPrep",
            android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("userName", null)
        val userRole = prefs.getString("userRole", null)

        val userSection = view.findViewById<LinearLayout>(R.id.nav_user_section)
        val userNameTV  = view.findViewById<TextView>(R.id.nav_user_name)
        val userAvatar  = view.findViewById<TextView>(R.id.nav_user_avatar)

        if (userName != null) {
            // ✅ Logged in — show first name + avatar
            val firstName = userName.split(" ").firstOrNull() ?: userName
            userNameTV.text = firstName
            userAvatar.text = firstName.firstOrNull()?.uppercase() ?: "U"
            userAvatar.visibility = View.VISIBLE
        } else {
            // ✅ Not logged in — show Login
            userNameTV.text = "Login"
            userAvatar.visibility = View.GONE
        }

        // ✅ Click on user section
        userSection.setOnClickListener {
            if (userName != null) {
                // Logged in — show popup
                showUserPopup(view, userName, userRole ?: "")
            } else {
                // Not logged in — go to login
                val navHostFragment = requireActivity()
                    .supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment)
                navHostFragment?.findNavController()?.navigate(R.id.loginFragment)
            }
        }
    }

    // ✅ Show user popup menu
    private fun showUserPopup(anchorView: View, userName: String, userRole: String) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_user_menu, null)

        // Set user info in popup header
        popupView.findViewById<TextView>(R.id.popup_user_name).text = userName
        popupView.findViewById<TextView>(R.id.popup_user_role).text =
            when (userRole) {
                "student"    -> "🎓 Student"
                "instructor" -> "👨‍🏫 Instructor"
                "admin"      -> "👑 Admin"
                else         -> userRole
            }

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.elevation     = 10f
        popupWindow.isOutsideTouchable = true

        // Show below user section
        val userSection = anchorView.findViewById<LinearLayout>(R.id.nav_user_section)
        popupWindow.showAsDropDown(userSection, 0, 8)

        // Get navController
        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment?.findNavController()

        // ✅ Profile click
        popupView.findViewById<LinearLayout>(R.id.popup_profile).setOnClickListener {
            popupWindow.dismiss()
            navController?.navigate(R.id.profileFragment)
        }

        // ✅ Dashboard click
        popupView.findViewById<LinearLayout>(R.id.popup_dashboard).setOnClickListener {
            popupWindow.dismiss()
            val prefs = requireActivity().getSharedPreferences("PeerPrep",
                android.content.Context.MODE_PRIVATE)
            val role  = prefs.getString("userRole", "") ?: ""

            when (role) {
                "admin"      -> navController?.navigate(R.id.adminDashboardFragment)
                "instructor" -> navController?.navigate(R.id.instructorDashboardFragment)
                "student"    -> navController?.navigate(R.id.studentDashboardFragment)
            }
        }

        // ✅ Logout click
        popupView.findViewById<LinearLayout>(R.id.popup_logout).setOnClickListener {
            popupWindow.dismiss()

            // Clear SharedPreferences
            val prefs = requireActivity().getSharedPreferences("PeerPrep",
                android.content.Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            // Sign out Firebase
            auth.signOut()

            // Reset navbar
            view?.let { setupUserSection(it) }

            // Go to home
            navController?.navigate(R.id.homeFragment)
        }
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
                R.id.menu_home    -> navController.navigate(R.id.homeFragment)
                R.id.menu_about   -> navController.navigate(R.id.aboutFragment)
                R.id.menu_contact -> navController.navigate(R.id.contactFragment)

                // Courses
                R.id.maths   -> navController.navigate(R.id.mathsFragment)
                R.id.science -> navController.navigate(R.id.scienceFragment)
                R.id.coding  -> navController.navigate(R.id.codingFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }
}